package utc_4910.screenActivities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.File;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Scanner;

import utc_4910.photoencryptionincloud.AmazonAccountKeys;
import utc_4910.photoencryptionincloud.FileKeyEncryption;
import utc_4910.photoencryptionincloud.R;

/**
 * Created by Matthew Jallouk on 3/1/2015.
 */
public class LoginActivity extends ActionBarActivity {

    private ImageView[] gestureButtons = new ImageView[16];
    private TableLayout gridLayout;
    private ArrayList<String> password = new ArrayList<String>();
    private Button confirmButton;
    private Button redrawButton;
    private boolean passSet = true;
    private RelativeLayout parentLayout;
    private TextView textView4;
    private EditText editText;
    private int usernameDefault = 0;

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Login Account Screen");


        setContentView(R.layout.gesture_activity);
        gridLayout = (TableLayout)findViewById(R.id.gestureGrid);
        parentLayout = (RelativeLayout)findViewById(R.id.relativeLayout);

        confirmButton = (Button)findViewById(R.id.confirmButton);
        redrawButton = (Button)findViewById(R.id.redrawButton);
        textView4 = (TextView)findViewById(R.id.textView4);
        editText = (EditText)findViewById(R.id.editText);

        gestureButtons[0] = (ImageView)findViewById(R.id.imageView1);
        gestureButtons[1] = (ImageView)findViewById(R.id.imageView2);
        gestureButtons[2] = (ImageView)findViewById(R.id.imageView3);
        gestureButtons[3] = (ImageView)findViewById(R.id.imageView4);
        gestureButtons[4] = (ImageView)findViewById(R.id.imageView5);
        gestureButtons[5] = (ImageView)findViewById(R.id.imageView6);
        gestureButtons[6] = (ImageView)findViewById(R.id.imageView7);
        gestureButtons[7] = (ImageView)findViewById(R.id.imageView8);
        gestureButtons[8] = (ImageView)findViewById(R.id.imageView9);
        gestureButtons[9] = (ImageView)findViewById(R.id.imageView10);
        gestureButtons[10] = (ImageView)findViewById(R.id.imageView11);
        gestureButtons[11] = (ImageView)findViewById(R.id.imageView12);
        gestureButtons[12] = (ImageView)findViewById(R.id.imageView13);
        gestureButtons[13] = (ImageView)findViewById(R.id.imageView14);
        gestureButtons[14] = (ImageView)findViewById(R.id.imageView15);
        gestureButtons[15] = (ImageView)findViewById(R.id.imageView16);

        this.runButtonListeners();
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void runButtonListeners() {
        gridLayout.setOnTouchListener(new PasswordButtonListener());

        parentLayout.setOnClickListener(new RelativeLayout.OnClickListener(){
            public void onClick(View v){
                Activity activity = LoginActivity.this;
                InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
            }
        });

        editText.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                if (usernameDefault == 0){
                    editText.setText("");
                    usernameDefault = 1;
                }else{
                    //Do Nothing
                }
            }
        });

        confirmButton.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                try {
                    File folder = new File(Environment.getExternalStorageDirectory() + "/.AWS");
                    String fileName = AmazonAccountKeys.getKeyFileName();
                    File keyFile = new File(folder + fileName);
                    FileKeyEncryption.decrypt(FileKeyEncryption.getSpecialKey(), keyFile, keyFile);

                    Scanner scan = new Scanner(keyFile);
                    String userName = editText.getText().toString().trim().toLowerCase();
                    userName = userName.replace(" ", "");
                    String userNameHash = hashString(userName);
                    String passwordHash = hashString(password.toString());

                    String record = "";
                    String userHash = "";
                    String passHash = "";
                    while (scan.hasNextLine()) {
                        record = scan.nextLine();
                        String[] info = record.split(":::");
                        userHash = info[0];
                        passHash = info[1];
                        if (userNameHash.equals(userHash)){
                            break;
                        }
                    }

                    if (passwordHash.equals(passHash) && userNameHash.equals(userHash)){
                        FileKeyEncryption.encrypt(FileKeyEncryption.getSpecialKey(), keyFile, keyFile);
                        Intent i = new Intent();
                        i.putExtra("UserName", userName);
                        i.setClass(LoginActivity.this, BucketActionActivity.class);
                        //Launch the next activity.
                        finish();
                        startActivity(i);
                    }else{
                        textView4.setText("       Sorry. Your username or\n        password is invalid.");
                        for (int i = 0; i < gestureButtons.length; i++){
                            gestureButtons[i].setImageResource(R.drawable.gesture_not_pressed);
                        }
                        passSet = true;
                        password = new ArrayList<String>();
                    }
                } catch (Exception e) {e.printStackTrace();}
            }
        });

        redrawButton.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                for (int i = 0; i < gestureButtons.length; i++){
                    gestureButtons[i].setImageResource(R.drawable.gesture_not_pressed);
                }
                passSet = true;
                password = new ArrayList<String>();
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public class PasswordButtonListener implements View.OnTouchListener{

        public PasswordButtonListener(){}

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (passSet) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    float x = event.getX();
                    float y = event.getY();
                    for (int i = 0; i < gridLayout.getChildCount(); i++) {
                        TableRow row = (TableRow)gridLayout.getChildAt(i);
                        View rowView = (View)gridLayout.getChildAt(i);
                        Rect rectViewRows = new Rect();
                        rowView.getHitRect(rectViewRows);
                        if (rectViewRows.contains((int) x, (int) y)){
                            y = y - gridLayout.getChildAt(i).getY();
                            for (int j = 0; j < row.getChildCount(); j++) {
                                View view = (View) row.getChildAt(j);
                                Rect rectView = new Rect();
                                view.getHitRect(rectView);
                                if (rectView.contains((int) x, (int) y)){
                                    gestureButtons[i*4+j].setImageResource(R.drawable.gesture_pressed);
                                    password.add((i*4+j) + "");
                                }
                            }
                        }
                    }
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    float x = event.getX();
                    float y = event.getY();
                    for (int i = 0; i < gridLayout.getChildCount(); i++) {
                        TableRow row = (TableRow)gridLayout.getChildAt(i);
                        View rowView = (View)gridLayout.getChildAt(i);
                        Rect rectViewRows = new Rect();
                        rowView.getHitRect(rectViewRows);
                        if (rectViewRows.contains((int) x, (int) y)){
                            y = y - gridLayout.getChildAt(i).getY();
                            for (int j = 0; j < row.getChildCount(); j++) {
                                View view = (View) row.getChildAt(j);
                                Rect rectView = new Rect();
                                view.getHitRect(rectView);
                                if (rectView.contains((int) x, (int) y) && !password.contains((i*4+j) + "")){
                                    gestureButtons[i*4+j].setImageResource(R.drawable.gesture_pressed);
                                    password.add((i*4+j) + "");
                                }
                            }
                        }
                    }
                    int[] posXY = {(int) x, (int) y};
                    gridLayout.getLocationOnScreen(posXY);

                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    passSet = false;
                    Log.d("Done!", password + "");
                    Log.d("Action", "UP");
                    return true;
                } else {
                    return false;
                }
            }else{
                return false;
            }
        }
    }

    public String hashString(String words){
        StringBuffer sb = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.reset();
            digest.update(words.getBytes("UTF-8"));

            sb = new StringBuffer();
            for (byte b : digest.digest()) {
                sb.append(String.format("%02x", b & 0xff));
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
