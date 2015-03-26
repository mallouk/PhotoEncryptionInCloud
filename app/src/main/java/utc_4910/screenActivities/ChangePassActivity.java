package utc_4910.screenActivities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.Toast;

import java.io.File;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Scanner;

import utc_4910.photoencryptionincloud.R;

/**
 * Created by Matthew Jallouk on 3/1/2015.
 */
public class ChangePassActivity extends Activity {

    private ImageView[] gestureButtons = new ImageView[16];
    private TableLayout tableLayout;
    private ArrayList<String> password = new ArrayList<String>();
    private Button confirmButton;
    private Button redrawButton;
    private boolean passSet = true;
    private RelativeLayout parentLayout;
    private TextView textView4;
    private EditText editText;
    private int usernameDefault = 0;
    private int newPassword = 0;
    private int confirmSwitcher = 0;
    private ArrayList<String> confirmPass;


    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gesture_activity);
        tableLayout = (TableLayout)findViewById(R.id.gestureGrid);
        parentLayout = (RelativeLayout)findViewById(R.id.relativeLayout);

        confirmButton = (Button)findViewById(R.id.confirmButton);
        redrawButton = (Button)findViewById(R.id.redrawButton);
        textView4 = (TextView)findViewById(R.id.textView4);
        editText = (EditText)findViewById(R.id.editText);
        textView4.setText("     Draw your current \nauthentication pattern!");

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
        tableLayout.setOnTouchListener(new PasswordButtonListener());

        parentLayout.setOnClickListener(new RelativeLayout.OnClickListener(){
            public void onClick(View v){
                Activity activity = ChangePassActivity.this;
                InputMethodManager inputMethodManager =
                        (InputMethodManager)activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
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
                confirmButton.setText("Moo");
                try {
                    File folder = new File(Environment.getExternalStorageDirectory() + "/.AWS");
                    String fileName = "/.keys";
                    File keyFile = new File(folder + fileName);
                    Scanner scan = new Scanner(keyFile);
                    String userName = editText.getText().toString().trim().toLowerCase();
                    userName = userName.replace(" ", "");
                    String userNameHash = hashString(userName);
                    String passwordHash = hashString(password.toString());
                    String totalFile = "";
                    String changingUserKeys = "";

                    if (newPassword == 0) { //Entering current password.
                        String record = "";

                        String userHash = "";
                        String passHash = "";
                        int foundMatch = 0;
                        while (scan.hasNextLine()) {
                            record = scan.nextLine();
                            String[] info = record.split(":::");

                            if (foundMatch == 0) {
                                userHash = info[0];
                                passHash = info[1];
                                changingUserKeys+=info[2] + ":::" + info[3] + ":::" + info[4] + ":::" + info[5] + ":::" +
                                        info[6] + ":::" + info[7] + ":::" + info[8] + ":::" + info[9];
                            }

                            if (userNameHash.equals(userHash)) {
                                //Do Nothing
                                foundMatch = 1;
                            } else {
                                totalFile += record + "\n";
                            }
                        }

                        if (passwordHash.equals(passHash) && userNameHash.equals(userHash)) {
                            textView4.setText("         Draw your new\nauthentication pattern!");
                            for (int i = 0; i < gestureButtons.length; i++) {
                                gestureButtons[i].setImageResource(R.drawable.gesture_not_pressed);
                            }
                            passSet = true;
                            password = new ArrayList<String>();
                            newPassword = 1;
                        } else {
                            textView4.setText("       Sorry. Your username or\n        password is invalid.");
                            for (int i = 0; i < gestureButtons.length; i++) {
                                gestureButtons[i].setImageResource(R.drawable.gesture_not_pressed);
                            }
                            passSet = true;
                            password = new ArrayList<String>();
                        }
                    }else if (newPassword == 1){ //Enter new password
                        if (confirmSwitcher == 0 && !password.isEmpty()){
                            textView4.setText("Redraw your new pattern to confirm.");
                            for (int i = 0; i < gestureButtons.length; i++) {
                                gestureButtons[i].setImageResource(R.drawable.gesture_not_pressed);
                            }
                            passSet = true;
                            confirmPass = (ArrayList<String>) (password.clone());
                            password = new ArrayList<String>();
                            confirmSwitcher = 1;
                        }else if (confirmSwitcher == 1 && password.equals(confirmPass) && !password.isEmpty()) {
                            PrintWriter printWriter = new PrintWriter(folder + fileName);

                            passwordHash = hashString(password.toString());
                            printWriter.print(totalFile);

                            printWriter.print(userNameHash + ":::" + passwordHash + ":::" + changingUserKeys);
                            printWriter.close();

                            Intent i = new Intent();
                            i.setClass(ChangePassActivity.this, MainActivity.class);
                            //Launch the next activity.
                            finish();
                            startActivity(i);
                            Toast.makeText(getApplicationContext(), "Account password has been changed!",
                                    Toast.LENGTH_LONG).show();
                        }else if (password.isEmpty()) {
                            textView4.setText("You can't have an empty pattern\n           password! Try again!");
                            passSet = true;
                        } else {
                            //Passwords don't match.
                            textView4.setText("         Your confirmed password\n     doesn't match the original one." +
                                    "\n                    Try again!");
                            confirmSwitcher = 0;
                            for (int i = 0; i < gestureButtons.length; i++) {
                                gestureButtons[i].setImageResource(R.drawable.gesture_not_pressed);
                            }
                            passSet = true;
                            confirmPass = new ArrayList<String>();
                            password = new ArrayList<String>();
                        }
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
                    for (int i = 0; i < tableLayout.getChildCount(); i++) {
                        TableRow row = (TableRow)tableLayout.getChildAt(i);
                        View rowView = (View)tableLayout.getChildAt(i);
                        Rect rectViewRows = new Rect();
                        rowView.getHitRect(rectViewRows);
                        if (rectViewRows.contains((int) x, (int) y)){
                            y = y - tableLayout.getChildAt(i).getY();
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
                    for (int i = 0; i < tableLayout.getChildCount(); i++) {
                        TableRow row = (TableRow)tableLayout.getChildAt(i);
                        View rowView = (View)tableLayout.getChildAt(i);
                        Rect rectViewRows = new Rect();
                        rowView.getHitRect(rectViewRows);
                        if (rectViewRows.contains((int) x, (int) y)){
                            y = y - tableLayout.getChildAt(i).getY();
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
                    tableLayout.getLocationOnScreen(posXY);

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
