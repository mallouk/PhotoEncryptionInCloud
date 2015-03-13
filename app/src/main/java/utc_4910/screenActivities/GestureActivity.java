package utc_4910.screenActivities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;

import utc_4910.photoencryptionincloud.R;

/**
 * Created by Matthew Jallouk on 3/1/2015.
 */
public class GestureActivity extends Activity {

    private ImageView[] gestureButtons = new ImageView[16];
    private GridLayout gridLayout;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gesture_activity);
        //added comment
        //test
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

        this.runButtonListeners();
        //test

    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void runButtonListeners() {
        for (int x = 0; x < gridLayout.getChildCount(); x++) {
            gridLayout.getChildAt(x).setOnClickListener(new PasswordButtonListener(this,x));
        }
    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public class PasswordButtonListener extends View implements  Button.OnClickListener, Button.OnLongClickListener,
            View.OnDragListener, View.OnTouchListener, GestureDetector.OnGestureListener{

        private GestureDetectorCompat gestureDetectorCompat;
        private int index;

        public PasswordButtonListener(Context context, int index) {
            super(context);
            this.index = index;
            gestureDetectorCompat = new GestureDetectorCompat(GestureActivity.this, this);
        }

        @Override
        public void onClick(View v) {
            Log.d("TAG-CLICK", gridLayout.getChildAt(index) + " " + index);
            gestureButtons[index].setImageResource(R.drawable.gesture_pressed);
        }

        @Override
        public boolean onDrag(View v, DragEvent event) {
            if (event.getAction() == DragEvent.ACTION_DRAG_LOCATION){
                Log.d("TAG-DRAG", gridLayout.getChildAt(index) + " " + index);
                gestureButtons[index].setImageResource(R.drawable.gesture_pressed);
            }
            return true;
        }

        @Override
        public boolean onLongClick(View v) {
            Log.d("TAG-LONG", gridLayout.getChildAt(index) + " " + index);
            gestureButtons[index].setImageResource(R.drawable.gesture_pressed);
            return true;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                v.startDrag(data, shadowBuilder, v, 0);
                v.setVisibility(View.INVISIBLE);
                return true;
            } else {
                return false;
            }
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            Log.d("Tap", "Single Tap!");
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            Log.d("SCROLL", "Scroll!");
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
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
