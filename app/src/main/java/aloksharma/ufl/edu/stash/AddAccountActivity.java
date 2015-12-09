package aloksharma.ufl.edu.stash;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class AddAccountActivity extends DrawerActivity {


    @SuppressLint("MissingSuperCall")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_account_activity);

        final Intent serverIntent = new Intent(this, ServerAccess.class);
        final BankMappingHelper bankMappingHelper = new BankMappingHelper
                (this);

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
                serverIntent.putExtra("bankName", bankMappingHelper
                        .getBankCode(bank));
                startService(serverIntent);
//                finish();
            }
        });

        //Register to listen for the services response.
        IntentFilter serviceFilter = new IntentFilter("server_response");
        serviceFilter.addCategory(Intent.CATEGORY_DEFAULT);
        ServiceBroadcastReceiver serviceListener = new
                ServiceBroadcastReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver
                (serviceListener, serviceFilter);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, BankAccountsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        finish();
    }

    private class ServiceBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String response = intent.getStringExtra("server_response");
            ServerAccess.ServerAction responseAction = ServerAccess
                    .ServerAction.valueOf(response);

            switch (responseAction) {
                case GET_BALANCE:
                    String mfaRequired = intent.getStringExtra("mfaRequired");
                    if (mfaRequired != null && mfaRequired.equals("yes")) {

                        Log.d("AddAc: GET", "mfa req is yes");
                        final String access_token = intent.getStringExtra
                                ("access_token");
                        final String question = intent.getStringExtra
                                ("question");
                        final String username = intent.getStringExtra
                                ("username");
                        final String password = intent.getStringExtra
                                ("password");
                        final String type = intent.getStringExtra("type");

                        final AlertDialog.Builder alert = new AlertDialog
                                .Builder(AddAccountActivity.this);
                        final EditText edittext = new EditText
                                (AddAccountActivity.this);
                        alert.setMessage(question);
                        alert.setTitle("Please answer this question:");

                        alert.setView(edittext);

                        Log.d("dialog", "inside while 2");

                        alert.setPositiveButton("Submit", new DialogInterface
                                .OnClickListener() {
                            public void onClick(DialogInterface dialog, int
                                    whichButton) {
                                String answer = edittext.getText().toString();
                                final Intent serverIntent = new Intent
                                        (AddAccountActivity.this,
                                                ServerAccess.class);
                                serverIntent.putExtra("server_action",
                                        ServerAccess.ServerAction
                                                .MFA_QUESTION.toString());
                                serverIntent.putExtra("answer", answer);
                                serverIntent.putExtra("access_token",
                                        access_token);
                                serverIntent.putExtra("username", username);
                                serverIntent.putExtra("password", password);
                                serverIntent.putExtra("type", type);

                                startService(serverIntent);
                            }
                        });

                        alert.setNegativeButton("Cancel", new DialogInterface
                                .OnClickListener() {
                            public void onClick(DialogInterface dialog, int
                                    whichButton) {
                                dialog.dismiss();
                            }
                        });

                        Log.d("dialog", "inside while 3");

                        alert.show();
                    }
                    break;

                case MFA_QUESTION:
                    String responseCode = intent.getStringExtra
                            ("responseCode");
                    if (responseCode != null && responseCode.equals("201")) {
                        Log.d("AddAc: MFA", "code is 201");

                        final String access_token = intent.getStringExtra
                                ("access_token");
                        final String question = intent.getStringExtra
                                ("question");
                        final String username = intent.getStringExtra
                                ("username");
                        final String password = intent.getStringExtra
                                ("password");
                        final String type = intent.getStringExtra("type");

                        final AlertDialog.Builder alert = new AlertDialog
                                .Builder(AddAccountActivity.this);
                        final EditText edittext = new EditText
                                (AddAccountActivity.this);
                        alert.setMessage(question);
                        alert.setTitle("Please answer this question:");

                        alert.setView(edittext);

                        Log.d("dialog", "inside while 2, mfa_question case");

                        alert.setPositiveButton("Submit", new DialogInterface
                                .OnClickListener() {
                            public void onClick(DialogInterface dialog, int
                                    whichButton) {
                                String answer = edittext.getText().toString();
                                final Intent serverIntent = new Intent
                                        (AddAccountActivity.this,
                                                ServerAccess.class);
                                serverIntent.putExtra("server_action",
                                        ServerAccess.ServerAction
                                                .MFA_QUESTION.toString());
                                serverIntent.putExtra("answer", answer);
                                serverIntent.putExtra("access_token",
                                        access_token);
                                serverIntent.putExtra("username", username);
                                serverIntent.putExtra("password", password);
                                serverIntent.putExtra("type", type);

                                startService(serverIntent);
                            }
                        });

                        alert.setNegativeButton("Cancel", new DialogInterface
                                .OnClickListener() {
                            public void onClick(DialogInterface dialog, int
                                    whichButton) {
                                dialog.dismiss();
                            }
                        });

                        Log.d("dialog", "inside while 3");

                        alert.show();
                    } else if (responseCode != null && responseCode.equals
                            ("200")) {
                        Log.d("AddAc: MFA", "code is 200");
                        final String username = intent.getStringExtra
                                ("username");
                        final String password = intent.getStringExtra
                                ("password");
                        final String type = intent.getStringExtra("type");
                        final Intent serverIntent = new Intent
                                (AddAccountActivity.this,
                                        ServerAccess.class);
                        serverIntent.putExtra("server_action",
                                ServerAccess.ServerAction
                                        .GET_BALANCE.toString());
//                        serverIntent.putExtra("username", username);
//                        serverIntent.putExtra("password", password);
//                        serverIntent.putExtra("type", type);

//                        serverIntent.putExtra("bankUsername", username);
//                        serverIntent.putExtra("bankPassword", password);
//                        serverIntent.putExtra("bankName", type);
                        startService(serverIntent);

                        startService(serverIntent);
//                        finish();
                    }

                    break;
            }
        }

    }
}
