package aloksharma.ufl.edu.stash;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * Created by khimya on 11/8/15.
 */
public class ProfileActivity extends DrawerActivity {


    EditText user_name;
    EditText user_email;
    EditText user_password;
    SharedPreferences sharedPref;
    SharedPreferences.Editor sharedPrefEditor;
    Switch notif;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_profile);
        user_name = (EditText) findViewById(R.id.edit_name);
        user_email = (EditText) findViewById(R.id.edit_email);
        user_password = (EditText) findViewById(R.id.edit_changepassword);


        final ParseUser currentUser = ParseUser.getCurrentUser();
        String email_from_server =  currentUser.getEmail();
        String firstName_from_server = currentUser.getString("firstName");
        String lastName_from_server = currentUser.getString("lastName");

        if(email_from_server != null)
            user_email.setText(email_from_server);
        if(firstName_from_server!= null || lastName_from_server!= null){
            if(firstName_from_server == null)
                firstName_from_server = "";
            if(lastName_from_server == null)
                lastName_from_server = "";
            String name_from_server = firstName_from_server + ' ' + lastName_from_server;
            user_name.setText(name_from_server);
        }

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
        /**Create an intent*/
        Intent updateprofile_intent = new Intent(this, ServerAccess.class);

        /**Attaching Data to that intent*/
        updateprofile_intent.putExtra("User_Name", message_user_name);
        updateprofile_intent.putExtra("User_Email", message_user_email);
        updateprofile_intent.putExtra("User_Password", message_user_password);
        updateprofile_intent.putExtra("server_action", ServerAccess.ServerAction
                .UPDATE_PROFILE.toString());

        //Adding notification switch case status to shared Preferences
        sharedPref = this.getSharedPreferences("stashData", 0);
        sharedPrefEditor = sharedPref.edit();

        //event listener for switch status change
        notif = (Switch) findViewById(R.id.switch1);
        notif.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Boolean status = (isChecked == true) ? true : false;
                sharedPrefEditor.putBoolean("notifyStatus", status);
                sharedPrefEditor.commit();
            }
        });

        /**Launching that intent*/
        this.startService(updateprofile_intent);
    }
}
