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

import com.amazonaws.services.s3.model.SSECustomerKey;

import java.io.File;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Scanner;

import javax.crypto.KeyGenerator;

import utc_4910.photoencryptionincloud.R;

/**
 * Created by Matthew Jallouk on 3/16/2015.
 */
public class CreateAccountActivity extends Activity {

    private ImageView[] gestureButtons = new ImageView[16];
    private TableLayout gridLayout;
    private ArrayList<String> password = new ArrayList<String>();
    private ArrayList<String> confirmPass= new ArrayList<String>();
    private Button confirmButton;
    private Button redrawButton;
    private boolean passSet = true;
    private RelativeLayout parentLayout;
    private EditText userName;
    private TextView textView4;
    private int confirmSwitcher = 1;
    private int usernameCollision = 0;
    private int usernameDefault = 0;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gesture_activity);
        gridLayout = (TableLayout)findViewById(R.id.gestureGrid);
        parentLayout = (RelativeLayout)findViewById(R.id.relativeLayout);

        textView4 = (TextView)findViewById(R.id.textView4);
        userName = (EditText)findViewById(R.id.editText);
        confirmButton = (Button)findViewById(R.id.confirmButton);
        redrawButton = (Button)findViewById(R.id.redrawButton);

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
                Activity activity = CreateAccountActivity.this;
                InputMethodManager inputMethodManager =
                        (InputMethodManager)activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
            }
        });

        userName.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                if (usernameDefault == 0){
                    userName.setText("");
                    usernameDefault = 1;
                }else{
                    //Do Nothing
                }
            }
        });

        confirmButton.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v) {
                usernameCollision = 0;
                File folder = new File(Environment.getExternalStorageDirectory() + "/.AWS");
                folder.mkdirs();
                String fileName = "/.keys";
                String usernameHash = userName.getText().toString().trim().toLowerCase();
                usernameHash = usernameHash.replace(" ", "");
                try {
                    File file = new File(folder + fileName);
                    Scanner scan = null;
                    if (file.exists()) {
                        scan = new Scanner(file);
                    }
                    String record = "";
                    if (file.exists()) {
                        while (scan.hasNextLine()) {
                            record = scan.nextLine();
                            String[] tokens = record.split(":::");
                            String tokenUserName = tokens[0];
                            String usernameHashString = hashString(usernameHash);
                            if (tokenUserName.equals(usernameHashString)) {
                                usernameCollision = 1;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                String badSymbols = "~`!@#$%^&*()_+=,<>?/;:'[{}]";
                boolean badSymbolChecker = ((usernameHash.contains(badSymbols.substring(0, 1))) ||
                        (usernameHash.contains(badSymbols.substring(1, 2))) ||
                        (usernameHash.contains(badSymbols.substring(2, 3))) ||
                        (usernameHash.contains(badSymbols.substring(3, 4))) ||
                        (usernameHash.contains(badSymbols.substring(4, 5))) ||
                        (usernameHash.contains(badSymbols.substring(5, 6))) ||
                        (usernameHash.contains(badSymbols.substring(6, 7))) ||
                        (usernameHash.contains(badSymbols.substring(7, 8))) ||
                        (usernameHash.contains(badSymbols.substring(8, 9))) ||
                        (usernameHash.contains(badSymbols.substring(9, 10))) ||
                        (usernameHash.contains(badSymbols.substring(10, 11))) ||
                        (usernameHash.contains(badSymbols.substring(11, 12))) ||
                        (usernameHash.contains(badSymbols.substring(12, 13))) ||
                        (usernameHash.contains(badSymbols.substring(13, 14))) ||
                        (usernameHash.contains(badSymbols.substring(14, 15))) ||
                        (usernameHash.contains(badSymbols.substring(15, 16))) ||
                        (usernameHash.contains(badSymbols.substring(16, 17))) ||
                        (usernameHash.contains(badSymbols.substring(17, 18))) ||
                        (usernameHash.contains(badSymbols.substring(18, 19))) ||
                        (usernameHash.contains(badSymbols.substring(19, 20))) ||
                        (usernameHash.contains(badSymbols.substring(20, 21))) ||
                        (usernameHash.contains(badSymbols.substring(21, 22))) ||
                        (usernameHash.contains(badSymbols.substring(22, 23))) ||
                        (usernameHash.contains(badSymbols.substring(23, 24))) ||
                        (usernameHash.contains(badSymbols.substring(25, 26))) ||
                        (usernameHash.contains(badSymbols.substring(26, 27))) ||
                        (usernameHash.contains("\"")));
                if (!badSymbolChecker) {
                    if (usernameCollision == 0) {
                        textView4.setText("Redraw your pattern to confirm.");
                        if (confirmSwitcher == 1 && !password.isEmpty()) {
                            confirmSwitcher = 2;
                            for (int i = 0; i < gestureButtons.length; i++) {
                                gestureButtons[i].setImageResource(R.drawable.gesture_not_pressed);
                            }
                            passSet = true;
                            confirmPass = (ArrayList<String>) (password.clone());
                            password = new ArrayList<String>();

                        } else if (confirmSwitcher == 2 && password.equals(confirmPass) && !password.isEmpty()) {
                            File file = new File(folder + fileName);
                            try {

                                KeyGenerator generator = KeyGenerator.getInstance("AES");
                                generator.init(256, new SecureRandom());
                                SSECustomerKey sseKey1 = new SSECustomerKey(generator.generateKey());
                                SSECustomerKey sseKey2 = new SSECustomerKey(generator.generateKey());
                                SSECustomerKey sseKey3 = new SSECustomerKey(generator.generateKey());
                                SSECustomerKey sseKey4 = new SSECustomerKey(generator.generateKey());
                                SSECustomerKey sseKey5 = new SSECustomerKey(generator.generateKey());
                                SSECustomerKey sseKey6 = new SSECustomerKey(generator.generateKey());
                                SSECustomerKey sseKey7 = new SSECustomerKey(generator.generateKey());
                                SSECustomerKey sseKey8 = new SSECustomerKey(generator.generateKey());

                                String key1 = sseKey1.getKey();
                                String key2 = sseKey2.getKey();
                                String key3 = sseKey3.getKey();
                                String key4 = sseKey4.getKey();
                                String key5 = sseKey5.getKey();
                                String key6 = sseKey6.getKey();
                                String key7 = sseKey7.getKey();
                                String key8 = sseKey8.getKey();

                                Scanner scan = null;
                                if (file.exists()) {
                                    scan = new Scanner(file);
                                }
                                //Parse file to get existing keys and accounts.
                                String record = "";
                                String totalFile = "";
                                if (file.exists()) {
                                    while (scan.hasNextLine()) {
                                        record = scan.nextLine();
                                        totalFile += record + "\n";
                                    }
                                }
                                PrintWriter printWriter = new PrintWriter(folder + fileName);

                                printWriter.print(totalFile);

                                String usernameHashString = hashString(usernameHash);
                                String passwordHash = hashString(password.toString());
                                printWriter.print(usernameHashString + ":::" + passwordHash + ":::" + key1 + ":::" + key2 +
                                        ":::" + key3 + ":::" + key4 + ":::" + key5 + ":::" + key6 + ":::" + key7 +
                                        ":::" + key8);
                                printWriter.close();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            Intent i = new Intent();
                            i.setClass(CreateAccountActivity.this, MainActivity.class);
                            //Launch the next activity.
                            finish();
                            startActivity(i);
                            Toast.makeText(getApplicationContext(), "Account has been created. You can now login!",
                                    Toast.LENGTH_LONG).show();
                        } else if (password.isEmpty()) {
                            textView4.setText("You can't have an empty pattern\n           password! Try again!");
                            passSet = true;
                        } else {
                            //Passwords don't match.
                            textView4.setText("         Your confirmed password\n     doesn't match the original one." +
                                    "\n                    Try again!");
                            confirmSwitcher = 1;
                            for (int i = 0; i < gestureButtons.length; i++) {
                                gestureButtons[i].setImageResource(R.drawable.gesture_not_pressed);
                            }
                            passSet = true;
                            confirmPass = new ArrayList<String>();
                            password = new ArrayList<String>();
                        }
                    } else {
                        textView4.setText("             Username already exists.\n       Please choose another username" +
                                "\n              and draw your pattern!");
                        for (int i = 0; i < gestureButtons.length; i++) {
                            gestureButtons[i].setImageResource(R.drawable.gesture_not_pressed);
                        }
                        passSet = true;
                        password = new ArrayList<String>();

                    }
                }else{
                    textView4.setText("   Usernames can only contain\n  letters and numbers. Try again." + usernameHash);

                }
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