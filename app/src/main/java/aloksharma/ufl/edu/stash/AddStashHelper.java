package aloksharma.ufl.edu.stash;

/**
 * Created by amazon on 10/7/15.
 */
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.view.MenuItem;
import android.widget.TextView;



public class AddStashHelper extends Activity {


    public final static String EXTRA_MESSAGE = "aloksharma.ufl.edu.stash.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_display_message);

        //Get the message from the intent
        Intent intent = getIntent();
        String message = intent.getStringExtra(AddStash.EXTRA_MESSAGE);

        //Log.i("debug", "made it to new class");

        //create the text view
        TextView textView = new TextView(this);
        textView.setTextSize(40);
        textView.setText(message);

        //set the text view

        //ViewPager.LayoutParams layout = new ViewPager.LayoutParams();
        //addContentView(textView, layout);
        setContentView(textView);
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

}

