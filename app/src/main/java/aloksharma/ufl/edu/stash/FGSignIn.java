package aloksharma.ufl.edu.stash;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.widget.LoginButton;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;


public class FGSignIn extends Activity {

    LoginButton fbLoginButton;
    EditText fbUsername, fbEmailID;
    ParseUser parseUser;
    String name = null, email = null;

    public static final List<String> permissionList = new ArrayList<String>
            () {{
            add("public_profile");
            add("email");
        }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fgsign_in);

        fbLoginButton = (LoginButton) findViewById(R.id.fb_login_button);

        fbLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseFacebookUtils.logInWithReadPermissionsInBackground
                        (FGSignIn.this, permissionList, new LogInCallback() {
                            @Override
                            public void done(ParseUser user, ParseException
                                    err) {
                                if (user == null) {
                                    Log.d("StashFBLogin", "Login Cancelled " +
                                            "by user");
                                } else if (user.isNew()) {
                                    Log.d("StashFBLogin", "User FB signup " +
                                            "successful");
                                    getUserDetailsFromFB();
                                } else {
                                    Log.d("StashFBLogin", "User FB login " +
                                            "successful");
                                    getUserDetailsFromParse(user);
                                }
                            }
                        });
            }
        });
    }

    private void getUserDetailsFromFB() {
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        try {
                            email = response.getJSONObject().getString
                                    ("email");
                            fbEmailID.setText(email);
                            name = response.getJSONObject().getString("name");
                            fbUsername.setText(name);
                            saveNewUser();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).executeAsync();
    }

    private void saveNewUser() {
        parseUser = ParseUser.getCurrentUser();
        parseUser.setUsername(name);
        parseUser.setEmail(email);
        parseUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Toast.makeText(FGSignIn.this, "New user:" + name
                        + " Signed up", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getUserDetailsFromParse(ParseUser user) {
        parseUser = ParseUser.getCurrentUser();
        Toast.makeText(FGSignIn.this, "Welcome back " + parseUser
                .getUsername(), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent
            data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    public void fetchExistingUser() {
        Intent serverIntent = new Intent(getApplicationContext(),
                ServerAccess.class);
        serverIntent.putExtra("server_action", ServerAccess.ServerAction
                .GET_USER.toString());
        getApplicationContext().startService(serverIntent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is
        // present.
        getMenuInflater().inflate(R.menu.menu_fgsign_in, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
