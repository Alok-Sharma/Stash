package aloksharma.ufl.edu.stash;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

/**
 * Created by khimya on 11/8/15.
 */
public class ProfileActivity extends DrawerActivity {


    EditText user_name;
    EditText user_email;
    EditText user_password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_profile);
        user_name = (EditText) findViewById(R.id.edit_name);
        user_email = (EditText) findViewById(R.id.edit_email);
        user_password = (EditText) findViewById(R.id.edit_changepassword);

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        finish();
    }

    public void UpdateProfile(View view){
        String message_user_name = user_name.getText().toString();
        String message_user_email = user_email.getText().toString();
        String message_user_password = user_password.getText().toString();
        Log.d("ProfileActivityLog1", message_user_name);
        Log.d("ProfileActivityLog1", message_user_email);
        Log.d("ProfileActivityLog1", message_user_password);
        /**Create an intent*/
        Intent updateprofile_intent = new Intent(this, ServerAccess.class);

        /**Attaching Data to that intent*/
        updateprofile_intent.putExtra("User_Name", message_user_name);
        updateprofile_intent.putExtra("User_Email", message_user_email);
        updateprofile_intent.putExtra("User_Password", message_user_password);
        updateprofile_intent.putExtra("server_action", ServerAccess.ServerAction
                .UPDATE_PROFILE.toString());

        /**Launching that intent*/
        this.startService(updateprofile_intent);
    }
}
