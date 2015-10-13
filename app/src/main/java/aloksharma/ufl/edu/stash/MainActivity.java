package aloksharma.ufl.edu.stash;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.TextView;

import com.parse.ParseUser;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ParseUser.logInInBackground("alok.sharma127@gmail.com", "aloksharma");

        Intent serverIntent = new Intent(this, ServerAccess.class);
        serverIntent.putExtra("server_action", ServerAccess.ServerAction.GET_BALANCE.toString());
        this.startService(serverIntent);

        IntentFilter serviceFilter = new IntentFilter("server_response");
        serviceFilter.addCategory(Intent.CATEGORY_DEFAULT);
        ServiceBroadcastReceiver serviceListener = new ServiceBroadcastReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(serviceListener, serviceFilter);
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
                    TextView balanceText = (TextView)findViewById(R.id.mainTextView);
                    balanceText.setText("Alok, your balance is: " + balance);
            }
        }
    }
}
