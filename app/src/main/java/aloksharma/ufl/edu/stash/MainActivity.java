package aloksharma.ufl.edu.stash;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.plaid.client.request.Credentials;
import com.plaid.client.response.Transaction;
import com.plaid.client.response.TransactionsResponse;

import java.util.List;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        App app = (App)getApplicationContext(); //Get all variables in App.java

        Credentials testCredentials = new Credentials("plaid_test", "plaid_good");
        TransactionsResponse response = app.plaidUserClient.addUser(testCredentials, "amex", "test@test.com", null);
        List<Transaction> transactions = response.getTransactions();
        Log.d("Alok", "" + transactions);
    }
}
