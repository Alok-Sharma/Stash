package aloksharma.ufl.edu.stash;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class RegisterActivity extends ActionBarActivity implements View
        .OnClickListener {

    Button registerButton;
    public EditText username, password, retypepassword;
    static final int REQUEST_IMAGE_GET = 1;
    ParseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        retypepassword = (EditText) findViewById(R.id.retypepassword);

        registerButton = (Button) findViewById(R.id.registerButton);
        registerButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.registerButton:
                boolean validationError = false;
                StringBuilder errorMessage = new StringBuilder("Please ");

                if (username.getText().toString().equals("") || password
                        .getText().toString().equals("") || retypepassword
                        .getText().toString().equals("")) {
                    validationError = true;
                    errorMessage = errorMessage.append("fill all the text " +
                            "fields.");
                }

                if (!password.getText().toString().equals(retypepassword
                        .getText().toString())) {
                    validationError = true;
                    errorMessage = errorMessage.append("re-enter the " +
                            "password, the passwords do not match.");
                }

                if (validationError) {
                    Toast.makeText(this, errorMessage.toString(), Toast
                            .LENGTH_LONG).show();
                    return;
                }

                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("Please wait.");
                progressDialog.setMessage("Registering you into the " +
                        "system...");
                progressDialog.show();

                /*ServerAccess serverAccess = new ServerAccess();
                serverAccess.addUser(username.getText().toString(), password
                .getText().toString());*/

                user = new ParseUser();
                user.setUsername(username.getText().toString());
                user.setPassword(password.getText().toString());

                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        progressDialog.dismiss();

                        if (e != null) {
                            Toast.makeText(RegisterActivity.this, e
                                    .getMessage(), Toast.LENGTH_LONG).show();
                        } else {

                            AlertDialog.Builder alertDialog = new
                                    AlertDialog.Builder(RegisterActivity.this);

                            alertDialog.setTitle("Would you like to upload a" +
                                    " profile picture?");

                            alertDialog.setPositiveButton("YES", new
                                    DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface
                                                                    dialog,
                                                            int which) {

                                            // Write your code here to
                                            // invoke YES event
                                            selectImage();
                                        }
                                    });

                            // Setting Negative "NO" Button
                            alertDialog.setNegativeButton("NO", new
                                    DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface
                                                                    dialog,
                                                            int which) {

                                            dialog.cancel();
                                            Intent intent = new Intent
                                                    (RegisterActivity
                                                            .this,
                                                            LoginActivity
                                                                    .class);
                                            intent.addFlags(Intent
                                                    .FLAG_ACTIVITY_CLEAR_TASK
                                                    | Intent
                                                    .FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);


                                        }
                                    });

                            // Showing Alert Message
                            alertDialog.show();


                        }
                    }
                });

                break;

        }

    }

    public void selectImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_GET);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent
            data) {
        if (requestCode == REQUEST_IMAGE_GET && resultCode == RESULT_OK) {
            InputStream istream = null;
            try {
                istream = getContentResolver().openInputStream(data.getData());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Bitmap bitmap = BitmapFactory.decodeStream(istream);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
            byte[] bdata = stream.toByteArray();
            String thumbName = user.getUsername().replaceAll("\\s+", "");
            final ParseFile parseFile = new ParseFile(thumbName + "_thumb.jpg",
                    bdata);

            parseFile.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    user.put("profileThumb", parseFile);

                    //Finally save all the user details
                    user.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            Intent intent = new Intent
                                    (RegisterActivity
                                            .this,
                                            LoginActivity
                                                    .class);
                            intent.addFlags(Intent
                                    .FLAG_ACTIVITY_CLEAR_TASK
                                    | Intent
                                    .FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    });

                }

            });
        }
    }
}
