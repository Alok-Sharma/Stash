package aloksharma.ufl.edu.stash;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Alok on 10/15/2015.
 */
public class PlaidHelper {
    Context context;
    Gson gson = new Gson();
    SharedPreferences sharedPref;
    SharedPreferences.Editor sharedPrefEditor;

    PlaidHelper(Context context) {
        this.context = context;
        sharedPref = context.getSharedPreferences("keyStore", 0);
        sharedPrefEditor = sharedPref.edit();
    }

    AesCbcWithIntegrity.SecretKeys generateNewKeys() {
        Log.d("StashLog", "generating new keys");
        AesCbcWithIntegrity.SecretKeys keys = null;
        try {
            keys = AesCbcWithIntegrity.generateKey();
            String keysString = keys.toString();
            sharedPrefEditor.putString("keys", keysString);
            sharedPrefEditor.commit();
        } catch (Exception e) {
            Log.d("StashLog", "error in generate new keys: " + e.getMessage());
            e.printStackTrace();
        }
        return keys;
    }

    AesCbcWithIntegrity.SecretKeys fetchExistingKeys() {
        Log.d("StashLog", "fetching existing keys");
        try {
            String keysString = sharedPref.getString("keys", null);
            AesCbcWithIntegrity.SecretKeys keys = AesCbcWithIntegrity.keys
                    (keysString);
            if (keysString == null) {
                Toast.makeText(context, "Unable to decrypt. Please remove " +
                        "bank accounts and add again.", Toast.LENGTH_LONG)
                        .show();
            }
            return keys;
        } catch (Exception e) {
            Log.d("StashLog", "error in fetch existing keys: " + e
                    .getMessage());
            e.printStackTrace();
        }
        Toast.makeText(context, "Unable to decrypt. Please remove bank " +
                "accounts and add again.", Toast.LENGTH_LONG).show();
        return null;
    }

    Double getBankBalance(String access_token) {
        List<NameValuePair> postArgs = new ArrayList();
        String plaidUrl = context.getString(R.string.plaid_url) + "/get";
        postArgs.add(new BasicNameValuePair("access_token", access_token));

        return plaidPostRequest(plaidUrl, postArgs, fetchExistingKeys());
    }

    Double getBankBalance(String username, String password, ServerAccess
            .BankName bankName) {
        List<NameValuePair> postArgs = new ArrayList();
        String plaidUrl = context.getString(R.string.plaid_url);

        postArgs.add(new BasicNameValuePair("username", username));
        postArgs.add(new BasicNameValuePair("password", password));
        postArgs.add(new BasicNameValuePair("type", bankName.toString()));

        return plaidPostRequest(plaidUrl, postArgs, generateNewKeys());
    }

    /**
     * Returns the Map of the Bank access tokens. This map has a key of the
     * bank name
     * and the values are the encrypted access tokens.
     *
     * @return Map<String, String> of Bank names against encrypted access
     * tokens.
     */
    Map<String, String> getAccessTokenMapEncrypted() {
        Log.d("StashLog", "get access token map encrypted");
        try {
            if (ParseUser.getCurrentUser() != null) {
                Map<String, String> accessTokensEncrypted = ParseUser
                        .getCurrentUser().getMap("BankMap");
                return accessTokensEncrypted;
            } else {
                //current user is null. This shouldn't happen if the user was
                // logged in successfully.
            }
        } catch (Exception e) {
            Log.e("StashLog", "error in getAccessTokenMapEncrypted: " + e
                    .getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Same as getAccessTokenMapEncrypted, except the map has decrypted
     * access tokens.
     *
     * @return Map<String, String> of Bank names against decrypted access
     * tokens.
     */
    Map<String, String> getAccessTokenMapDecrypted() {
        Log.d("StashLog", "get access token map decrypted");
        Map<String, String> accessTokensDecrypted = new HashMap<>();
        Map<String, String> accessTokensEncrypted =
                getAccessTokenMapEncrypted();
        if (accessTokensEncrypted == null) {
            return null;
        }
        try {
            for (Map.Entry<String, String> bankEntry : accessTokensEncrypted
                    .entrySet()) {
                Log.d("StashLog", "Value: " + bankEntry.getValue());
                AesCbcWithIntegrity.CipherTextIvMac cipherTextIvMac = new
                        AesCbcWithIntegrity.CipherTextIvMac(bankEntry
                        .getValue());
                String accessToken = AesCbcWithIntegrity.decryptString
                        (cipherTextIvMac, fetchExistingKeys());
                String bankName = bankEntry.getKey();
                accessTokensDecrypted.put(bankName, accessToken);
            }
            return accessTokensDecrypted;
        } catch (Exception e) {
            Log.e("StashLog", "error in getAccessTokenMapDecrypted: " + e
                    .getMessage());
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
    private Double plaidPostRequest(String plaidUrl, List<NameValuePair>
            postArgs, AesCbcWithIntegrity.SecretKeys keys) {
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(plaidUrl);

        //Post Data
        postArgs.add(new BasicNameValuePair("client_id", context.getString(R
                .string
                .plaid_client_id)));
        postArgs.add(new BasicNameValuePair("secret", context.getString(R
                .string
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
            // Encrypt access token.
            String access_token = jObject.getString("access_token");
            AesCbcWithIntegrity.CipherTextIvMac cipherTextIvMac =
                    AesCbcWithIntegrity.encrypt(access_token, keys);
            String access_token_encrypted = cipherTextIvMac.toString();

            String bankName = jObject.getJSONArray("accounts").getJSONObject
                    (0).getString("institution_type");

            ParseUser.getCurrentUser().pinInBackground();
            ParseUser.getCurrentUser().saveInBackground();

            Map<String, String> bankMap = getAccessTokenMapEncrypted();
            if (bankMap == null) {
                bankMap = new HashMap<>();
            }
            bankMap.put(bankName, access_token_encrypted);
            ParseUser.getCurrentUser().put("BankMap", bankMap);

            ParseUser.getCurrentUser().pinInBackground();
            ParseUser.getCurrentUser().saveInBackground();

            JSONArray accountsArray = jObject.getJSONArray("accounts");
            double availableBalance = 0;
            for (int i = 0; i < accountsArray.length(); i++) {
                availableBalance += accountsArray.getJSONObject(i)
                        .getJSONObject("balance").optDouble("available", 0);
            }
            return availableBalance;
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return null;
    }
}
