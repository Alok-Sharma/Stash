package aloksharma.ufl.edu.stash;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MoneyGoalsFragment extends Fragment {

    HomeActivity homeActivity = new HomeActivity();

    public MoneyGoalsFragment() {
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
        View view = inflater.inflate(R.layout.activity_money_goals_fragment,
                container, false);

        TextView moneyGoalsGoalValue = (TextView) view.findViewById(R.id
                .moneyGoalsGoalValue);
        moneyGoalsGoalValue.setText(homeActivity.moneyGoalsGoalValue);
        TextView moneyGoalsMonthlySavings = (TextView) view.findViewById(R
                .id.moneyGoalsMonthlySavings);
        moneyGoalsMonthlySavings.setText(homeActivity
                .moneyGoalsMonthlySavings);
        TextView moneyGoalsPercentage = (TextView) view.findViewById(R.id
                .moneyGoalsPercentage);
        moneyGoalsPercentage.setText(homeActivity.moneyGoalsPercentage);
        TextView moneyGoalsToSaveAmount = (TextView) view.findViewById(R.id
                .moneyGoalsToSaveAmount);
        moneyGoalsToSaveAmount.setText(homeActivity.moneyGoalsToSaveAmount);
        HoloCircularProgressBar moneyGoalsProgressBar =
                (HoloCircularProgressBar) view.findViewById(R.id
                        .moneyGoalsProgressBar);
        moneyGoalsProgressBar.setProgress(homeActivity.moneyGoalsProgressBar);

        return view;
    }
}
