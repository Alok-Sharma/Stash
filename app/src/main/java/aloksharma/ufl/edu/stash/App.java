package aloksharma.ufl.edu.stash;

import android.app.Application;

import com.parse.Parse;
import com.plaid.client.PlaidClients;
import com.plaid.client.PlaidUserClient;

/**
 * Created by Alok on 9/23/2015.
 */
public class App extends Application {
    PlaidUserClient plaidUserClient;
    @Override
    public void onCreate() {
        super.onCreate();

        //Parse
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, getString(R.string.parse_applicationId), getString(R.string.parse_clientKey));

        //Plaid
        plaidUserClient = PlaidClients.testUserClient("test_id", "test_secret");
    }
}
