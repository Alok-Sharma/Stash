package aloksharma.ufl.edu.stash;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by Tarun on 10/7/2015.
 */
public class LoginApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();

        //Parse
        //Parse.enableLocalDatastore(this);
        Parse.initialize(this, getString(R.string.parse_applicationId), getString(R.string.parse_clientKey));
    }
}
