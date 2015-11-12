package aloksharma.ufl.edu.stash;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class HomeActivity extends DrawerActivity{

    private HoloCircularProgressBar mainHoloCircularProgressBar;
    private GridView stashGridView;
    int savedAmount = 0;
    int toSaveAmount = 0;

    static ArrayList<ParseObject> gridObjectList = new ArrayList<>();
    static int saveAmount;
    static String stashName;
    static String moneyGoalsGoalValue;
    static String moneyGoalsMonthlySavings;
    static String moneyGoalsPercentage;
    static String moneyGoalsToSaveAmount;
    static float moneyGoalsProgressBar;
    static String timeGoalsGoalValue;
    static String timeGoalsMonthlySavings;
    static String timeGoalsToSaveAmount;
    static String timeGoalsPercentage;
    static float timeGoalsProgressBar;

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_home);
        if (ParseUser.getCurrentUser() == null) {
            //user isn't logged in, move to login page.
            Intent loginActivity = new Intent(this, LoginActivity.class);
            startActivity(loginActivity);
            finish();
        }

//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_home);

        //Start the service and get the balance.
        Intent serverIntent = new Intent(this, ServerAccess.class);
        serverIntent.putExtra("server_action", ServerAccess.ServerAction.GET_BALANCE.toString());
        this.startService(serverIntent);

        //Register to listen for the services response.
        IntentFilter serviceFilter = new IntentFilter("server_response");
        serviceFilter.addCategory(Intent.CATEGORY_DEFAULT);
        ServiceBroadcastReceiver serviceListener = new ServiceBroadcastReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(serviceListener, serviceFilter);

        mainHoloCircularProgressBar = (HoloCircularProgressBar)findViewById(R.id.simple);

        ParseQuery<ParseObject> stashQuery = ParseQuery.getQuery("Stash");
        stashQuery.whereEqualTo("user", ParseUser.getCurrentUser());
        final ArrayList<ParseObject> stashList = new ArrayList<>();

        final ArrayList<String> stashNameList = new ArrayList<>();

        stashQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> stashes, ParseException e) {
                for (ParseObject stash : stashes) {
                    toSaveAmount = toSaveAmount + stash.getInt("StashGoal");
                    savedAmount = savedAmount + stash.getInt("StashValue");
                    stashList.add(stash);
                    int stashDifferential = stash.getInt("StashGoal") - stash.getInt("StashValue");
                    stashNameList.add(stash.getString("StashName")+" :\t"+String.valueOf(stashDifferential)+"$ to save");
                }
                TextView toSaveText = (TextView)findViewById(R.id.toSaveAmount);
                toSaveText.setText("$" + toSaveAmount);

                gridObjectList = stashList;
                saveAmount = toSaveAmount;
                Log.d("StashLogAlok", "tosave: " + toSaveAmount + " saved: " + savedAmount);

                float mainCircleProgress;
                if(savedAmount == 0 || toSaveAmount == 0) {
                    mainCircleProgress = 0;
                } else {
                    mainCircleProgress = (float)savedAmount/toSaveAmount;
                }
                mainHoloCircularProgressBar.setProgress(mainCircleProgress);

                stashGridView = (GridView) findViewById(R.id.stashGridView);
                stashGridView.setAdapter(new ProgressBarAdapter(getApplicationContext()));

               stashGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View v,
                                            int position, long id) {
                        stashName = gridObjectList.get(position).getString("StashName");

                        moneyGoalsGoalValue = "$"+gridObjectList.get(position).getInt("StashGoal");
                        moneyGoalsMonthlySavings = "$"+String.valueOf(Math.round(((float) gridObjectList.get(position).getInt("StashGoal")/12)*100.0)/100.0)+"/month";
                        moneyGoalsProgressBar =  (float) (gridObjectList.get(position).getInt("StashGoal")) / saveAmount;
                        moneyGoalsPercentage = String.valueOf(Math.round(((gridObjectList.get(position).getInt("StashGoal"))) * 100) / saveAmount)+"%";
                        moneyGoalsToSaveAmount = "$"+saveAmount;

                        timeGoalsGoalValue = "$"+gridObjectList.get(position).getInt("StashGoal");
                        timeGoalsMonthlySavings = "$"+String.valueOf(Math.round(((float) gridObjectList.get(position).getInt("StashGoal")/12)*100.0)/100.0)+"/month";
                        timeGoalsProgressBar =  (float) (gridObjectList.get(position).getInt("StashGoal")) / saveAmount;
                        timeGoalsPercentage = String.valueOf(Math.round(((gridObjectList.get(position).getInt("StashGoal"))) * 100) / saveAmount)+"%";
                        timeGoalsToSaveAmount = "$"+saveAmount;

                        Intent i = new Intent(getApplicationContext(), ViewStashActivity.class);
                        startActivity(i);
                    }
                });
            }
        });
    }



    /**
     * The inner Broadcast receiver class that receives the responses from the ServerAccess class.
     * This class checks what response it is receiving, with a similar switch case statement as in the
     * ServerAccess class, and performs actions on the UI accordingly.
     */
    private class ServiceBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String response = intent.getStringExtra("server_response");
            ServerAccess.ServerAction responseAction = ServerAccess.ServerAction.valueOf(response);

            switch (responseAction) {
                case GET_BALANCE:
                    Double balance = intent.getDoubleExtra("balance", -1.0);
                    Double effectiveBalance = balance - savedAmount;

                    TextView effectiveBalanceView = (TextView)findViewById(R.id.effectiveBalance);
                    effectiveBalanceView.setText("Effective Balance: $" + effectiveBalance);

                    String error = intent.getStringExtra("error");
                    if(error != null && error.equals("no_bank")) {
                        Toast.makeText(context, "Please add at least one bank account from the menu.", Toast.LENGTH_LONG).show();
                    }
//                    TextView balanceText = (TextView)findViewById(R.id.mainTextView);
//                    balanceText.setText("Alok, your balance is: " + balance);
            }
        }
    }
}
