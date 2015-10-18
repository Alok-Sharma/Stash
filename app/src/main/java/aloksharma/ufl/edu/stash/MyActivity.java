package aloksharma.ufl.edu.stash;

/**
 * Created by amazon on 10/7/15.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;


import com.parse.ParseUser;

public class MyActivity extends Activity {


    public final static String EXTRA_MESSAGE = "aloksharma.ufl.edu.stash.MESSAGE";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        ParseUser.logInInBackground("alok.sharma127@gmail.com", "aloksharma");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /** Called when the user clicks the Create Stash button */
    public void CreateStash(View view) {

        //Getting all user inputs in variable//
        /***********************************************************************/
        EditText stashname = (EditText) findViewById(R.id.edit_stashname);  //reading the user input for stash name i.e. edit_stashname
        EditText targetdate = (EditText) findViewById(R.id.edit_targetdate);  //reading the user input for stash targetdate i.e. edit_targetdate
        EditText goal = (EditText) findViewById(R.id.edit_goal);  //reading the user input for stash goal value i.e. edit_goal


        String message_stashname = stashname.getText().toString();
        String message_targetdate = targetdate.getText().toString();
        int message_goal = Integer.parseInt(goal.getText().toString());
        /***********************************************************************/

        //Create an intent
        //Intent addstash_intent = new Intent(this, DisplayMessageActivity.class);
        Intent addstash_intent = new Intent(this, ServerAccess.class);


        //Attaching Data to that intent//
        /*************************************************************/
        addstash_intent.putExtra("StashName", message_stashname);
        addstash_intent.putExtra("StashTargetDate", message_targetdate);
        addstash_intent.putExtra("StashGoal", message_goal);
        addstash_intent.putExtra("server_action", ServerAccess.ServerAction.ADD_STASH.toString());

        //Launching that intent//
        /*************************************************************/
        //startActivity(addstash_intent);
        this.startService(addstash_intent);


    }
}
