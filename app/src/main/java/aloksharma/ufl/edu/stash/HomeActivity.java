package aloksharma.ufl.edu.stash;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.glomadrian.dashedcircularprogress.DashedCircularProgress;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class HomeActivity extends Activity implements View.OnClickListener{

    private DashedCircularProgress dashedCircularProgress;
    private DashedCircularProgress circularProgressStash1;
    private DashedCircularProgress circularProgressStash2;
    ImageButton logoutButton;
    ImageButton addStashButton;
    int savedAmount = 0;
    int toSaveAmount = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        logoutButton = (ImageButton) findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(this);
        addStashButton = (ImageButton) findViewById(R.id.addStashButton);
        addStashButton.setOnClickListener(this);

        Intent serverIntent = new Intent(this, ServerAccess.class);
        serverIntent.putExtra("server_action", ServerAccess.ServerAction.GET_BALANCE.toString());
        this.startService(serverIntent);

        IntentFilter serviceFilter = new IntentFilter("server_response");
        serviceFilter.addCategory(Intent.CATEGORY_DEFAULT);
        ServiceBroadcastReceiver serviceListener = new ServiceBroadcastReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(serviceListener, serviceFilter);

        dashedCircularProgress = (DashedCircularProgress)findViewById(R.id.simple);
        dashedCircularProgress.reset();
        circularProgressStash1 = (DashedCircularProgress)findViewById(R.id.stash1Progress);
        circularProgressStash1.reset();
        circularProgressStash2 = (DashedCircularProgress)findViewById(R.id.stash2Progress);
        circularProgressStash2.reset();

        ParseQuery<ParseObject> stashQuery = ParseQuery.getQuery("Stash");
        stashQuery.whereEqualTo("user", ParseUser.getCurrentUser());
        final ArrayList<ParseObject> stashList = new ArrayList<>();

        stashQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> stashes, ParseException e) {
                for (ParseObject stash : stashes) {
                    toSaveAmount = toSaveAmount + stash.getInt("StashGoal");
                    TextView toSaveText = (TextView) findViewById(R.id.toSaveAmount);
                    toSaveText.setText("$" + toSaveAmount);
                    savedAmount = savedAmount + stash.getInt("StashValue");
                    stashList.add(stash);
                }
                dashedCircularProgress.setMax(toSaveAmount);
                dashedCircularProgress.setValue(savedAmount);

                if(stashList.size()>=1) {
                    TextView stash1Name = (TextView) findViewById(R.id.stash1Name);
                    stash1Name.setText(stashList.get(stashList.size() - 1).getString("StashName"));
                    circularProgressStash1.setMax(100);
                    int stash1pct = Math.round(((stashList.get(stashList.size() - 1).getInt("StashGoal")) * 100)/toSaveAmount);
                    circularProgressStash1.setValue(stash1pct);
                    TextView stash1Percentage = (TextView) findViewById(R.id.stash1Percentage);
                    stash1Percentage.setText(Integer.toString(stash1pct) + "%");
                }

                if(stashList.size()>=2) {
                    TextView stash2Name = (TextView) findViewById(R.id.stash2Name);
                    stash2Name.setText(stashList.get(stashList.size() - 2).getString("StashName"));
                    circularProgressStash2.setMax(100);
                    int stash2pct = Math.round(((stashList.get(stashList.size() - 2).getInt("StashGoal")) * 100)/toSaveAmount);
                    circularProgressStash2.setValue(stash2pct);
                    TextView stash2Percentage = (TextView) findViewById(R.id.stash2Percentage);
                    stash2Percentage.setText(Integer.toString(stash2pct) + "%");
                }
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
//                    TextView balanceText = (TextView)findViewById(R.id.mainTextView);
//                    balanceText.setText("Alok, your balance is: " + balance);
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.logoutButton:
                ParseUser.logOutInBackground();
                startActivity(new Intent(this,LoginActivity.class));
                break;
            case R.id.addStashButton:
                startActivity(new Intent(this,MyActivity.class));
                break;
        }
    }
}
