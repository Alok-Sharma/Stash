package aloksharma.ufl.edu.stash;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;


public class ViewStashActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    String stashObjectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_stash);
//        viewStashFunctionality();
    }

    private void viewStashFunctionality() {
        Intent incomingIntent = getIntent();
        stashObjectId = incomingIntent.getStringExtra("objectId");

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(HomeActivity.stashName);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(HomeActivity.stashColor));

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setBackgroundColor(HomeActivity.stashColor);

        FloatingActionButton addAccountFAB = (FloatingActionButton) findViewById(R.id.fabAddMoney);
        addAccountFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddMoneyDialog(v, stashObjectId);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewStashFunctionality();
        Log.d("StashLog", "onresume called for view stash");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_deleteStash:
                ParseQuery<ParseObject> removeQuery = ParseQuery.getQuery("Stash");
                removeQuery.getInBackground(stashObjectId, new GetCallback<ParseObject>() {
                    public void done(ParseObject stashObject, ParseException e) {
                        if (e == null) {
                            try {
                                stashObject.delete();
                            } catch (ParseException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                });
                /*Intent serverIntent = new Intent(this, ServerAccess.class);
                serverIntent.putExtra("server_action", ServerAccess.ServerAction.DELETE_STASH.toString());
                serverIntent.putExtra("stashObjectId", stashObjectId);
                startService(serverIntent);*/
                try {
                    Thread.sleep(350);
                    Intent homeActivity = new Intent(this, HomeActivity.class);
                    homeActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(homeActivity);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return true;
            case android.R.id.home:
                Intent intent = new Intent(this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Reuse the existing instance
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.view_stash, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter
                (getSupportFragmentManager());
        MoneyGoalsFragment moneyFragment = new MoneyGoalsFragment();
        TimeGoalsFragment timeFragment = new TimeGoalsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("stashObjectId", stashObjectId);
        moneyFragment.setArguments(bundle);
        timeFragment.setArguments(bundle);

        adapter.addFragment(moneyFragment, "MONEY GOALS");
        adapter.addFragment(timeFragment, "TIME GOALS");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    public void showAddMoneyDialog(View view, String stashObjectId) {
        android.app.FragmentManager fm = getFragmentManager();
        AddMoneyFragment addMoneyDialog = new AddMoneyFragment();
        Bundle bundle = new Bundle();
        bundle.putString("stashObjectId", stashObjectId);
        addMoneyDialog.setArguments(bundle);
        addMoneyDialog.show(fm, "fragment_edit_name");
    }
}
