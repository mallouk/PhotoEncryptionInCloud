package utc_4910.screenActivities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import utc_4910.photoencryptionincloud.R;

/**
 * Created by Matthew Jallouk on 3/1/2015.
 */
public class GestureActivity extends Activity {

    private ImageView[] gestureButtons = new ImageView[16];
    private GridLayout gridLayout;
    private ArrayList<String> password = new ArrayList<String>();
    private Button confirmButton;
    private Button redrawButton;
    private boolean passSet = true;
    private RelativeLayout parentLayout;



    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gesture_activity);
        gridLayout = (GridLayout)findViewById(R.id.gestureGrid);
        parentLayout = (RelativeLayout)findViewById(R.id.relativeLayout);

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
                Activity activity = GestureActivity.this;
                InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
            }
        });

        confirmButton.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){

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


        public PasswordButtonListener(){

        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (passSet) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    float x = event.getX();
                    float y = event.getY();
                    for (int i = 0; i < gridLayout.getChildCount(); i++) {
                        View view = gridLayout.getChildAt(i);
                        Rect rectView = new Rect();
                        view.getHitRect(rectView);
                        if (rectView.contains((int) x, (int) y) && gridLayout.getChildAt(i).isShown()) {
                            Log.d("Index: ", i + "");
                            gestureButtons[i].setImageResource(R.drawable.gesture_pressed);
                            password.add(i + "");
                        }
                    }
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    float x = event.getX();
                    float y = event.getY();
                    for (int i = 0; i < gridLayout.getChildCount(); i++) {
                        View view = gridLayout.getChildAt(i);
                        Rect rectView = new Rect();
                        view.getHitRect(rectView);
                        if (rectView.contains((int) x, (int) y) && gridLayout.getChildAt(i).isShown()
                                && !password.contains(i + "")) {
                            Log.d("Index: ", i + "");
                            gestureButtons[i].setImageResource(R.drawable.gesture_pressed);
                            password.add(i + "");
                        }
                    }
                    int[] posXY = {(int) x, (int) y};
                    gridLayout.getLocationOnScreen(posXY);
                    //Log.d("DRAW", "MOVING " + index + "   " + x + " " + y);

                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    passSet = false;
                    Log.d("Done!", password + "");
                    Log.d("Action", "UP");
                    //onDraw(this);
                    return true;
                } else {
                    return false;
                }
            }else{
                return false;
            }
        }
    }
}
