package aloksharma.ufl.edu.stash;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.Calendar;

/**
 * Created by Alok on 11/15/2015.
 */
public class AddMoneyFragment extends DialogFragment {

    EditText repeatOnDate;
    ImageView calendarImage;
    final Calendar calendar = Calendar.getInstance();
    int year = calendar.get(Calendar.YEAR);
    int month = calendar.get(Calendar.MONTH);
    int day = calendar.get(Calendar.DAY_OF_MONTH);

    public AddMoneyFragment() {

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View addMoneyView = inflater.inflate(R.layout.fragment_add_money, null);

        calendarImage = (ImageView)addMoneyView.findViewById(R.id.calendarImage);
        int color = Color.parseColor("#939393");
        calendarImage.setColorFilter(color);

        repeatOnDate = (EditText)addMoneyView.findViewById(R.id.repeatOnDate);
        repeatOnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dateDialog = new DatePickerDialog(getActivity(), dpickerListener, year, month,
                        day);
                dateDialog.show();
            }
        });

        builder.setView(addMoneyView)
                .setPositiveButton("", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //start the service.
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
            repeatOnDate.setText(day + "/" + month + "/" + year);
        }
    };
}
