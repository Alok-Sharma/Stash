package aloksharma.ufl.edu.stash;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.github.glomadrian.dashedcircularprogress.DashedCircularProgress;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class HomeActivity extends Activity{

    private DashedCircularProgress dashedCircularProgress;
    private ListView stashListView;
    ImageButton addStashButton;
    int savedAmount = 0;
    int toSaveAmount = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        addStashButton = (ImageButton) findViewById(R.id.addStashButton);
        addStashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, MyActivity.class));
            }
        });

        Intent serverIntent = new Intent(this, ServerAccess.class);
        serverIntent.putExtra("server_action", ServerAccess.ServerAction.GET_BALANCE.toString());
        this.startService(serverIntent);

        IntentFilter serviceFilter = new IntentFilter("server_response");
        serviceFilter.addCategory(Intent.CATEGORY_DEFAULT);
        ServiceBroadcastReceiver serviceListener = new ServiceBroadcastReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(serviceListener, serviceFilter);

        dashedCircularProgress = (DashedCircularProgress)findViewById(R.id.simple);
        dashedCircularProgress.reset();

        ParseQuery<ParseObject> stashQuery = ParseQuery.getQuery("Stash");
        stashQuery.whereEqualTo("user", ParseUser.getCurrentUser());
        final ArrayList<ParseObject> stashList = new ArrayList<>();
        final ArrayList<String> stashNameList = new ArrayList<>();

        stashQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> stashes, ParseException e) {
                for (ParseObject stash : stashes) {
                    toSaveAmount = toSaveAmount + stash.getInt("StashGoal");
                    TextView toSaveText = (TextView)findViewById(R.id.toSaveAmount);
                    toSaveText.setText("$" + toSaveAmount);
                    savedAmount = savedAmount + stash.getInt("StashValue");
                    stashList.add(stash);
                    int stashDifferential = stash.getInt("StashGoal")-stash.getInt("StashValue");
                    stashNameList.add(stash.getString("StashName")+" :\t"+String.valueOf(stashDifferential)+"$ to save");
                }
                dashedCircularProgress.setMax(toSaveAmount);
                dashedCircularProgress.setValue(savedAmount);

                stashListView = (ListView) findViewById(R.id.stashListView);
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(HomeActivity.this,android.R.layout.simple_list_item_1,stashNameList);
                stashListView.setAdapter(arrayAdapter);
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
}
