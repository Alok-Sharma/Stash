package aloksharma.ufl.edu.stash;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Calendar;

/**
 * Created by Alok on 11/15/2015.
 */
public class AddMoneyFragment extends DialogFragment {

    SharedPreferences sharedPref;

    EditText repeatOnDate;
    ImageView calendarImage;
    Spinner addPeriodSpinner;
    Spinner endEventSpinner;
    EditText amountValueField;
    TextView currentBalanceText;
    TextView repeatOnDateText;
    TextView endEventText;

    final Calendar calendar = Calendar.getInstance();
    int year = calendar.get(Calendar.YEAR);
    int month = calendar.get(Calendar.MONTH);
    int day = calendar.get(Calendar.DAY_OF_MONTH);

    View addMoneyView;

    public AddMoneyFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        sharedPref = getActivity().getSharedPreferences("stashData", 0);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        addMoneyView = inflater.inflate(R.layout.fragment_add_money, null);

        calendarImage = (ImageView)addMoneyView.findViewById(R.id.calendarImage);
        int color = Color.parseColor("#939393");
        calendarImage.setColorFilter(color);

        amountValueField = (EditText)addMoneyView.findViewById(R.id.addAmount);
        endEventSpinner = (Spinner)addMoneyView.findViewById(R.id.endEvent);
        endEventText = (TextView)addMoneyView.findViewById(R.id.endText);
        repeatOnDateText = (TextView)addMoneyView.findViewById(R.id.repeatOnText);

        currentBalanceText = (TextView)addMoneyView.findViewById(R.id.currentBalance);
        String balanceString = sharedPref.getString("balance", "-1");
        currentBalanceText.setText("(Current balance: " + balanceString + ")");

        repeatOnDate = (EditText)addMoneyView.findViewById(R.id.repeatOnDate);
        repeatOnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dateDialog = new DatePickerDialog(getActivity(), dpickerListener, year, month,
                        day);
                dateDialog.show();
            }
        });

        addPeriodSpinner = (Spinner)addMoneyView.findViewById(R.id.timePeriod);
        addPeriodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selected = adapterView.getItemAtPosition(i).toString();
                if (selected.equals("Monthly")) {
                    repeatOnDate.setVisibility(View.VISIBLE);
                    repeatOnDateText.setVisibility(View.VISIBLE);
                    calendarImage.setVisibility(View.VISIBLE);
                    endEventSpinner.setVisibility(View.VISIBLE);
                    endEventText.setVisibility(View.VISIBLE);
                } else if (selected.equals("One Time")) {
                    repeatOnDate.setVisibility(View.GONE);
                    repeatOnDateText.setVisibility(View.GONE);
                    calendarImage.setVisibility(View.GONE);
                    endEventSpinner.setVisibility(View.GONE);
                    endEventText.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        builder.setView(addMoneyView)
                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String addPeriod = addPeriodSpinner.getSelectedItem().toString();
                        double addAmount = Double.parseDouble(amountValueField.getText().toString());
                        String endEvent = endEventSpinner.getSelectedItem().toString();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AddMoneyFragment.this.getDialog().cancel();
                    }
                });

        return builder.create();
    }

    private DatePickerDialog.OnDateSetListener dpickerListener
            = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int Year, int Month, int Day) {
            year = Year;
            month = Month;
            day = Day;
            repeatOnDate.setText(month + "/" + day + "/" + year);
        }
    };
}
