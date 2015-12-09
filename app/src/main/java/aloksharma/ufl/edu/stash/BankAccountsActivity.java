package aloksharma.ufl.edu.stash;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by nikitadagar on 11/14/15.
 */
public class BankAccountsActivity extends DrawerActivity {

    ServiceBroadcastReceiver serviceListener;
    IntentFilter serviceFilter;

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState, R.layout.bank_accounts);
        final Intent serverIntent = new Intent(this, ServerAccess.class);
        final Context context = this;

        //Register to listen for the services response.
        serviceFilter = new IntentFilter("server_response");
        serviceFilter.addCategory(Intent.CATEGORY_DEFAULT);
        serviceListener = new ServiceBroadcastReceiver();

        FloatingActionButton addAccountFAB = (FloatingActionButton)
                findViewById(R.id.fab);
        addAccountFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(BankAccountsActivity.this,
                        AddAccountActivity.class);
                startActivity(myIntent);
            }
        });

        ListView listView = (ListView) findViewById(R.id.BanksList);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(final AdapterView<?> parent, View view,
                                    final int listPosition, long id) {

                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setTitle("Delete Bank")
                        .setMessage("Are you sure you want to delete this " +
                                "bank?")
                        .setPositiveButton(android.R.string.yes, new
                                DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int
                                    which) {
                                String bankName = (String) parent
                                        .getItemAtPosition(listPosition);
                                serverIntent.putExtra("server_action",
                                        ServerAccess.ServerAction
                                                .DELETE_BANK.toString());
                                serverIntent.putExtra("BankName", bankName);
                                context.startService(serverIntent);
                                fetchBanks();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new
                                DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int
                                    which) {
                                // do nothing
                            }
                        })
                        .show();
            }
        });
    }

    private void fetchBanks() {
        //creating an intent to talk to server access
        final Intent serverIntent = new Intent(this, ServerAccess.class);
        serverIntent.putExtra("server_action", ServerAccess.ServerAction
                .GET_BALANCE.toString());
        startService(serverIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver
                (serviceListener, serviceFilter);
        fetchBanks();
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver
                (serviceListener);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();

        View empty = findViewById(R.id.empty);
        ListView list = (ListView) findViewById(R.id.BanksList);
        list.setEmptyView(empty);
    }

    private class ServiceBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String response = intent.getStringExtra("server_response");
            ServerAccess.ServerAction responseAction = ServerAccess
                    .ServerAction.valueOf(response);
            BankMappingHelper bankMappingHelper = new BankMappingHelper
                    (context);

            switch (responseAction) {
                case GET_BALANCE:
//                    if (intent.getStringExtra("finalBalance") != null &&
//                            intent.getStringExtra("finalBalance").equals
//                                    ("yes")) {

                        ListView banksList = (ListView) findViewById(R.id
                                .BanksList);
                        ArrayList<String> list;
                        String error = intent.getStringExtra("error");
                        HashMap<String, String> map = (HashMap<String,
                                String>) intent.getSerializableExtra("map");

                        if (error == null && map != null) {
                            Set<String> set = map.keySet();
                            //passing ArrayList to ListView Adapter
                            list = new ArrayList<>(set);

                            //for Enum to String conversion
                            for (String bank : list) {
                                String b = bankMappingHelper.getBankName(bank);
                                list.remove(bank);  //remove Enum
                                list.add(b);    //add full name of bank
                            }
                            //create list view adapter
                            ArrayAdapter listAdapter = new ArrayAdapter<>
                                    (context, R.layout.banks_list_row, list);
                            banksList.setAdapter(listAdapter);
                        } else if (error != null && error.equals("no_bank")) {
                            ArrayAdapter listAdapter = new ArrayAdapter<>
                                    (context, R.layout.banks_list_row, new
                                            ArrayList<>());
                            banksList.setAdapter(listAdapter);
                        }
//                    }
                    break;

            }
        }
    }
}
