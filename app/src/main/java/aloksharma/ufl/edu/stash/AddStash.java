package aloksharma.ufl.edu.stash;

/**
 * Created by amazon on 10/7/15.
 */

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import java.util.Calendar;


public class AddStash extends DrawerActivity {

    public final static String EXTRA_MESSAGE = "aloksharma.ufl.edu.stash" +
            ".MESSAGE";

    EditText targetdate;
    EditText stashname;
    ImageView calendarImage;
    EditText goal;
    EditText stashvlaue;
    final Calendar calendar = Calendar.getInstance();
    int year = calendar.get(Calendar.YEAR);
    int month = calendar.get(Calendar.MONTH);
    int day = calendar.get(Calendar.DAY_OF_MONTH);
    static final int DIALOG_ID = 0;

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_addstash);
        stashname = (EditText) findViewById(R.id.edit_stashname);
        goal = (EditText) findViewById(R.id.edit_goal);
        calendarImage = (ImageView)findViewById(R.id.calendarImageAddStash);
        int color = Color.parseColor("#939393");
        calendarImage.setColorFilter(color);
        targetdate = (EditText) findViewById(R.id.edit_targetdate);
        stashvlaue = (EditText) findViewById(R.id.edit_stashvalue);
        targetdate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                showDialog(DIALOG_ID);
                return true;
            }
        });
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_ID)
            return new DatePickerDialog(this, dpickerListener, year, month,
                    day);
        return null;
    }

    private DatePickerDialog.OnDateSetListener dpickerListener
            = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int Year, int Month, int Day) {
            year = Year;
            month = Month;
            day = Day;
            targetdate.setText(day + "/" + month + "/" + year);
        }
    };

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is
        // present.
        getMenuInflater().inflate(R.menu.menu_my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Called when the user clicks the Create Stash button
     */
    public void CreateStash(View view) {

        /**Getting all user inputs in variable*/
        String message_stashname = stashname.getText().toString();
        String message_targetdate = targetdate.getText().toString();
        int message_goal = Integer.parseInt(goal.getText().toString());
        int message_stashvalue = Integer.parseInt(stashvlaue.getText()
                .toString());

        /**Create an intent*/
        Intent addstash_intent = new Intent(this, ServerAccess.class);

        /**Attaching Data to that intent*/
        addstash_intent.putExtra("StashName", message_stashname);
        addstash_intent.putExtra("StashTargetDate", message_targetdate);
        addstash_intent.putExtra("StashGoal", message_goal);
        addstash_intent.putExtra("StashValue", message_stashvalue);
        addstash_intent.putExtra("server_action", ServerAccess.ServerAction
                .ADD_STASH.toString());

        /**Launching that intent*/
        this.startService(addstash_intent);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        finish();
    }

    public void showAddMoneyDialog(View view) {
        FragmentManager fm = getFragmentManager();
        AddMoneyFragment addMoneyDialog = new AddMoneyFragment();
        addMoneyDialog.show(fm, "fragment_edit_name");
    }
}