package aloksharma.ufl.edu.stash;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TimeGoalsFragment extends Fragment {

    HomeActivity homeActivity = new HomeActivity();

    public TimeGoalsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_time_goals_fragment,
                container, false);

        TextView timeGoalsGoalValue = (TextView) view.findViewById(R.id
                .timeGoalsGoalValue);
        timeGoalsGoalValue.setText(homeActivity.timeGoalsGoalValue);
        TextView timeGoalsMonthlySavings = (TextView) view.findViewById(R.id
                .timeGoalsMonthlySavings);
        timeGoalsMonthlySavings.setText(homeActivity.timeGoalsMonthlySavings);
        TextView timeGoalsPercentage = (TextView) view.findViewById(R.id
                .timeGoalsPercentage);
        timeGoalsPercentage.setText(homeActivity.timeGoalsPercentage);
        TextView timeGoalsToSaveAmount = (TextView) view.findViewById(R.id
                .timeGoalsToSaveAmount);
        timeGoalsToSaveAmount.setText(homeActivity.timeGoalsToSaveAmount);
        HoloCircularProgressBar timeGoalsProgressBar =
                (HoloCircularProgressBar) view.findViewById(R.id
                        .timeGoalsProgressBar);
        timeGoalsProgressBar.setProgress(homeActivity.timeGoalsProgressBar);

        return view;
    }
}
