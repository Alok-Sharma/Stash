package aloksharma.ufl.edu.stash;

import android.content.Context;
import android.util.Log;

import com.parse.ParseUser;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Alok on 10/15/2015.
 */
public class PlaidHelper {

    Context context;
    PlaidHelper(Context context) {
        this.context = context;
        try {
            AesCbcWithIntegrity.SecretKeys keys = AesCbcWithIntegrity.generateKey();
            AesCbcWithIntegrity.CipherTextIvMac cipherTextIvMac = AesCbcWithIntegrity.encrypt("some test", keys);
            //store or send to server
            String ciphertextString = cipherTextIvMac.toString();
            String plainText = AesCbcWithIntegrity.decryptString(cipherTextIvMac, keys);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Double getBankBalance(String access_token) {
        List<NameValuePair> postArgs = new ArrayList();
        String plaidUrl = context.getString(R.string.plaid_url) + "/get";
        postArgs.add(new BasicNameValuePair("access_token", access_token));

        return plaidPostRequest(plaidUrl, postArgs);
    }

    Double getBankBalance(String username, String password, ServerAccess.BankName bankName) {
        List<NameValuePair> postArgs = new ArrayList();
        String plaidUrl = context.getString(R.string.plaid_url);
        postArgs.add(new BasicNameValuePair("username", username));
        postArgs.add(new BasicNameValuePair("password", password));
        postArgs.add(new BasicNameValuePair("type", bankName.toString()));
        return plaidPostRequest(plaidUrl, postArgs);
    }

    /**
     * Returns the Map of the Bank access tokens. This map has a key of the bank name
     * and the values are the encrypted access tokens.
     * @return Map<String, byte[]> of access tokens.
     */
    Map<String, String> getAccessTokenMap() {
        try {
            if (ParseUser.getCurrentUser() != null) {
                Map<String, String> accessTokens = ParseUser.getCurrentUser().getMap("BankMap");
                return accessTokens;
            }else{
                //current user is null. This shouldn't happen if the user was
                // logged in successfully.
                Log.d("StashLog", "current user was null");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Executes a POST to the plaid /auth endpoint, and fetches the users
     * bank balance.
     *
     * @param plaidUrl The Plaid url to send the request to
     * @param postArgs The POST arguments
     * @return Double Users Bank balance.
     */
    Double plaidPostRequest(String plaidUrl, List<NameValuePair> postArgs) {
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(plaidUrl);

        //Post Data
        postArgs.add(new BasicNameValuePair("client_id", context.getString(R.string
                .plaid_client_id)));
        postArgs.add(new BasicNameValuePair("secret", context.getString(R.string
                .plaid_secret)));

        //Encoding POST data
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(postArgs));
        } catch (UnsupportedEncodingException e) {
            // log exception
            e.printStackTrace();
        }

        //making POST request, and parse it.
        try {
            HttpResponse response = httpClient.execute(httpPost);
            String responseString = EntityUtils.toString(response.getEntity());
            JSONObject jObject = new JSONObject(responseString);
            Log.d("StashLog", "PLAID RESPONSE: " + responseString);

            //fetch the access_token and bank name and store it on parse.
            String access_token = jObject.getString("access_token"); //TODO: Alok, change to BankMap
            String bankName = jObject.getJSONArray("accounts").getJSONObject(0).getString("institution_type");

            //currentUser must not be null.
//            ParseUser.getCurrentUser().addUnique("access_tokens",
//                    access_token);

            ParseUser.getCurrentUser().pinInBackground();
            ParseUser.getCurrentUser().saveInBackground();

            Map<String, String> bankMap = getAccessTokenMap();
            if(bankMap == null) {
                bankMap = new HashMap<>();
            }
            bankMap.put(bankName, access_token);
            ParseUser.getCurrentUser().put("BankMap", bankMap);

            ParseUser.getCurrentUser().pinInBackground();
            ParseUser.getCurrentUser().saveInBackground();

            double balance = jObject.getJSONArray("accounts").getJSONObject
                    (0).getJSONObject("balance").getDouble("available");
            return balance;
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


}
