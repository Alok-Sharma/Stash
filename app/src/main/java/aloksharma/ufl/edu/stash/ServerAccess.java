package aloksharma.ufl.edu.stash;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.format.DateUtils;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Alok on 9/26/2015.
 */
public class ServerAccess extends IntentService {

    PlaidHelper plaidHelper;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public enum ServerAction {
        ADD_USER, ADD_STASH, GET_BALANCE, ADD_MONEY, DELETE_BANK, DELETE_STASH, UPDATE_PROFILE, ALARM,
        ADD_RULE
    }

    public ServerAccess() {
        super("ServerAccess");
    }

    @Override
    protected void onHandleIntent(Intent incomingIntent) {
        sharedPreferences = getSharedPreferences("stashData", 0);
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

                //Push data to your function
                addStash(StashName, StashTargetDate, StashGoal);
                Intent homeActivity = new Intent(this, HomeActivity.class);
                homeActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(homeActivity);
                break;
            case UPDATE_PROFILE:
                String User_Name = incomingIntent.getStringExtra("User_Name");
                String User_Email = incomingIntent.getStringExtra("User_Email");
                String User_Password = incomingIntent.getStringExtra("User_Password");
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
                String stashObjectId = incomingIntent.getStringExtra("stashObjectId");
                Double addAmount = incomingIntent.getDoubleExtra("addAmount", 0.0);
                addMoneyToStash(stashObjectId, addAmount);
                break;
            case ADD_RULE:
                String stashObjectIdAddRule = incomingIntent.getStringExtra("stashObjectId");
                Double addAmountRule = incomingIntent.getDoubleExtra("addAmount", 0.0);
                String repeatOnDateString = incomingIntent.getStringExtra("repeatOnDate");
                String endOnEvent = incomingIntent.getStringExtra("endOn");
                addRule(stashObjectIdAddRule, addAmountRule, repeatOnDateString, endOnEvent);
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
                            double balance = getBalanceFromTokens(accessTokens);
                            //making a copy of Map because I'm able to send only HashMap
                            HashMap<String, String> banks = new HashMap<>(accessTokens);
                            outgoingIntent.putExtra("map", banks);
                            if (balance != -1.0) {
                                outgoingIntent.putExtra("balance", balance);
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
                    Double balance = plaidHelper.getBankBalance(bankUsername,
                            bankPassword, bankName);
                    Log.d("StashLog", "balance: " + balance);
                    outgoingIntent.putExtra("balance", balance);
                }
                break;

            case DELETE_BANK:
                String bankName = incomingIntent.getStringExtra("BankName");
                String bankCode = bankMappingHelper.getBankCode(bankName);
                HashMap<String, String> bankMap = new HashMap<>(plaidHelper.getAccessTokenMapEncrypted());
                bankMap.remove(bankCode);
                ParseUser.getCurrentUser().put("BankMap", bankMap);
                ParseUser.getCurrentUser().saveInBackground();
                ParseUser.getCurrentUser().pinInBackground();
                break;
            case DELETE_STASH:
                String removeStashId = incomingIntent.getStringExtra("stashObjectId");
                ParseQuery<ParseObject> removeQuery = ParseQuery.getQuery("Stash");
                removeQuery.getInBackground(removeStashId, new GetCallback<ParseObject>() {
                    public void done(ParseObject stashObject, ParseException e) {
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
            case ALARM:
                Map<String, String> accessTokensAlarm = plaidHelper.getAccessTokenMapDecrypted();
                if (accessTokensAlarm == null) {
                    // no banks associated yet.
                    outgoingIntent.putExtra("error", "no_bank");
                } else {
                    double savedAmount = 0, effectiveBal;
                    Double balance = getBalanceFromTokens(accessTokensAlarm);
                    List<ParseObject> stashes = getStashes();
                    // for each stash check if todays date is same as autoAddNext date.
                    for (ParseObject stash : stashes) {
                        if (isAutoAddDate(stash) && isEndConditionMet(stash)) {
                            // Today is the date for auto adding money and the end condition is being met.
                            // Delete all auto add fields.
                            stash.remove("AutoAddValue");
                            stash.remove("AutoAddEnd");
                            stash.remove("AutoAddOn");
                            stash.saveInBackground();
                            stash.pinInBackground();
                        } else if (isAutoAddDate(stash) && !isEndConditionMet(stash)) {
                            // Today is the date for adding money, but the end condition is not met.
                            // Add money to stash. Update AutoAddOn date.
                            String objectId = stash.getObjectId();
                            Double autoAddValue = stash.getDouble("AutoAddValue");
                            String autoAddOn = stash.getString("AutoAddOn");

                            addMoneyToStash(objectId, autoAddValue);

                            String newAutoAddOn = incrementMonth(autoAddOn);
                            stash.put("AutoAddOn", newAutoAddOn);
                            stash.saveInBackground();
                            stash.pinInBackground();
                        }
                    }
                    Boolean status = sharedPreferences.getBoolean("notifyStatus", true);
                        effectiveBal = balance - savedAmount;
                    if (status == true && effectiveBal < -1.0) {
                        NotificationCompat.Builder mBuilder =
                                new NotificationCompat.Builder(this)
                                        .setSmallIcon(R.drawable.stashlogo)
                                        .setContentTitle("You're out of cash!")
                                        .setContentText("Your effective balance is less than zero.");
                        // Creates an explicit intent for an Activity in your app

                        Intent resultIntent = new Intent(this, HomeActivity.class);
                        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, 0);

                        mBuilder.setContentIntent(resultPendingIntent);
                        NotificationManager mNotificationManager =
                                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        // mId allows you to update the notification later on.
                        mNotificationManager.notify(1, mBuilder.build());
                    }
                }
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

    private List<ParseObject> getStashes(){
        ParseQuery<ParseObject> stashQuery = ParseQuery.getQuery("Stash");
        stashQuery.whereEqualTo("user", ParseUser.getCurrentUser());
        List<ParseObject> stashList = null;
        try {
            stashList = stashQuery.find();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return stashList;
    }

    /**
     * Get bank balance of all banks within the input map of access tokens.
     * @param accessTokens map of accesstokens for every bank associated with the user.
     * @return total balance.
     */
    private double getBalanceFromTokens(Map<String, String> accessTokens) {
        String accessToken = accessTokens.get("wells"); // TODO: Only getting wells fargo balance. Iterate and get all (Alok)
        Double balance = plaidHelper.getBankBalance(accessToken);
        if(balance != null){
            return balance;
        }
        return -1.0;
    }

    /**
     * Add specified amount of money to the specified stash. The stash here is identified by its object id on Parse.
     * @param stashObjectId Parse object id of the stash to which you want to add money to.
     * @param addAmount Amount to add.
     */
    private void addMoneyToStash(String stashObjectId, final Double addAmount) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Stash");
        query.getInBackground(stashObjectId, new GetCallback<ParseObject>() {
            public void done(ParseObject stashObject, ParseException e) {
                if (e == null) {
                    double currentValue = stashObject.getDouble("StashValue");
                    double newValue = currentValue + addAmount;
                    stashObject.put("StashValue", newValue);
                    stashObject.saveInBackground();
                    stashObject.pinInBackground();
                } else {
                    // something went wrong
                    Log.e("StashLog", e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * For a specific stash, create a new rule for when to automatically add money to tat stash.
     * @param stashObjectId
     * @param amount
     * @param addMoneyOnString
     * @param endOnString
     */
    private void addRule(String stashObjectId, final Double amount, final String addMoneyOnString, final String endOnString) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Stash");
        query.getInBackground(stashObjectId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject stashObject, ParseException e) {
                // TODO: check if e is null first.
                stashObject.put("AutoAddOn", addMoneyOnString);
                stashObject.put("AutoAddValue", amount);
                stashObject.put("AutoAddEnd", endOnString);
                stashObject.saveInBackground();
                stashObject.pinInBackground();
            }
        });
        String ruleAsString = "$" + amount + " will be added on " + addMoneyOnString + ", repeating every month " +
                "until the " + endOnString.toLowerCase();
        editor = sharedPreferences.edit();
        editor.putString("rule-"+stashObjectId, ruleAsString);
        editor.commit();
    }

    /**
     * Checks if todays date is the same as the argument date. Returns true if it is.
     * @param stash The stash to check against today's
     * @return boolean
     */
    private boolean isAutoAddDate(ParseObject stash) {
        String autoAddOn = stash.getString("AutoAddOn");
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        Date autoAddOnDate;
        try {
            autoAddOnDate = dateFormat.parse(autoAddOn);
            return DateUtils.isToday(autoAddOnDate.getTime());
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Checks if end condition was met.
     * @param stash
     * @return
     */
    private boolean isEndConditionMet(ParseObject stash) {
        String endEvent = stash.getString("AutoAddEnd");
        String[] endEventOptions = getResources().getStringArray(R.array.endEventOptions);
        if(endEvent.equals(endEventOptions[0])) {
            //Check if goal amount reached
            Double stashGoal = stash.getDouble("StashGoal");
            Double stashValue = stash.getDouble("StashValue");
            return stashGoal == stashValue;
        } else if(endEvent.equals(endEventOptions[1])) {
            //Check if goal date reached
            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            String targetDateString = stash.getString("StashTargetDate");
            try {
                Date targetDate = dateFormat.parse(targetDateString);
                return DateUtils.isToday(targetDate.getTime());
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private String incrementMonth(String autoAddOn) {
        String[] dateSplit = autoAddOn.split("/");
        String monthString = dateSplit[0];
        Integer month = Integer.parseInt(monthString);
        Integer newMonth = month + 1;
        String newMonthString = newMonth + "";
        String newDate = newMonthString + "/" + dateSplit[1] + "/" + dateSplit[2];
        return newDate;
    }

    /**
     * Add Stash Functionality
     */
    public void addStash(String StashName, String StashTargetDate, int
            StashGoal) {
        //Stub to create Stash
        Log.d("CreateStashLog1", StashName);
        Log.d("CreateStashLog2", StashTargetDate);
        Log.d("CreateStashLog3", "" + StashGoal);

        /**Send to Parse Database*/
        final ParseObject Stash = new ParseObject("Stash");
        Stash.put("StashName", StashName);
        Stash.put("StashTargetDate", StashTargetDate);
        Stash.put("StashGoal", StashGoal);
        Stash.put("StashValue", 0); //A new stash will have an initial value of 0.

        /*Link the ParseUser object with the Stash object*/
        Stash.put("user", ParseUser.getCurrentUser());
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

    public void updateprofile(String User_Name, String User_Email, String User_Password) {
        /**Send to Parse Database*/
        final ParseObject Stash = new ParseObject("Stash");
        ParseUser currentuser = ParseUser.getCurrentUser();
        int firstSpace = User_Name.indexOf(" ");
        String firstName = User_Name.substring(0, firstSpace);
        String lastName = User_Name.substring(firstSpace).trim();

        currentuser.put("firstName",firstName);
        currentuser.put("lastName",lastName);
        currentuser.setPassword(User_Password);
        currentuser.setEmail(User_Email);
        currentuser.saveInBackground();
    }
}