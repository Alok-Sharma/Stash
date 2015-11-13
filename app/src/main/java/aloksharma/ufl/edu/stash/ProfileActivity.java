package aloksharma.ufl.edu.stash;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

/**
 * Created by khimya on 11/8/15.
 */
public class ProfileActivity extends DrawerActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_profile);
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_profile);
    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        finish();
    }
}
