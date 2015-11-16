package aloksharma.ufl.edu.stash;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by nikitadagar on 11/14/15.
 */
public class BankAccountsActivity extends DrawerActivity {

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState, R.layout.bank_accounts);

        //creating an intent to talk to server access
        final Intent serverIntent = new Intent(this, ServerAccess.class);
        serverIntent.putExtra("server_action", ServerAccess.ServerAction.GET_BALANCE.toString());
        startService(serverIntent);

        //Register to listen for the services response.
        IntentFilter serviceFilter = new IntentFilter("server_response");
        serviceFilter.addCategory(Intent.CATEGORY_DEFAULT);
        ServiceBroadcastReceiver serviceListener = new ServiceBroadcastReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(serviceListener, serviceFilter);
    }

    private class ServiceBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String response = intent.getStringExtra("server_response");
            ServerAccess.ServerAction responseAction = ServerAccess.ServerAction.valueOf(response);

            switch (responseAction) {
                case GET_BALANCE:

                    ListView BanksList = (ListView) findViewById(R.id.BanksList);
                    ArrayList<String> list = null;

                    if (!"no_bank".equals(intent.getStringExtra("error"))){

                        HashMap<String, String> map = (HashMap<String, String>) intent.getSerializableExtra("map");
                        Set<String> set = map.keySet();
                        //passing ArrayList to ListView Adapter
                        list = new ArrayList<>(set);

                        //for Enum to String conversion
                        HashMap<String, String> banks = new HashMap<>();
                        initBankNamesHash(banks);

                        for (String bank : list) {
                            String b = banks.get(bank);
                            list.remove(bank);  //remove Enum
                            list.add(b);    //add full name of bank
                        }

                        //create list view adapter
                        ArrayAdapter listAdapter = new ArrayAdapter<>(context, R.layout.banks_list_row, list);

                        BanksList.setAdapter(listAdapter);
                    }
            }
        }
    }

    private void initBankNamesHash(HashMap<String, String> hm){
        hm.put("amex", "American Express");
        hm.put("bofa", "Bank of America");
        hm.put("capone360", "Capital One");
        hm.put("schwab", "Charles Schwab");
        hm.put("chase", "Chase");
        hm.put("citi", "Citi");
        hm.put("fidelity", "Fidelity");
        hm.put("nfcu", "Navy Federal Credit Union");
        hm.put("pnc", "PNC");
        hm.put("svb", "Silicon Valley Bank");
        hm.put("suntrust", "SunTrust");
        hm.put("td", "TD Bank");
        hm.put("us", "US Bank");
        hm.put("usaa", "USAA");
        hm.put("wells", "Wells Fargo");
    }
}
