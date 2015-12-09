package aloksharma.ufl.edu.stash;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.util.Log;
import android.widget.EditText;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PRITI on 12/8/15.
 */
public class alertDialogMFA extends Activity {

    SharedPreferences sharedPref;
    SharedPreferences.Editor sharedPrefEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = getSharedPreferences("stashData", 0);
        sharedPrefEditor = sharedPref.edit();
        Log.d("alert", "hi i am alert");
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        final int[] responseCode = {Integer.parseInt(getIntent()
                .getStringExtra("responseCode"))};
        final String[] question = {getIntent().getStringExtra("question")};
        final String[] responseString = new String[1];
        final String username = getIntent().getStringExtra("username");
        final String password = getIntent().getStringExtra("password");
        final String type = getIntent().getStringExtra("type");
        final String accessToken = getIntent().getStringExtra("accessToken");
        final String plaidUrl = "https://tartan.plaid.com/auth/step";
        final int[] count = {1};
        final HttpResponse[] response = {null};
        final JSONObject[] jObject = new JSONObject[1];

        final List<NameValuePair> postArgs = new ArrayList();
        postArgs.add(new BasicNameValuePair("username", username));
        postArgs.add(new BasicNameValuePair("password", password));
        postArgs.add(new BasicNameValuePair("type", type));
        postArgs.add(new BasicNameValuePair("accessToken", accessToken));
        postArgs.add(new BasicNameValuePair("client_id", "test_id"));
        postArgs.add(new BasicNameValuePair("secret", "test_secret"));
        postArgs.add(new BasicNameValuePair("list", "true"));

//        do
//        {

        if (responseCode[0] == 201) {
            Log.d("dialog", "inside while 1");
            final AlertDialog.Builder alert = new AlertDialog.Builder(this);
            final EditText edittext = new EditText(this);
            alert.setMessage(question[0]);
            alert.setTitle("Enter Your Title");

            alert.setView(edittext);

            Log.d("dialog", "inside while 2");

            alert.setPositiveButton("Submit", new DialogInterface
                    .OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    Editable YouEditTextValue = edittext.getText();
                    String answer = edittext.getText().toString();
                    postArgs.set(7, new BasicNameValuePair("mfa", answer));

//                    HttpPost httpPost = new HttpPost(plaidUrl);
//                    HttpClient httpClient = new DefaultHttpClient();
//                    try {
//                        httpPost.setEntity(new UrlEncodedFormEntity(postArgs));
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
                public void onClick(DialogInterface dialog, int whichButton) {
                    dialog.dismiss();
                }
            });

            Log.d("dialog", "inside while 3");

            alert.show();
        }


//        }
//        while (responseCode[0] == 201);
        else {
            JSONArray accountsArray;
            double availableBalance = 0;
            try {
                accountsArray = jObject[0].getJSONArray("accounts");
                for (int i = 0; i < accountsArray.length(); i++) {
                    availableBalance += accountsArray.getJSONObject(i)
                            .getJSONObject("balance").optDouble("available",
                                    0.0);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.d("StashPlaidHelper", "balanceMFA = " + availableBalance);
        }
    }

}