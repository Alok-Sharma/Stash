package aloksharma.ufl.edu.stash;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.internal.view.ContextThemeWrapper;
import android.text.Editable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

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
    SharedPreferences sharedPref;
    SharedPreferences.Editor sharedPrefEditor;

    List<String> mfaBanks = new ArrayList<String>() {{
        add("bofa");
        add("capone360");
        add("citi");
        add("pnc");
        add("td");
        add("us");
        add("usaa");
    }};

    PlaidHelper(Context context) {
        this.context = context;
        sharedPref = context.getSharedPreferences("stashData", 0);
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
            if (keysString == null) {
                return null;
            } else {
                AesCbcWithIntegrity.SecretKeys keys = AesCbcWithIntegrity
                        .keys(keysString);
                return keys;
            }
        } catch (Exception e) {
            Log.d("StashLog", "error in fetch existing keys: " + e
                    .getMessage());
            e.printStackTrace();
        }
        return null;
    }

    Double getBankBalance(String access_token) {
        List<NameValuePair> postArgs = new ArrayList();
        String plaidUrl = context.getString(R.string.plaid_url) + "/get";
        postArgs.add(new BasicNameValuePair("access_token", access_token));
        AesCbcWithIntegrity.SecretKeys keys = fetchExistingKeys();
        if (keys != null) {
            return plaidPostRequest(plaidUrl, postArgs, keys);
        } else {
            return null;
        }
    }

    Double getBankBalance(String username, String password, String bankName) {
        List<NameValuePair> postArgs = new ArrayList();
        Log.d("StashPlaidHelper", bankName);
        if (mfaBanks.contains(bankName)) {
            String plaidUrl = context.getString(R.string.plaid_url);

            postArgs.add(new BasicNameValuePair("username", username));
            postArgs.add(new BasicNameValuePair("password", password));
            postArgs.add(new BasicNameValuePair("type", bankName));

            return plaidMFAPostRequest(plaidUrl, postArgs, generateNewKeys()
                    , username, password, bankName);
        } else {
            String plaidUrl = context.getString(R.string.plaid_url);

            postArgs.add(new BasicNameValuePair("username", username));
            postArgs.add(new BasicNameValuePair("password", password));
            postArgs.add(new BasicNameValuePair("type", bankName));

            return plaidPostRequest(plaidUrl, postArgs, generateNewKeys());
        }
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
//                Log.d("StashLog", "Size: " + accessTokensEncrypted.size());
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
                AesCbcWithIntegrity.SecretKeys keys = fetchExistingKeys();
                if (keys == null) {
                    return null;
                } else {
                    String accessToken = AesCbcWithIntegrity.decryptString
                            (cipherTextIvMac, keys);
                    String bankName = bankEntry.getKey();
                    accessTokensDecrypted.put(bankName, accessToken);
                    return accessTokensDecrypted;
                }
            }
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
//            if (response.getStatusLine().getStatusCode()==200)
//            {
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

            Map<String, String> bankMap = getAccessTokenMapEncrypted();
            if (bankMap == null) {
                bankMap = new HashMap<>();
            }
            bankMap.put(bankName, access_token_encrypted);
            ParseUser.getCurrentUser().put("BankMap", bankMap);
            Log.d("StashLog", "Bankmap updated");
            ParseUser.getCurrentUser().pinInBackground();
            ParseUser.getCurrentUser().saveInBackground();

            JSONArray accountsArray = jObject.getJSONArray("accounts");
            double availableBalance = 0;
            for (int i = 0; i < accountsArray.length(); i++) {
                availableBalance += accountsArray.getJSONObject(i)
                        .getJSONObject("balance").optDouble("available", 0.0);
            }
            sharedPrefEditor.putString("balance", availableBalance + "");
            sharedPrefEditor.commit();
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

    private Double plaidMFAPostRequest(String plaidUrl, final List<NameValuePair>
            postArgs, AesCbcWithIntegrity.SecretKeys keys, String username,
                                       String password, String bankName) {
        postArgs.add(new BasicNameValuePair("client_id", "test_id"));
        postArgs.add(new BasicNameValuePair("secret", "test_secret"));
        postArgs.add(new BasicNameValuePair("list", "true"));
        HttpPost httpPost = new HttpPost(plaidUrl);
        HttpClient httpClient = new DefaultHttpClient();
        String responseString;
        int count = 0;
        double availableBalance = 0;
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(postArgs));
            HttpResponse response = httpClient.execute(httpPost);
            responseString = EntityUtils.toString(response.getEntity());
            JSONObject jObject = new JSONObject(responseString);
            Log.d("S:responseString", responseString);
            Log.d("S:response", response.toString());
            if (response.getStatusLine().getStatusCode() == 201) {

                postArgs.add(new BasicNameValuePair("access_token", jObject
                        .getString("access_token")));
                String accessToken = jObject.getString("access_token");

//                while (response.getStatusLine().getStatusCode() == 201) {

//                    Intent i = new Intent(context, alertDialogMFA.class);
//                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    context.startActivity(i);
//                    displayAlert();
//                postArgs.add(new BasicNameValuePair("username", username));
//                postArgs.add(new BasicNameValuePair("password", password));
//                postArgs.add(new BasicNameValuePair("type", bankName));

//                Intent intent = new Intent(context, alertDialogMFA.class);
//                intent.putExtra("responseString", responseString);
//                intent.putExtra("username", username);
//                intent.putExtra("password", password);
//                intent.putExtra("type", bankName);
//                intent.putExtra("accessToken", accessToken);
//                intent.putExtra("question", jObject.getJSONArray("mfa").get
//                        (0).toString());
//                intent.putExtra("responseCode", String.valueOf(response.getStatusLine()
//                        .getStatusCode()));
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                context.startActivity(intent);

//                context.setTheme(R.style.AppTheme);

                final WindowManager manager = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.gravity = Gravity.CENTER;
                layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
                layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
                layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                layoutParams.alpha = 1.0f;
                layoutParams.packageName = context.getPackageName();
                layoutParams.buttonBrightness = 1f;
                layoutParams.windowAnimations = android.R.style
                        .Animation_Dialog;

                final View view = View.inflate(context.getApplicationContext
                        (), R.layout.dialog_mfa, null);
                Button yesButton = (Button) view.findViewById(R.id.button);
                Button noButton = (Button) view.findViewById(R.id.button2);
                yesButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        manager.removeView(view);
                    }
                });
                noButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        manager.removeView(view);
                    }
                });
                manager.addView(view, layoutParams);


            final AlertDialog.Builder alert = new AlertDialog.Builder(new
                    ContextThemeWrapper(
                    App.getContext(), R.style.AppTheme));
            final EditText edittext = new EditText(context);
            alert.setMessage(jObject.getJSONArray("mfa").get(0).toString());
            alert.setTitle("Enter Your Title");

            alert.setView(edittext);

            Log.d("dialog", "inside while 2");

            alert.setPositiveButton("Submit", new DialogInterface
                    .OnClickListener() {
                public void onClick(DialogInterface dialog, int
                        whichButton) {
                    Editable YouEditTextValue = edittext.getText();
                    String answer = edittext.getText().toString();
                    postArgs.set(7, new BasicNameValuePair("mfa", answer));

//                    HttpPost httpPost = new HttpPost(plaidUrl);
//                    HttpClient httpClient = new DefaultHttpClient();
//                    try {
//                        httpPost.setEntity(new UrlEncodedFormEntity
// (postArgs));
//                        response[0] = httpClient.execute(httpPost);
//                        responseString[0] = EntityUtils.toString(response[0]
//                                .getEntity());
//                        Log.d("dialog", responseString[0]);
//                        jObject[0] = new JSONObject(responseString[0]);
//                        count[0]++;
//                        responseCode[0] = response[0].getStatusLine()
//                                .getStatusCode();
//                        question[0] = jObject[0].getJSONArray("mfa")
//                                .getString(0);
//                        dialog.dismiss();
//                    } catch (UnsupportedEncodingException e) {
//                        e.printStackTrace();
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    } catch (ClientProtocolException e) {
//                        e.printStackTrace();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                        sharedPrefEditor.putString("answer", answer);
                        sharedPrefEditor.commit();
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface
                        .OnClickListener() {
                    public void onClick(DialogInterface dialog, int
                            whichButton) {
                        dialog.dismiss();
                    }
                });

                Log.d("dialog", "inside while 3");


//                alert.show();

//                alert.ge
                Log.d("answer:",sharedPref.getString("answer","default"));
            }
//                Log.d("S:StatusLine", response.getStatusLine().toString());
//
//                Log.d("StashPlaidHelper", jObject.toString());
//
//                plaidUrl = "https://tartan.plaid.com/auth/step";
//                count++;
//                Log.d("StashPlaidHelper", String.valueOf(count));
//                if (count == 2) {
//                    postArgs.set(7, new BasicNameValuePair("mfa",
//                            "tomato"));
////                        System.out.println(postArgs.toString());
//                } else
//                    postArgs.add(new BasicNameValuePair("mfa", "again"));
//                httpPost = new HttpPost(plaidUrl);
//                httpPost.setEntity(new UrlEncodedFormEntity(postArgs));
//                response = httpClient.execute(httpPost);
//                responseString = EntityUtils.toString(response.getEntity
//                        ());
//                jObject = new JSONObject(responseString);
//                Log.d("StashPlaidHelper", jObject.toString());
//                if (response.getStatusLine().getStatusCode() != 201 &&
//                        response.getStatusLine().getStatusCode() != 200) {
//                    Log.d("StashPlaidHelper", String.valueOf(response
//                            .getStatusLine().getStatusCode()));
//                    Log.d("StashPlaidHelper", jObject.getString
//                            ("message"));
//                    Log.d("StashPlaidHelper", jObject.getString
//                            ("resolve"));
//                }
////                }
//                JSONArray accountsArray = jObject.getJSONArray("accounts");
//
//                for (int i = 0; i < accountsArray.length(); i++) {
//                    availableBalance += accountsArray.getJSONObject(i)
//                            .getJSONObject("balance").optDouble("available",
//                                    0.0);
//                }
//                Log.d("StashPlaidHelper", "balanceMFA = " +
// availableBalance);
//            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

        return availableBalance;
    }

}
