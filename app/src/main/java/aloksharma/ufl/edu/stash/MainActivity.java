package aloksharma.ufl.edu.stash;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.parse.ParseObject;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Intent serverIntent = new Intent(this, ServerAccess.class);
//        serverIntent.putExtra("server_action", ServerAccess.ServerAction
//                .GET_BALANCE.toString());
//        this.startService(serverIntent);

//        ParseObject testObject = new ParseObject("TestObject");
//        testObject.put("foo", "bar");
//        testObject.saveInBackground();
    }
}
