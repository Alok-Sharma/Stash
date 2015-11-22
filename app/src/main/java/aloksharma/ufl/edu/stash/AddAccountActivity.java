package aloksharma.ufl.edu.stash;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class AddAccountActivity extends DrawerActivity {

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_account_activity);
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_account_activity);
//        ParseUser.logInInBackground("nikita","nikita");

        final Intent serverIntent = new Intent(this, ServerAccess.class);
        final BankMappingHelper bankMappingHelper = new BankMappingHelper(this);

        Button add_account = (Button) findViewById(R.id.add_account_btn);
        add_account.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText bankUsername = (EditText) findViewById(R.id
                        .bankUsername);
                EditText bankPassword = (EditText) findViewById(R.id
                        .bankPassword);
                Spinner bankNamesList = (Spinner) findViewById(R.id
                        .bankNamesList);
                String bank = bankNamesList.getSelectedItem().toString();

                serverIntent.putExtra("server_action", ServerAccess
                        .ServerAction.GET_BALANCE.toString());
                serverIntent.putExtra("bankUsername", bankUsername.getText()
                        .toString());
                serverIntent.putExtra("bankPassword", bankPassword.getText()
                        .toString());
                serverIntent.putExtra("bankName", bankMappingHelper.getBankCode(bank));
                startService(serverIntent);
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, BankAccountsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        finish();
    }
}
