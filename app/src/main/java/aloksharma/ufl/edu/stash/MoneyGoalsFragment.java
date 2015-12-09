package aloksharma.ufl.edu.stash;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MoneyGoalsFragment extends Fragment {

    HomeActivity homeActivity = new HomeActivity();
    SharedPreferences sharedPreferences;
    String ruleAsString;

    public MoneyGoalsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle incomingBundle = getArguments();
        final String stashObjectId;
        if(incomingBundle != null) {
            stashObjectId = incomingBundle.getString("stashObjectId");
        } else {
            stashObjectId = null;
        }
        sharedPreferences = getActivity().getSharedPreferences("stashData", 0);
        ruleAsString = sharedPreferences.getString("rule-" + stashObjectId, "No rule set for adding money.");
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
        TextView rule = (TextView)view.findViewById(R.id.moneyGoalsRule);
        rule.setText(ruleAsString);
        return view;
    }
}
