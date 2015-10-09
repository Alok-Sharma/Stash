package aloksharma.ufl.edu.stash;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.parse.ParseUser;

public class MainActivityt extends AppCompatActivity  implements View.OnClickListener{

    Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        logoutButton = (Button) findViewById(R.id.logoutButton);

        logoutButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.logoutButton:
                ParseUser.logOutInBackground();
                startActivity(new Intent(this,LoginActivity.class));
                break;
        }
    }

}
