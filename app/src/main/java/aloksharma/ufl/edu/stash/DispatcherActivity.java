package aloksharma.ufl.edu.stash;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.parse.ParseUser;

public class DispatcherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if(ParseUser.getCurrentUser()!=null)
        {
            startActivity(new Intent(this,MainActivity.class));
        }
        else
        {
            startActivity(new Intent(this,LoginActivity.class));
        }
    }
}
