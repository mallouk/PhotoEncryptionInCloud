package utc_4910.screenActivities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;

import java.util.ArrayList;

import utc_4910.photoencryptionincloud.R;

/**
 * Created by Matthew Jallouk on 3/1/2015.
 */
public class GestureActivity extends Activity {

    private ImageView[] gestureButtons = new ImageView[16];
    private GridLayout gridLayout;
    private PasswordButtonListener passwordButtonListener;
    private ArrayList<String> password = new ArrayList<String>();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.gesture_activity);
        passwordButtonListener = new PasswordButtonListener(this);
        //setContentView(passwordButtonListener);

        gridLayout = (GridLayout)findViewById(R.id.gestureGrid);
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
        //comment
        this.runButtonListeners();
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void runButtonListeners() {
        //for (int x = 0; x < gridLayout.getChildCount(); x++) {
            //passwordButtonListener.setIndex(x);
            //gridLayout.getChildAt(x).setOnTouchListener(new PasswordButtonListener(this, x));
        gridLayout.setOnTouchListener(passwordButtonListener);
        //}
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public class PasswordButtonListener extends View implements View.OnTouchListener{

        private int index;
        private boolean passSet = true;

        public PasswordButtonListener(Context context){
            super(context);
        }

        public PasswordButtonListener(Context context, int index) {
            super(context);
            this.index = index;
        }

        public void setIndex(int index){
            this.index = index;
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
                    event.getDeviceId();
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
                    return true;
                } else {
                    return false;
                }
            }else{
                return false;
            }
        }

        public void onDraw(Canvas canvas){
            super.onDraw(canvas);
            Rect r = new Rect(0, 0, canvas.getWidth(), canvas.getHeight()/2);
            Paint blue = new Paint(Color.BLUE);
            blue.setStyle(Paint.Style.FILL);

            canvas.drawRect(r, blue);
        }
    }







}
