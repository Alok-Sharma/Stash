package aloksharma.ufl.edu.stash;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.parse.ParseFile;
import com.parse.ParseUser;

public class DrawerActivity extends AppCompatActivity {

    private static int fromPosition = 0;
    String TITLES[] = {"Profile", "Bank Accounts", "Add Stash", "Logout"};
    int ICONS[] = {R.drawable.ic_person_black_18dp, R.drawable
            .ic_account_balance_black_18dp, R.drawable
            .ic_add_circle_outline_black_18dp, R.drawable
            .ic_power_settings_new_black_18dp};

    String NAME = null;
    String EMAIL = null;
    Bitmap bitmap;

    private Toolbar toolbar;

    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    public DrawerLayout Drawer;

    ActionBarDrawerToggle mDrawerToggle;
    ParseUser currentUser;


    protected void onCreate(Bundle savedInstanceState, int resLayoutID) {
        super.onCreate(savedInstanceState);
        setContentView(resLayoutID);

        currentUser = ParseUser.getCurrentUser();
        Log.d("Stash: DrawerActivity", "CurrentUser is: " + currentUser
                .getUsername());
        NAME = currentUser.getUsername();
        Log.d("Stash: Drawer Activity:", "User's Name: " + NAME);
        EMAIL = currentUser.getEmail();
        Log.d("Stash: Drawer Activity:", "User's Email: " + EMAIL);
        try {
            ParseFile parseFile = currentUser.getParseFile("profileThumb");
            if (parseFile != null) {
                byte[] data = parseFile.getData();
                bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        mRecyclerView = (RecyclerView) findViewById(R.id.RecyclerView);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new CustomAdapter(TITLES, ICONS, NAME, EMAIL, bitmap);
        mRecyclerView.setAdapter(mAdapter);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        final GestureDetector mGestureDetector = new GestureDetector
                (DrawerActivity.this, new GestureDetector
                        .SimpleOnGestureListener() {

                    @Override
                    public boolean onSingleTapUp(MotionEvent e) {
                        return true;
                    }

                });

        mRecyclerView.addOnItemTouchListener(new RecyclerView
                .OnItemTouchListener() {
            Intent navigateTo;

            @Override
            public boolean onInterceptTouchEvent(RecyclerView recyclerView,
                                                 MotionEvent motionEvent) {
                View child = recyclerView.findChildViewUnder(motionEvent
                        .getX(), motionEvent.getY());

                if (child != null && mGestureDetector.onTouchEvent
                        (motionEvent)) {
                    Drawer.closeDrawers();
                    if (recyclerView.getChildPosition(child) == 1) {
                        navigateTo = new Intent(DrawerActivity.this,
                                ProfileActivity.class);
                        navigateTo.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(navigateTo);
                    }
                    if (recyclerView.getChildPosition(child) == 2) {
                        navigateTo = new Intent(DrawerActivity.this,
                                BankAccountsActivity.class);
                        navigateTo.addFlags(Intent
                                .FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(navigateTo);
                    }
                    if (recyclerView.getChildPosition(child) == 3) {
                        navigateTo = new Intent(DrawerActivity.this,
                                AddStash.class);
                        navigateTo.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(navigateTo);
                    }
                    if (recyclerView.getChildPosition(child) == 4) {
                        ParseUser.logOut();
                        navigateTo = new Intent(DrawerActivity.this,
                                LoginActivity.class);
                        navigateTo.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(navigateTo);
                        finish();
                    }
                    return true;
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView recyclerView, MotionEvent
                    motionEvent) {

            }
        });

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        Drawer = (DrawerLayout) findViewById(R.id.DrawerLayout);

        mDrawerToggle = new ActionBarDrawerToggle(this, Drawer, toolbar, R
                .string.openDrawer, R.string.closeDrawer) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        Drawer.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
    }

//    @Override
//    protected void onResume()
//    {
//        super.onResume();
//        currentUser = ParseUser.getCurrentUser();
//
//        try {
//            ParseFile parseFile = currentUser.getParseFile("profileThumb");
//            if (parseFile == null) {
//                mProfileImage.setImageResource(R.drawable
// .ic_person_black_18dp);
//            } else {
//                byte[] data = parseFile.getData();
//                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0,
// data.length);
//                mProfileImage.setImageBitmap(bitmap);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}