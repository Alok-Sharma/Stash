package aloksharma.ufl.edu.stash;

import android.app.Application;
import android.content.Context;

import com.facebook.FacebookSdk;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;

/**
 * Created by Alok on 9/23/2015.
 */
public class App extends Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();

        //Parse
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, getString(R.string.parse_applicationId),
                getString(R.string.parse_clientKey));

        //Facebook
        FacebookSdk.sdkInitialize(this);
        ParseFacebookUtils.initialize(this);
    }

    public static Context getContext() {
        return mContext;
    }
}
