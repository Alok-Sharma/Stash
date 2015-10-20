package aloksharma.ufl.edu.stash;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.Profile;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.widget.LoginButton;

import com.parse.ParseFacebookUtils;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import de.hdodenhof.circleimageview.CircleImageView;

import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import java.util.List;

public class LoginActivity extends Activity {

    Button loginButton,signUp;
    EditText username,password;
    LoginButton fbLoginButton;
    ParseUser parseUser;
    Profile mFbProfile;
    String name = null, email = null;
    CircleImageView mProfileImage;

    Intent homeActivity;

    public static final List<String> permissionList = new ArrayList<String>
            () {{
            add("public_profile");
            add("email");
        }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        homeActivity = new Intent(this, HomeActivity.class);

        if (ParseUser.getCurrentUser() != null) {
            parseUser = ParseUser.getCurrentUser();
            Log.d("StashFBLogin", "got it before fbButton pressed");
            startActivity(homeActivity);
            finish();
        }

        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        loginButton = (Button) findViewById(R.id.loginButton);
        signUp = (Button) findViewById(R.id.signUp);

        mProfileImage = (CircleImageView) findViewById(R.id.profile_image);

        fbLoginButton = (LoginButton) findViewById(R.id.fb_login_button);

        fbLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginWithFB();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginWithStandard();
            }
        });
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

    }

    public void loginWithStandard(){
        boolean validationError = false;
        StringBuilder errorMessage = new StringBuilder("Please ");
        if("".equals(username) || "".equals(password)){
            validationError = true;
            errorMessage = errorMessage.append("fill all the text fields.");
        }
        if(validationError){
            Toast.makeText(this, errorMessage.toString(), Toast.LENGTH_LONG).show();
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait.");
        progressDialog.setMessage("Logging into the application...");
        progressDialog.show();
        ParseUser.logInInBackground(username.getText().toString(), password.getText().toString(), new LogInCallback() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                progressDialog.dismiss();
                if (e != null) {
                    Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                } else {
                    //Intent intent = new Intent(LoginActivity.this, DispatcherActivity.class);
                    homeActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(homeActivity);
                }
            }
        });
    }

    public void register(){
        startActivity(new Intent(this, RegisterActivity.class));
    }

    public void loginWithFB() {
        ParseFacebookUtils.logInWithReadPermissionsInBackground
                (LoginActivity.this, permissionList, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException
                            err) {
                        if (user == null) {
                            Log.d("StashFBLogin", "Login Cancelled by user");
                        } else if (user.isNew()) {
                            Log.d("StashFBLogin", "User FB signup successful");
                            getUserDetailsFromFB();
                            startActivity(homeActivity);
                            finish();
                        } else {
                            Log.d("StashFBLogin", "User FB login successful");
                            user.saveInBackground();
                            user.pinInBackground();
                            getUserDetailsFromParse();
                        }
                        if (err != null) {
                            Log.d("priti1", err.getMessage());
                        }
                    }
                });
    }

    private void getUserDetailsFromFB() {
        Log.d("StashFBLogin", "inside getUserDetailsFromFB ");
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {

                        Log.d("json", response.toString());
                        try {
                            name = response.getJSONObject().getString("name");
                            email = response.getJSONObject().getString
                                    ("email");
                            saveNewUser();
                        } catch (JSONException e) {
                            Log.d("StashFBLogin", e.getMessage());
                        }


                    }
                }
        ).executeAsync();

        mFbProfile = Profile.getCurrentProfile();

        ProfilePhotoAsync profilePhotoAsync = new ProfilePhotoAsync(mFbProfile);
        profilePhotoAsync.execute();

    }

    private void saveNewUser() {
        parseUser = ParseUser.getCurrentUser();

        if (name != null) parseUser.setUsername(name);
        if (email != null) parseUser.setEmail(email);
        parseUser.put("firstName", mFbProfile.getFirstName());
        Log.d("first name", mFbProfile.getFirstName());
        parseUser.put("lastName", mFbProfile.getLastName());
        Log.d("last name", mFbProfile.getLastName());


        Log.d("objectID", parseUser.getObjectId());
        //Saving profile photo as a ParseFile
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Bitmap bitmap = ((BitmapDrawable) mProfileImage.getDrawable()).getBitmap();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        byte[] data = stream.toByteArray();
        String thumbName = parseUser.getUsername().replaceAll("\\s+", "");
        final ParseFile parseFile = new ParseFile(thumbName + "_thumb.jpg", data);

        parseFile.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                parseUser.put("profileThumb", parseFile);

                //Finally save all the user details
                parseUser.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        Toast.makeText(LoginActivity.this, "New user:" + name + "" +
                                " Signed up", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

//        ParseQuery<ParseObject> userTableQuery = ParseQuery.getQuery("User");
//        userTableQuery.getInBackground(parseUser.getObjectId(), new
//                GetCallback<ParseObject>() {
//                    public void done(ParseObject userTableObject,
//                                     ParseException
//                                             e) {
//                        if (e == null) {
//                            userTableObject.put("firstName", mFbProfile
//                                    .getFirstName());
//                            userTableObject.put("lastName", mFbProfile
//                                    .getLastName());
//                            if (email != null)
//                                userTableObject.put("email", email);
////                            userTableObject.put("profileThumb", parseFile);
//                        } else {
//                            Log.d("StashFBLogin", e.getMessage());
//                        }
//                    }
//                });


        Log.d("StashFBLogin", "leaving saveNewUser ");

    }

    private void getUserDetailsFromParse() {
        parseUser = ParseUser.getCurrentUser();
        //Fetch profile photo
        try {
            ParseFile parseFile = parseUser.getParseFile("profileThumb");
            byte[] data = parseFile.getData();
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data
                    .length);
            mProfileImage.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Toast.makeText(LoginActivity.this, "Welcome back to Stash!", Toast
                .LENGTH_SHORT).show();
        Log.d("StashFBLogin", "logged in existing user using parse");
        startActivity(homeActivity);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent
            data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_fgsign_in, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class ProfilePhotoAsync extends AsyncTask<String, String, String> {
        Profile profile;
        public Bitmap bitmap;

        public ProfilePhotoAsync(Profile profile) {
            this.profile = profile;
        }

        @Override
        protected String doInBackground(String... params) {
            // Fetching data from URI and storing in bitmap
            bitmap = DownloadImageBitmap(profile.getProfilePictureUri(200,
                    200).toString());
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mProfileImage.setImageBitmap(bitmap);
        }

        public Bitmap DownloadImageBitmap(String url) {
            Bitmap bm = null;
            try {
                URL aURL = new URL(url);
                URLConnection conn = aURL.openConnection();
                conn.connect();
                InputStream is = conn.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);
                bm = BitmapFactory.decodeStream(bis);
                bis.close();
                is.close();
            } catch (IOException e) {
                Log.e("IMAGE", "Error getting bitmap", e);
            }
            return bm;
        }
    }

}
