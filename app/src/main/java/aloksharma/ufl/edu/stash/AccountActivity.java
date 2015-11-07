package aloksharma.ufl.edu.stash;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Button;
import android.view.View;

import com.parse.ParseUser;

public class AccountActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_activity);
        ParseUser.logInInBackground("nikita","nikita");

        final Intent serverIntent = new Intent(this, ServerAccess.class);

        Button add_account = (Button) findViewById(R.id.add_account_btn);
        add_account.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText bankUsername = (EditText) findViewById(R.id.bankUsername);
                EditText bankPassword = (EditText) findViewById(R.id.bankPassword);
                Spinner bankNamesList = (Spinner) findViewById(R.id.bankNamesList);


                serverIntent.putExtra("server_action", ServerAccess.ServerAction.GET_BALANCE.toString());
                serverIntent.putExtra("bankUsername", bankUsername.getText().toString());
                serverIntent.putExtra("bankPassword", bankPassword.getText().toString());
                serverIntent.putExtra("bankName", "wells");
                startService(serverIntent);

            }
        });

    }

}
