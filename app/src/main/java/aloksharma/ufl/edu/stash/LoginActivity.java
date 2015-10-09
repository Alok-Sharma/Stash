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
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    Button loginButton;
    EditText username,password;
    TextView signUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        loginButton = (Button) findViewById(R.id.loginButton);
        signUp = (TextView) findViewById(R.id.signUp);

        loginButton.setOnClickListener(this);
        signUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.loginButton:
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
                            Toast.makeText(LoginActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                        } else {
                            Intent intent = new Intent(LoginActivity.this,DispatcherActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }
                });
                break;

            case R.id.signUp:
                startActivity(new Intent(this,RegisterActivity.class));
                break;
        }
    }
}
