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

    //Define private variables.
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

    /** Method that sets the initial properties of the screen.
     *
     * @param savedInstanceState        state of the previous instance being saved.
     */
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

        //Listen for any button clicks or events.
        this.runButtonListeners();
    }

    /** Method that performs actions based upon which buttons were clicked.
     *
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void runButtonListeners() {
        gridLayout.setOnTouchListener(new PasswordButtonListener());

        /** Call that will hide the keyboard from the users view. This occurs when the user clicks any
         *  other object other than the editText.
         *
         */
        parentLayout.setOnClickListener(new RelativeLayout.OnClickListener(){
            public void onClick(View v){
                Activity activity = LoginActivity.this;
                InputMethodManager inputMethodManager = (InputMethodManager)
                        activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
            }
        });

        /** Call that occurs when the editText is selected. It clears the default username.
         *
         */
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

        /** Call that occurs when the user wishes to confirm their drawn password. This block
         *  of code takes into account checking for valid username/password pairs.
         *
         */
        confirmButton.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                try {
                    //Obtain file if it exists and read it to determine if the username exists already.
                    File folder = new File(Environment.getExternalStorageDirectory() + "/.AWS");
                    String fileName = AmazonAccountKeys.getKeyFileName();
                    File keyFile = new File(folder + fileName);

                    //Decrypt file and check to see if the username exists.
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

                    //If the user exists, check if their drawn password matches the stored one.
                    //Otherwise, throw an error.
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

        /** This event occurs when we tap the redraw button on the screen. It just clears all of the resets
         *  the screen back to original state prior to any username/password pairs being entered.
         *
         */
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

    /** Inner class that handles the gestures taken in by the user drawing their password.
     *
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public class PasswordButtonListener implements View.OnTouchListener{

        /** Constructor that defines initial properties. Why is this even here again?
         *
         */
        public PasswordButtonListener(){}

        /** Method is executed when a user's finger touches the screen. The specific
         *  logic executed is based upon what sort of gesture occurred.
         *
         * @param v                     object of which is this run on (main TableLayout
         *                              in this case).
         * @param event                 event to be analyzed.
         * @return                      returns a true/false to determine if this
         *                              event was handled (always will return true).
         */
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (passSet) {
                //Highlight the tapped button and add it to our password gesture.
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

                    //As we move our finger across, we pick up more buttons and add them to our queue of
                    //tapped buttons.
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

                    //Finally, once we lift our finger, add the last button that we're on to our gestured
                    //list of buttons. We lock this feature so you can't add any more buttons withour reseting
                    //the state of the machine.
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

    /** Method that hashes whatever string it takes in. The hashing algorithm is SHA-1.
     *
     * @param words                 string to be hashed, typically is a username/password.
     * @return                      returns an SHA-1, hash.
     */
    public String hashString(String words){
        StringBuffer sb = null;
        try {
            //Define the algorithm to use.
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.reset();
            //Translate bytes into unicode
            digest.update(words.getBytes("UTF-8"));

            //Chain them together and shift them to get a hash.
            sb = new StringBuffer();
            for (byte b : digest.digest()) {
                sb.append(String.format("%02x", b & 0xff));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        //Return a string version of the hash.
        return sb.toString();
    }
}
