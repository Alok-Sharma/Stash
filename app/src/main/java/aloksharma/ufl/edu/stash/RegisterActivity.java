package aloksharma.ufl.edu.stash;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.lang.String.*;


public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    Button registerButton;
    public EditText username,password,retypepassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        retypepassword = (EditText) findViewById(R.id.retypepassword);

        registerButton = (Button) findViewById(R.id.registerButton);
        registerButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.registerButton:
                boolean validationError = false;
                StringBuilder errorMessage = new StringBuilder("Please ");

                if(username.getText().toString().equals("") || password.getText().toString().equals("") || retypepassword.getText().toString().equals("")){
                    validationError = true;
                    errorMessage = errorMessage.append("fill all the text fields.");
                }

                if(!password.getText().toString().equals(retypepassword.getText().toString())){
                    validationError = true;
                    errorMessage = errorMessage.append("re-enter the password, the passwords do not match.");
                }

                if(validationError){
                    Toast.makeText(this,errorMessage.toString(),Toast.LENGTH_LONG).show();
                    return;
                }

                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("Please wait.");
                progressDialog.setMessage("Registering you into the system...");
                progressDialog.show();

                ParseUser user = new ParseUser();
                user.setUsername(username.getText().toString());
                user.setPassword(password.getText().toString());

                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        progressDialog.dismiss();
                        if (e != null) {
                            Toast.makeText(RegisterActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                        } else {
                            Intent intent = new Intent(RegisterActivity.this,DispatcherActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }
                });
               /*user.signUpInBackground((e) -> {
                   progressDialog.dismiss();
                   if (e != null) {
                       Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                   } else {
                       Intent intent = new Intent(this,DispatcherActivity.class);
                       intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                       startActivity(intent);
                   }
               });*/

                break;
        }

    }

}
