package aloksharma.ufl.edu.stash;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.HashMap;

public class AccountActivity extends DrawerActivity {

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_account_activity);
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_account_activity);
//        ParseUser.logInInBackground("nikita","nikita");

        final Intent serverIntent = new Intent(this, ServerAccess.class);

        final HashMap<String, Enum> bankNamesHash = new HashMap<String, Enum>();
        createBankNamesHash(bankNamesHash);

        Button add_account = (Button) findViewById(R.id.add_account_btn);
        add_account.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText bankUsername = (EditText) findViewById(R.id.bankUsername);
                EditText bankPassword = (EditText) findViewById(R.id.bankPassword);
                Spinner bankNamesList = (Spinner) findViewById(R.id.bankNamesList);
                String bank = bankNamesList.getSelectedItem().toString();

                serverIntent.putExtra("server_action", ServerAccess.ServerAction.GET_BALANCE.toString());
                serverIntent.putExtra("bankUsername", bankUsername.getText().toString());
                serverIntent.putExtra("bankPassword", bankPassword.getText().toString());
                serverIntent.putExtra("bankName", bankNamesHash.get(bank).toString());
                startService(serverIntent);
                finish();
            }
        });

    }

    private void createBankNamesHash(HashMap<String, Enum> hm){
        hm.put("American Express", ServerAccess.BankName.amex);
        hm.put("Bank of America", ServerAccess.BankName.bofa);
        hm.put("Capital One", ServerAccess.BankName.capone360);
        hm.put("Charles Schwab", ServerAccess.BankName.schwab);
        hm.put("Chase", ServerAccess.BankName.chase);
        hm.put("Citi", ServerAccess.BankName.citi);
        hm.put("Fidelity", ServerAccess.BankName.fidelity);
        hm.put("Navy Federal Credit Union", ServerAccess.BankName.nfcu);
        hm.put("PNC", ServerAccess.BankName.pnc);
        hm.put("Silicon Valley Bank", ServerAccess.BankName.svb);
        hm.put("SunTrust", ServerAccess.BankName.suntrust);
        hm.put("TD Bank", ServerAccess.BankName.td);
        hm.put("US Bank", ServerAccess.BankName.us);
        hm.put("USAA", ServerAccess.BankName.usaa);
        hm.put("Wells Fargo", ServerAccess.BankName.wells);
    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        finish();
    }
}
