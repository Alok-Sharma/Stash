package aloksharma.ufl.edu.stash;

import android.app.IntentService;
import android.content.Intent;

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
import java.util.List;

/**
 * Created by Alok on 9/26/2015.
 */
public class ServerAccess extends IntentService {

    public enum ServerAction {
        ADD_USER, GET_USER, ADD_STASH, GET_ACCESS_TOKEN
    }

    public enum BankName {
        amex, bofa, capone360, schwab, chase, citi, fidelity, nfcu, pnc, svb, suntrust,
        td, us, usaa, wells
    }

    public ServerAccess() {
        super("ServerAccess");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getStringExtra("server_action");
        Intent responseIntent = new Intent("server_response");
        responseIntent.putExtra("server_response", action);


    }

    public void addUser() {
        //Stub for adding a new user to Parse. Will be used on registration.
    }

    public void getUser() {
        //Stub to get the Parse user object.
    }

    private Double getBankBalance(String access_token) {
        List<NameValuePair> postArgs = new ArrayList();
        String plaidUrl = getString(R.string.plaid_url) + "/get";
        postArgs.add(new BasicNameValuePair("access_token", access_token));

        return plaidPostRequest(plaidUrl, postArgs);
    }

    private Double getBankBalance(String username, String password, BankName bankName) {
        List<NameValuePair> postArgs = new ArrayList();
        String plaidUrl = getString(R.string.plaid_url);
        postArgs.add(new BasicNameValuePair("username", username));
        postArgs.add(new BasicNameValuePair("password", password));
        postArgs.add(new BasicNameValuePair("type", bankName.toString()));

        return plaidPostRequest(plaidUrl, postArgs);
    }

    /**
     * Executes a POST to the plaid /auth endpoint, and fetches the users bank balance.
     *
     * @param plaidUrl The Plaid url to send the request to
     * @param postArgs The POST arguments
     * @return Double Users Bank balance.
     */
    private Double plaidPostRequest(String plaidUrl, List<NameValuePair> postArgs) {
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(plaidUrl);

        //Post Data
        postArgs.add(new BasicNameValuePair("client_id", getString(R.string.plaid_client_id)));
        postArgs.add(new BasicNameValuePair("secret", getString(R.string.plaid_secret)));

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
            double balance = jObject.getJSONArray("accounts").getJSONObject(0).getJSONObject("balance").getDouble("available");
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
