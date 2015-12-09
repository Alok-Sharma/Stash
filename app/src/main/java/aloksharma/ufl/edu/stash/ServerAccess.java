package aloksharma.ufl.edu.stash;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Alok on 9/26/2015.
 */
public class ServerAccess extends IntentService {

    PlaidHelper plaidHelper;

    public enum ServerAction {
        ADD_USER, ADD_STASH, GET_BALANCE, ADD_MONEY, DELETE_BANK,
        DELETE_STASH, UPDATE_PROFILE, MFA_QUESTION
    }

    public ServerAccess() {
        super("ServerAccess");
    }

    @Override
    protected void onHandleIntent(Intent incomingIntent) {
        String action = incomingIntent.getStringExtra("server_action");
        ServerAction serverAction = ServerAction.valueOf(action);
        Intent outgoingIntent = new Intent("server_response");
        outgoingIntent.putExtra("server_response", action);
        plaidHelper = new PlaidHelper(this);
        BankMappingHelper bankMappingHelper = new BankMappingHelper(this);

        switch (serverAction) {
            case ADD_STASH:
                String StashName = incomingIntent.getStringExtra("StashName");
                String StashTargetDate = incomingIntent.getStringExtra
                        ("StashTargetDate");
                int StashGoal = incomingIntent.getIntExtra("StashGoal", 0);
                int StashValue = incomingIntent.getIntExtra("StashValue", 0);

                //Push data to your function
                addStash(StashName, StashTargetDate, StashGoal, StashValue);
                Intent homeActivity = new Intent(this, HomeActivity.class);
                homeActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(homeActivity);
                break;

            case UPDATE_PROFILE:
                String User_Name = incomingIntent.getStringExtra("User_Name");
                String User_Email = incomingIntent.getStringExtra
                        ("User_Email");
                String User_Password = incomingIntent.getStringExtra
                        ("User_Password");
                //Push data to your function
                updateprofile(User_Name, User_Email, User_Password);
                homeActivity = new Intent(this, HomeActivity.class);
                homeActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(homeActivity);
                break;

            case ADD_USER:
                //GET_BALANCE creates a new bank account if the incoming
                // intent has username, password and bank name.

            case ADD_MONEY:
                //Add money to a stash.
                String stashObjectId = incomingIntent.getStringExtra
                        ("stashObjectId");
                final Double addAmount = incomingIntent.getDoubleExtra
                        ("addAmount", 0.0);

                ParseQuery<ParseObject> query = ParseQuery.getQuery("Stash");
                query.getInBackground(stashObjectId, new
                        GetCallback<ParseObject>() {
                            public void done(ParseObject stashObject,
                                             ParseException
                                                     e) {
                                if (e == null) {
                                    double currentValue = stashObject.getDouble
                                            ("StashValue");
                                    double newValue = currentValue + addAmount;
                                    stashObject.put("StashValue", newValue);
                                    stashObject.saveInBackground();
                                } else {
                                    // something went wrong
                                    Log.e("StashLog", e.getMessage());
                                    e.printStackTrace();
                                }
                            }
                        });
                break;

            case GET_BALANCE:
                //Make appropriate getBankBalance call depending if
                // username/password is available in the intent.
                String bankUsername = incomingIntent.getStringExtra
                        ("bankUsername");

                Map<String, String> accessTokens = null;
                if (bankUsername == null) {
                    //no username, password. Use access token.
                    accessTokens = plaidHelper.getAccessTokenMapDecrypted();
                    if (accessTokens == null) {
                        // no banks associated yet.
                        outgoingIntent.putExtra("error", "no_bank");
                    } else {
                        try {
                            String accessToken = accessTokens.get("wells");
                            // TODO: Only getting wells fargo balance.
                            // Iterate and get all (Alok)
                            Double balance = plaidHelper.getBankBalance
                                    (accessToken);
                            //making a copy of Map because I'm able to send
                            // only HashMap
                            HashMap<String, String> banks = new HashMap<>
                                    (accessTokens);
                            outgoingIntent.putExtra("map", banks);
                            if (balance != null) {
                                outgoingIntent.putExtra("balance", balance);
                                outgoingIntent.putExtra("finalBalance", "yes");
                            } else {
                                outgoingIntent.putExtra("error", "no_keys");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    //found a username, password.
                    String bankPassword = incomingIntent.getStringExtra
                            ("bankPassword");
                    String bankName = incomingIntent.getStringExtra
                            ("bankName");
                    Double balance = plaidHelper.getBankBalance
                            (bankUsername, bankPassword, bankName);
                    Log.d("StashLog", "balance: " + balance);
                    if (balance == -2.0) {
                        outgoingIntent.putExtra("mfaRequired", "yes");
                        PlaidHelper.Response resp = plaidHelper.getMFAQuestion
                                (bankUsername, bankPassword, bankName);
                        outgoingIntent.putExtra("question", resp.question);
                        outgoingIntent.putExtra("responseCode", String
                                .valueOf(resp
                                .responseCode));
                        outgoingIntent.putExtra("access_token", resp
                                .access_token);
                        outgoingIntent.putExtra("username", bankUsername);
                        outgoingIntent.putExtra("password", bankPassword);
                        outgoingIntent.putExtra("type", bankName);
                    } else {
                        outgoingIntent.putExtra("balance", balance);
                        outgoingIntent.putExtra("finalBalance", "yes");
                    }
                }
                break;

            case MFA_QUESTION:
                String access_token = incomingIntent.getStringExtra
                        ("access_token");
                String answer = incomingIntent.getStringExtra("answer");
                String username = incomingIntent.getStringExtra("username");
                String password = incomingIntent.getStringExtra("password");
                String type = incomingIntent.getStringExtra("type");
                PlaidHelper.Response resp = plaidHelper.postMFAAnswer
                        (username, password, type, access_token, answer);
                if (resp.responseCode == 201) {
                    outgoingIntent.putExtra("question", resp.question);
                    outgoingIntent.putExtra("responseCode", String.valueOf(resp
                            .responseCode));
                    outgoingIntent.putExtra("access_token", resp
                            .access_token);
                    outgoingIntent.putExtra("username", username);
                    outgoingIntent.putExtra("password", password);
                    outgoingIntent.putExtra("type", type);
                } else if (resp.responseCode == 200) {
                    outgoingIntent.putExtra("responseCode", String.valueOf(resp
                            .responseCode));
                    outgoingIntent.putExtra("finalBalance", "yes");
                    outgoingIntent.putExtra("balance", resp.balance);
                }


            case DELETE_BANK:
                String bankName = incomingIntent.getStringExtra("BankName");
                String bankCode = bankMappingHelper.getBankCode(bankName);
                HashMap<String, String> bankMap = new HashMap<>(plaidHelper
                        .getAccessTokenMapEncrypted());
                bankMap.remove(bankCode);
                ParseUser.getCurrentUser().put("BankMap", bankMap);
                ParseUser.getCurrentUser().saveInBackground();
                ParseUser.getCurrentUser().pinInBackground();
                break;

            case DELETE_STASH:
                String removeStashId = incomingIntent.getStringExtra
                        ("stashObjectId");
                ParseQuery<ParseObject> removeQuery = ParseQuery.getQuery
                        ("Stash");
                removeQuery.getInBackground(removeStashId, new
                        GetCallback<ParseObject>() {
                            public void done(ParseObject stashObject,
                                             ParseException
                                                     e) {
                                if (e == null) {
                                    try {
                                        stashObject.delete();
                                    } catch (ParseException e1) {
                                        e1.printStackTrace();
                                    }
                                }
                            }
                        });
                break;
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(outgoingIntent);
    }

    public void addUser() {
        //Stub for adding a new user to Parse. Will be used on registration.
    }

    public void getUser() {
        //Stub to get the Parse user object.
    }

    /**
     * Add Stash Functionality
     */
    public void addStash(String StashName, String StashTargetDate, int
            StashGoal, int StashValue) {
        //Stub to create Stash
        Log.d("CreateStashLog1", StashName);
        Log.d("CreateStashLog2", StashTargetDate);
        Log.d("CreateStashLog3", "" + StashGoal);
        Log.d("CreateStashLog4", "" + StashValue);

        /**Send to Parse Database*/
        final ParseObject Stash = new ParseObject("Stash");
        Stash.put("StashName", StashName);
        Stash.put("StashTargetDate", StashTargetDate);
        Stash.put("StashGoal", StashGoal);
        Stash.put("StashValue", StashValue);

        /*Link the ParseUser object with the Stash object*/
        ParseUser currentUser = ParseUser.getCurrentUser();
        Stash.put("user", ParseUser.getCurrentUser());     //create a user
        // relation with the current user
        Stash.saveInBackground(new SaveCallback() {

            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.i("Success", "11");
                } else {
                    Log.i("Fail", "22");
                }
            }
        });

        /**
         * My Stashes Functionality - Populating the list of Stash Fields;
         * that query should be via a button
         */
        /**Create query for objects of type "Stash"*/
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Stash");

        /**Restrict to cases where the user is the current user**/
        query.whereEqualTo("user", ParseUser.getCurrentUser());

        final ArrayList Stash_List = new ArrayList();
        query.findInBackground(new FindCallback<ParseObject>() {

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

    /**
     * Remove Stash Functionality
     */
    public void removeStash(ParseObject Stash) {
        Stash.deleteInBackground();
    }

    public void updateprofile(String User_Name, String User_Email, String
            User_Password) {
        /**Send to Parse Database*/
        final ParseObject Stash = new ParseObject("Stash");
        ParseUser currentuser = ParseUser.getCurrentUser();

        if(User_Name!= null && !User_Name.isEmpty()){
            int firstSpace = User_Name.indexOf(" ");
            String firstName = User_Name.substring(0, firstSpace);
            String lastName = User_Name.substring(firstSpace).trim();

            currentuser.put("firstName",firstName);
            currentuser.put("lastName",lastName);
        }

        if(User_Password!= null && !User_Password.isEmpty()){
               currentuser.setPassword(User_Password); }

        if(User_Email!= null && !User_Email.isEmpty()){
            currentuser.setEmail(User_Email);}

        currentuser.saveInBackground();
    }
}