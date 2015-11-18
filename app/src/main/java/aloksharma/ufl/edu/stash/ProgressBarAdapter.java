package aloksharma.ufl.edu.stash;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.ParseObject;

import java.util.ArrayList;

public class ProgressBarAdapter extends BaseAdapter {

    private Context mContext;
    static int colorCounter = -1;
    HomeActivity homeActivity = new HomeActivity();

    public ProgressBarAdapter(Context c) {
        mContext = c;
    }

    @Override
    public int getCount() {
        return homeActivity.gridObjectList.size();
    }

    @Override
    public Object getItem(int arg0) {
        if (homeActivity.gridObjectList instanceof ArrayList) {
            return ((ArrayList<ParseObject>) homeActivity.gridObjectList)
                    .get(arg0);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View grid;
        if (convertView == null) {
            grid = new View(mContext);
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            grid = inflater.inflate(R.layout.grid_items, parent, false);

        } else {
            grid = (View) convertView;
        }


        HoloCircularProgressBar holoCircularProgressBar =
                (HoloCircularProgressBar) grid.findViewById(R.id
                        .holoCircularProgressBar);
        int stashPercent = Math.round(((homeActivity.gridObjectList.get
                (position).getInt("StashGoal")) * 100) / homeActivity
                .saveAmount);
        holoCircularProgressBar.setProgress((float) (homeActivity
                .gridObjectList.get(position).getInt("StashGoal")) /
                homeActivity.saveAmount);
        TextView stashPercentage = (TextView) grid.findViewById(R.id
                .stashPercent);
        stashPercentage.setText(stashPercent + "%");
        TextView stashName = (TextView) grid.findViewById(R.id.stashName);
        stashName.setText(homeActivity.gridObjectList.get(position)
                .getString("StashName"));
        ArrayList<Integer> colorList = new ArrayList<Integer>();
        colorList.add(mContext.getResources().getColor(R.color.yellow));
        colorList.add(mContext.getResources().getColor(R.color.dark_blue));
        colorList.add(mContext.getResources().getColor(R.color.orange));
        colorList.add(mContext.getResources().getColor(R.color.pink));

        colorCounter = position % 4;
        int randomColor = colorList.get(colorCounter);
        holoCircularProgressBar.setProgressColor(randomColor);
        stashName.setTextColor(randomColor);
        stashPercentage.setTextColor(randomColor);
        LinearLayout stashBorder = (LinearLayout) grid.findViewById(R.id
                .stashBorder);
        stashBorder.setBackgroundColor(randomColor);

        return grid;
    }

}

