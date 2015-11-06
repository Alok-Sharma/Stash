package aloksharma.ufl.edu.stash;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.graphics.Color;

import com.parse.ParseObject;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class ProgressBarAdapter extends BaseAdapter {

    private Context mContext;
    static int i = 1;
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
        if(homeActivity.gridObjectList instanceof ArrayList) {
            return ((ArrayList<ParseObject>)homeActivity.gridObjectList).get(arg0);
        }
        else{
            return null;
        }
        //homeActivity.gridObjectList[arg0];
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View grid;

        if(convertView==null){
            grid = new View(mContext);
            grid.setLayoutParams(new GridView.LayoutParams(85, 85));
            grid.setScaleX(85);
            grid.setScaleY(85);
            /*HoloCircularProgressBar holoCircularProgressBar = (HoloCircularProgressBar)findViewById(R.id.holoCircularProgressBar);
            int stashPercent = Math.round((float)(homeActivity.gridObjectList.get(homeActivity.gridObjectList.size()-i).getInt("StashGoal"))/homeActivity.saveAmount);
            holoCircularProgressBar.setProgress((float)(homeActivity.gridObjectList.get(homeActivity.gridObjectList.size()-i).getInt("StashGoal"))/homeActivity.saveAmount);
            i++;*/
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            grid=inflater.inflate(R.layout.grid_items, parent, false);

            HoloCircularProgressBar holoCircularProgressBar = (HoloCircularProgressBar) grid.findViewById(R.id.holoCircularProgressBar);
            int stashPercent = Math.round(((homeActivity.gridObjectList.get(homeActivity.gridObjectList.size() - i).getInt("StashGoal")) * 100) / homeActivity.saveAmount);
            holoCircularProgressBar.setProgress((float) (homeActivity.gridObjectList.get(homeActivity.gridObjectList.size() - i).getInt("StashGoal")) / homeActivity.saveAmount);
            TextView stashPercentage = (TextView) grid.findViewById(R.id.stashPercent);
            stashPercentage.setText(stashPercent+"%");
            TextView stashName = (TextView)grid.findViewById(R.id.stashName);
            stashName.setText(homeActivity.gridObjectList.get(homeActivity.gridObjectList.size() - i).getString("StashName"));

            ArrayList<Integer>  colorList = new ArrayList<Integer>();
            colorList.add(-956847);
            colorList.add(-347881);
            colorList.add(-13738326);
            colorList.add(-1556359);
            /*Random rand = new Random();
            int r = rand.nextInt(255);
            int g = rand.nextInt(255);
            int b = rand.nextInt(255);
            int randomColor = Color.rgb(r,g,b);*/
            Collections.shuffle(colorList);
            int randomColor = colorList.get(0);
            holoCircularProgressBar.setProgressColor(randomColor);
            stashName.setTextColor(randomColor);
            stashPercentage.setTextColor(randomColor);
            LinearLayout stashBorder = (LinearLayout)grid.findViewById(R.id.stashBorder);
            stashBorder.setBackgroundColor(randomColor);

            holoCircularProgressBar.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {

                }
            });

                i++;
        }else{
            grid = (View)convertView;
        }

        //ImageView imageView = (ImageView)grid.findViewById(R.id.image);
        //imageView.setImageResource(mThumbIds[position]);

        return grid;
    }

}

