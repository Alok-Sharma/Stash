package aloksharma.ufl.edu.stash;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.facebook.android.crypto.keychain.SharedPrefsBackedKeyChain;
import com.facebook.crypto.Crypto;
import com.facebook.crypto.Entity;
import com.facebook.crypto.util.SystemNativeCryptoLibrary;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Alok on 9/26/2015.
 */
public class ServerAccess extends IntentService {

    Crypto crypto;

    public enum ServerAction {
        ADD_USER, GET_USER, ADD_STASH, GET_BALANCE, GET_ACCESS_TOKEN
    }

    public enum BankName {
        amex, bofa, capone360, schwab, chase, citi, fidelity, nfcu, pnc,
        svb, suntrust,
        td, us, usaa, wells
    }

    public ServerAccess() {
        super("ServerAccess");
        crypto = new Crypto(new SharedPrefsBackedKeyChain(App.getContext()), new SystemNativeCryptoLibrary());
    }

    @Override
    protected void onHandleIntent(Intent incomingIntent) {
        String action = incomingIntent.getStringExtra("server_action");
        ServerAction serverAction = ServerAction.valueOf(action);
        Intent outgoingIntent = new Intent("server_response");
        outgoingIntent.putExtra("server_response", action);

        switch (serverAction) {
            case ADD_STASH:
                //Pulling the data from the incoming intent
                String StashName = incomingIntent.getStringExtra("StashName");
                String StashTargetDate = incomingIntent.getStringExtra("StashTargetDate");
                String StashGoal = incomingIntent.getStringExtra("StashGoal");

                //Push data to your function
                addStash(StashName, StashTargetDate, StashGoal);

               break;

            case ADD_USER:
            case GET_BALANCE:
                //Make appropriate getBankBalance call depending if
                // username/password is available in the intent.
                PlaidHelper plaidHelper = new PlaidHelper(this);
                String bankUsername = incomingIntent.getStringExtra
                        ("bankUsername");
                if (bankUsername == null) {
                    //no username, password. Use access token.
                    Map<String, byte[]> accessTokens = plaidHelper.getAccessTokenMap();
                    try {
                        byte[] encryptedFetch = accessTokens.get("wells");
                        byte[] decryptedFetch = crypto.decrypt(encryptedFetch, new Entity("password")); //TODO: use the users password to encrypt and decrypt.
                        String accessToken = new String(decryptedFetch);
                        Double balance = plaidHelper.getBankBalance(accessToken);
                        Log.d("StashLog", "balance: " + balance);
                        outgoingIntent.putExtra("balance", balance);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    //found a username, password.
                    String bankPassword = incomingIntent.getStringExtra
                            ("bankPassword");
                    String bankName = incomingIntent.getStringExtra
                            ("bankName");
                    Double balance = plaidHelper.getBankBalance(bankUsername,
                            bankPassword, BankName.valueOf(bankName));
                    Log.d("StashLog", "balance: " + balance);
                    outgoingIntent.putExtra("balance", balance);
                }

            case GET_USER:
            case GET_ACCESS_TOKEN:
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(outgoingIntent);
    }

    public void addUser() {
        //Stub for adding a new user to Parse. Will be used on registration.
    }

    public void getUser() {
        //Stub to get the Parse user object.
    }

   /******************Add Stash Functionality******************************************/
    public void addStash(String StashName, String StashTargetDate, String StashGoal){
        //Stub to create Stash
        Log.d("CreateStashLog1",StashName);
        Log.d("CreateStashLog2",StashTargetDate);
        Log.d("CreateStashLog3",StashGoal);

        //Send to Parse Database
        final ParseObject Stash = new ParseObject("Stash");

        Stash.put("StashName", StashName);
        Stash.put("StashTargetDate", StashTargetDate);
        Stash.put("StashGoal", StashGoal);

        //Link the ParseUser object with the Stash object
        ParseUser currentUser = ParseUser.getCurrentUser();

        Stash.put("user", ParseUser.getCurrentUser());     //create a user relation with the current user

        //Stash.saveInBackground();

        Stash.saveInBackground(new SaveCallback() {

            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.i("Success","11");

                } else {
                    Log.i("Fail", "22");
                }
            }

        });
        /******************End of Add Stash Functionality******************************************/


        /******************My Stashes Functionality******************************************/
       //Populating the list of Stash Fields; that query should be via a button

        // Create query for objects of type "Post"
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Stash");

        // Restrict to cases where the user is the current user.
        query.whereEqualTo("user", ParseUser.getCurrentUser());


        final ArrayList Stash_List = new ArrayList();
        // Run the query
        query.findInBackground(new FindCallback<ParseObject>() {                //Ideally should be called on click event such as View Stash

            @Override
            public void done(List<ParseObject> StashList, ParseException e) {
                if (e == null) {
                    // If there are results, update the list of posts
                    // and notify the adapter

                    for (ParseObject Stash : StashList) {
                        String val = Stash.getString("StashName");
                        Stash_List.add(val);
                        Log.i("CreateStashLog4", val);
                        /**Removes All Stashes for this user**///
                        //Stash.deleteInBackground();
                    }
                } else {
                    Log.i("CreateStashLog5", "Error: " + e.getMessage());
                }
            }

        });
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //Log.i("CreateStashLog6", Stash_List.size()+"");
        //removeStash(Stash);                 //Removes the object Stash
    }
    /******************End of My Stashes Functionality******************************************/




    /******************Remove Stash Functionality*********************************************/
    public void removeStash(ParseObject Stash){

        Stash.deleteInBackground();

    }
    /******************End of Remove Stash Functionality*********************************************/

//    private void addAccessToken() {
//        String input = "test_wells";
//        try {
//            byte[] encryptedInput = crypto.encrypt(input.getBytes(), new Entity("password"));
//            Log.d("nikita", "byte: " + encryptedInput);
//            Map<String, byte[]> bankMap = new HashMap<>();
//            bankMap.put("wells", encryptedInput);
//            ParseUser.getCurrentUser().put("BankMap", bankMap);
//            ParseUser.getCurrentUser().save();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
