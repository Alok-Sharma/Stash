//package aloksharma.ufl.edu.stash;
//
//import android.support.v4.widget.DrawerLayout;
//import android.os.Bundle;
//import android.support.v7.app.ActionBarDrawerToggle;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.support.v7.widget.Toolbar;
//import android.view.GestureDetector;
//import android.view.MotionEvent;
//import android.view.View;
//import android.widget.Toast;
//
//
//public class DrawerActivity extends AppCompatActivity {
//
//    private static int fromPosition = 0;
//    String TITLES[] = {"Profile", "Bank Account", "Add Stash", "Logout"};
//    int ICONS[] = {R.drawable.ic_person_black_24dp, R.drawable.ic_account_balance_black_24dp, R.drawable
//            .ic_add_circle_outline_black_24dp, R.drawable.power};
//
//    String NAME = "Priti Changlani";
//    String EMAIL = "changlani.priti@gmail.com";
//    int PROFILE = R.drawable.labradorlove;
//
//    private Toolbar toolbar;
//
//    RecyclerView mRecyclerView;
//    RecyclerView.Adapter mAdapter;
//    RecyclerView.LayoutManager mLayoutManager;
//    public DrawerLayout Drawer;
//
//    ActionBarDrawerToggle mDrawerToggle;
//
//
//    protected void onCreate(Bundle savedInstanceState, int resLayoutID) {
//        super.onCreate(savedInstanceState);
//        setContentView(resLayoutID);
//
//        toolbar = (Toolbar) findViewById(R.id.tool_bar);
//        setSupportActionBar(toolbar);
//
//        mRecyclerView = (RecyclerView) findViewById(R.id.RecyclerView);
//
//        mRecyclerView.setHasFixedSize(true);
//
//        mAdapter = new CustomAdapter(TITLES, ICONS, NAME, EMAIL, PROFILE);
//
//        mRecyclerView.setAdapter(mAdapter);
//        mLayoutManager = new LinearLayoutManager(this);
//
//        mRecyclerView.setLayoutManager(mLayoutManager);
//
//        final GestureDetector mGestureDetector = new GestureDetector
//                (DrawerActivity.this, new GestureDetector
//                        .SimpleOnGestureListener() {
//
//            @Override
//            public boolean onSingleTapUp(MotionEvent e) {
//                return true;
//            }
//
//        });
//
//        mRecyclerView.addOnItemTouchListener(new RecyclerView
//                .OnItemTouchListener() {
//            @Override
//            public boolean onInterceptTouchEvent(RecyclerView recyclerView,
//                                                 MotionEvent motionEvent) {
//                View child = recyclerView.findChildViewUnder(motionEvent
//                        .getX(), motionEvent.getY());
//
//
//                if (child != null && mGestureDetector.onTouchEvent
//                        (motionEvent)) {
//                    Drawer.closeDrawers();
//                    if (recyclerView.getChildPosition(child) == 0) {
//                        if (fromPosition == 0) {
//                        } else {
//                            fromPosition = 0;
//                            Toast.makeText(DrawerActivity.this, "The Item " +
//                                    "Clicked is: " + recyclerView
//                                    .getChildPosition(child), Toast
//                                    .LENGTH_SHORT).show();
//                        }
//                    }
//                    if (recyclerView.getChildPosition(child) == 1) {
//                        if (fromPosition == 1) {
//                        } else {
//                            fromPosition = 1;
//                            Toast.makeText(DrawerActivity.this, "The Item " +
//                                    "Clicked is: " + recyclerView
//                                    .getChildPosition(child), Toast
//                                    .LENGTH_SHORT).show();
//                        }
//                    }
//
//                    return true;
//
//                }
//
//                return false;
//            }
//
//            @Override
//            public void onTouchEvent(RecyclerView recyclerView, MotionEvent
//                    motionEvent) {
//
//            }
//
//        });
//
//        mLayoutManager = new LinearLayoutManager(this);
//
//        mRecyclerView.setLayoutManager(mLayoutManager);
//
//
//        Drawer = (DrawerLayout) findViewById(R.id.DrawerLayout);
//
//        mDrawerToggle = new ActionBarDrawerToggle(this, Drawer, toolbar, R
//                .string.openDrawer, R.string.closeDrawer) {
//
//
//            @Override
//            public void onDrawerOpened(View drawerView) {
//                super.onDrawerOpened(drawerView);
//            }
//
//            @Override
//            public void onDrawerClosed(View drawerView) {
//                super.onDrawerClosed(drawerView);
//            }
//        };
//        Drawer.setDrawerListener(mDrawerToggle);
//        mDrawerToggle.syncState();
//
//
//    }
//}