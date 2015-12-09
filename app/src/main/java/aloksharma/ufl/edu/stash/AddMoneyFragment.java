package aloksharma.ufl.edu.stash;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
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
    View addMoneyView;

    final Calendar calendar = Calendar.getInstance();
    int year = calendar.get(Calendar.YEAR);
    int month = calendar.get(Calendar.MONTH);
    int day = calendar.get(Calendar.DAY_OF_MONTH);

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

        Bundle incomingBundle = getArguments();
        final String stashObjectId;
        if(incomingBundle != null) {
            stashObjectId = incomingBundle.getString("stashObjectId");
        } else {
            stashObjectId = null;
        }

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
        Double balanceString = Math.floor((Double.valueOf(sharedPref.getString("balance", "-1")))* 100) / 100;
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

        amountValueField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus && amountValueField.getText().toString().trim().equals("")) {
                    amountValueField.setText("0");
                }
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
                .setPositiveButton("Done", null)
                .setNegativeButton("Cancel", null);

        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button positiveButton = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        String addAmountString = amountValueField.getText().toString().trim();
                        String repeatOnDateString = repeatOnDate.getText().toString().trim();
                        String addPeriod = addPeriodSpinner.getSelectedItem().toString();
                        String endEvent = endEventSpinner.getSelectedItem().toString();
//                        String[] endEventOptions = getResources().getStringArray(R.array.endEventOptions);

                        if (addAmountString.equals("")) {
                            amountValueField.setError("Enter the amount to add.");
                        } else if (repeatOnDateString.equals("") && repeatOnDate.getVisibility() != View.GONE) {
                            repeatOnDate.setError("Enter the target date.");
                        } else {
                            // TODO send below data back to the calling activity.
                            double addAmount = Double.parseDouble(addAmountString);
                            Log.d("StashLog", "Alok " + addPeriod + " " + stashObjectId + " " + addAmount);
                            Intent serverIntent = new Intent(getActivity(), ServerAccess.class);
                            serverIntent.putExtra("stashObjectId", stashObjectId);
                            serverIntent.putExtra("addAmount", addAmount);

                            if(addPeriod.equals("One Time")) {
                                serverIntent.putExtra("server_action", ServerAccess.ServerAction.ADD_MONEY.toString());
                            } else if(addPeriod.equals("Monthly")) {
                                serverIntent.putExtra("server_action", ServerAccess.ServerAction.ADD_RULE.toString());
                                serverIntent.putExtra("repeatOnDate", repeatOnDateString);
                                serverIntent.putExtra("endOnEvent", endEvent);
                            }
                            getActivity().startService(serverIntent);
                                try {
                                    Thread.sleep(350);
                                    Intent homeActivity = new Intent(getActivity().getApplicationContext(), HomeActivity.class);
                                    homeActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(homeActivity);
                                    dismiss();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            dismiss();
                        }
                    }
                });
                Button negativeButton = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_NEGATIVE);
                // same for negative (and/or neutral) button if required
            }
        });

        return dialog;
    }

    private DatePickerDialog.OnDateSetListener dpickerListener
            = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int Year, int Month, int Day) {
            year = Year;
            month = Month + 1;
            day = Day;
            repeatOnDate.setText(month + "/" + day + "/" + year);
        }
    };
}
