package utc_4910.screenActivities;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.amazonaws.services.s3.model.Bucket;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import utc_4910.photoencryptionincloud.BucketManager;
import utc_4910.photoencryptionincloud.R;

public class BucketActionActivity extends ActionBarActivity {

    //Instance variables defined here
    final private BucketManager bucketManager = new BucketManager("non-capstonebem");
    private Button createBucketButton = null;
    private Button deleteBucketButton = null;
    private Button uploadPhotoButton = null;
    private Button listObjectsButton = null;
    private Spinner spinner = null;
    private Handler mHandler = null;
    private ListBucketRunnable listBucketRunnable = null;
    private String userName;

    /** Method that is run at the start of this activity being called
     *
     * @param savedInstanceState        state of the previous instance being saved.
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bucket_action_activity);
        userName = (String)getIntent().getSerializableExtra("UserName");


        setTitle("Logged in as: " + userName);

        //Instantiation of the various objects on the screen and other instance variables defined
        createBucketButton = (Button)findViewById(R.id.createBucket);
        deleteBucketButton = (Button)findViewById(R.id.deleteBucketButton);
        uploadPhotoButton = (Button)findViewById(R.id.uploadPhotoButton);
        listObjectsButton = (Button)findViewById(R.id.downloadPhotoButton);
        spinner = (Spinner) findViewById(R.id.spinner);
        mHandler = new Handler();
        listBucketRunnable = new ListBucketRunnable();

        //Update the bucket list of currently existing buckets and illustrate those changes by
        //listing those buckets in the spinner list.
        this.updateBucketList(listBucketRunnable, mHandler, spinner);

        //Listen for events such as button clicks.
        this.runButtonListeners();
    }

    /** Method that holds all of the event detections. When a button is clicked this method
     * holds and runs a series of commands based upon which button was clicked.
     *
     */
    public void runButtonListeners(){


        /** This AdapterView listener runs when the spinner on our screen is selected.
         * This method just resets the active bucket to whatever bucket the spinner selected.
         *
         */
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedItem = spinner.getSelectedItem().toString();
                bucketManager.setBucketName(selectedItem);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });


        /** This Button listener runs when the download button is selected. This method will call
         *  another activity to the screen to display the contents of the current active bucket.
         *  The current active bucket is dictated by the selected item in the spinner list.
         *  This second activity will allow the user to download/delete files from said bucket.
         *
         */
        listObjectsButton.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v) {
                if (spinner.getCount() != 0) {
                    Intent i = new Intent();
                    //Pass the active bucket name to the next activity.
                    i.putExtra("BucketName", bucketManager.getBucketName());
                    i.setClass(BucketActionActivity.this, SpillBucketActivity.class);
                    //Launch the next activity.
                    startActivity(i);
                }else{
                    Toast.makeText(getApplicationContext(), "You don't have any buckets to look at. Please create one.",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        /** This Button listener runs when the upload button is selected. This method will
         *  bring up the gallery of the device it is running on allowing the user to select
         *  a photo. Once they do, the photo will be uploaded to the appropriate active bucket
         *  dictated by the selected itm in the spinner list.
         *
         */
        uploadPhotoButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                //Encapsulate the gallery call in a runnable thread. We do this because many calls
                //to the devices systems cannot run on the main user thread and thusly have to be
                //called via secondary threads.

                Runnable r = new Runnable() {
                    public void run() {
                        Intent imagePicker = new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        imagePicker.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                        startActivityForResult(imagePicker, 1);
                    }
                };

                if (spinner.getCount() != 0) {
                    Thread t1 = new Thread(r);
                    t1.start();
                }else{
                    Toast.makeText(getApplicationContext(), "You don't have any buckets to upload a file to. Please create one.",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        /** This Button listener runs when the Create bucket button is clicked. This button
         *  opens a new view to allow the user to type in the name of the bucket they wish to give
         *  it, as well as the level of encryption they wish to enact on the bucket and all
         *  subsequent files inside that bucket.
         *
         */
        createBucketButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Use threading to bring up the CreateBucket View.
                Runnable r = new Runnable() {
                    public void run() {
                        Intent i = new Intent();
                        i.putExtra("UserName", userName);
                        i.setClass(BucketActionActivity.this, CreateBucketActivity.class);
                        finish();
                        startActivity(i);
                    }
                };
                ExecutorService executeT1 = Executors.newFixedThreadPool(1);
                executeT1.execute(r);
                executeT1.shutdown();
                while (!executeT1.isTerminated()){};


                //Updated bucket list in spinner.
                updateBucketList(listBucketRunnable, mHandler, spinner);
            }
        });

        /** This Button listener runs when the Destroy Bucket button is clicked. This button takes the
         *  spinner active bucket and destroys it. However, this is only if the bucket is empty.
         *  An error is thrown if the bucket is not empty. The user must empty the contents of a bucket
         *  if the deletion of the bucket is to go through.
         *
         */
        deleteBucketButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spinner.getCount() != 0) {
                    final String bucketName = spinner.getSelectedItem().toString();
                    final BucketManager newBucket = new BucketManager(bucketName);

                    //Threads are dispatched to check if the bucket actually exists and if the bucket
                    //is empty or not.
                    final BucketExistsRunnable bucketExistsRunnable = new BucketExistsRunnable(newBucket);
                    final IsBucketEmptyRunnable isBucketEmptyRunnable = new IsBucketEmptyRunnable(newBucket);
                    ExecutorService execute = Executors.newFixedThreadPool(1);
                    ExecutorService executeT2 = Executors.newFixedThreadPool(5);

                    execute.execute(bucketExistsRunnable);
                    execute.shutdown();
                    while (!execute.isTerminated()) {};


                    executeT2.execute(isBucketEmptyRunnable);
                    executeT2.shutdown();
                    while (!executeT2.isTerminated()) {};


                    //If the bucket exists and is empty, we destroy it. Otherwise, do nothing.
                    Runnable r = new Runnable() {
                        public void run() {
                            if (bucketExistsRunnable.doesBucketExist() && isBucketEmptyRunnable.isBucketEmpty()) {
                                newBucket.destroyBucket();
                            }
                        }
                    };

                    //Return the appropriate response to the user.
                    if (bucketExistsRunnable.doesBucketExist() && isBucketEmptyRunnable.isBucketEmpty()) {
                        Toast.makeText(getApplicationContext(), "'" + bucketName + "' bucket destroyed!",
                                Toast.LENGTH_LONG).show();
                    } else if (!isBucketEmptyRunnable.isBucketEmpty()) {
                        Toast.makeText(getApplicationContext(), "You can't delete a bucket that has stuff in it.",
                                Toast.LENGTH_LONG).show();
                    }


                    ExecutorService executeT3 = Executors.newFixedThreadPool(5);
                    executeT3.execute(r);
                    executeT3.shutdownNow();
                    while (!executeT3.isTerminated()) {};


                    //Update the spinner list accordingly.
                    updateBucketList(listBucketRunnable, mHandler, spinner);
                }else{
                    Toast.makeText(getApplicationContext(), "You don't have any buckets to destroy. Please create one.",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /** Method that updates the spinner list by querying the current existing buckets, taking their
     *  names and placing them in the spinner list so that user can see them.
     *
     * @param listBuckets                   the param that handles the thread execution to obtain
     *                                      the list of buckets.
     * @param mHandler                      handler object that updates the spinner.
     * @param spinner                       spinner object that gets updated via the handler.
     */
    public void updateBucketList(final ListBucketRunnable listBuckets, Handler mHandler, final Spinner spinner) {
        //Execute the thread to get the list of bucket names.
        ExecutorService execute = Executors.newFixedThreadPool(1);
        execute.execute(listBuckets);
        execute.shutdown();
        while (!execute.isTerminated()){};

        //Wait til it's done getting the list and update the spinner list with this new bucket list
        mHandler.post(new Runnable(){
            public void run(){
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(BucketActionActivity.this,
                        android.R.layout.simple_spinner_item, listBuckets.listBucketNames());
                spinner.setAdapter(adapter);
            }
        });
    }

    /** Inner class created that implements the Runnable interface, to update the
     *  spinner list of buckets.
     *
     */
    public class ListBucketRunnable implements Runnable{
        //Instance variables defined here
        private List<Bucket> listBucket;
        private ArrayList<String> bucketNames = new ArrayList<String>();

        /** Method that is used by the runnable interface to update the spinner list of buckets.
         *
         */
        public void run(){
            listBucket = bucketManager.listBuckets();
            bucketNames = new ArrayList<String>();
            for (int i = 0; i < listBucket.size(); i++){
                Bucket bucket = listBucket.get(i);
                if (bucket.getName().contains("-" + userName + "-")) {
                    bucketNames.add(bucket.getName() + "");
                }
            }
        }

        /** Method that returns the list of bucket names
         *
         * @return                          returns the list of bucket names.
         */
        public ArrayList<String> listBucketNames(){
            return bucketNames;
        }
    }

    /** Inner class created that implements the Runnable interface to check to see if a
     *  certain bucket is empty.
     *
     */
    public class IsBucketEmptyRunnable implements Runnable{
        //Define instance variables.
        private BucketManager newBucket;
        private boolean isBucketEmpty = false;

        /** Constructor that defines the initial properties of this class, primarily we
         *  are passing the bucket name of which we check.
         *
         * @param newBucket                 BucketManager object is used to call a method
         *                                  to determine if a bucket is empty.
         */
        public IsBucketEmptyRunnable(BucketManager newBucket){
            this.newBucket = newBucket;
        }

        /** Method that is used by the runnable interface to check to see if a bucket is
         *  empty or not.
         *
         */
        public void run() {
            isBucketEmpty = newBucket.isBucketEmpty();
        }

        /** Method that returns a boolean if a bucket is empty or not.
         *
         * @return                          returns true/false if the bucket is empty.
         */
        public boolean isBucketEmpty(){
            return isBucketEmpty;
        }
    }

    /** Inner class created that implements the Runnable interface to check to see if a
     *  certain bucket (passed as a param) exists.
     *
     */
    public class BucketExistsRunnable implements Runnable{
        //Define instance variables.
        private BucketManager newBucket;
        private boolean bucketExist = false;

        /** Constructor that defines the initial properties of this class, primarily we
         *  are passing the bucket name of which we check.
         *
         * @param newBucket                 BucketManager object is used to call a method
         *                                  to determine if a bucket actually exists.
         */
        public BucketExistsRunnable(BucketManager newBucket){
            this.newBucket = newBucket;
        }

        /** Method that is used by the runnable interface to check to see if a bucket is
         *  empty or not.
         *
         */
        public void run() {
            bucketExist = newBucket.doesBucketExist();
        }

        /** Method that returns a boolean if a bucket exists or not.
         *
         * @return                          returns true/false if the bucket exists.
         */
        public boolean doesBucketExist(){
            return bucketExist;
        }
    }

    /** Inner class that uses the AsyncTask class to deal with calling the external activity
     *  to have the gallery show up on screen for the user to select a photo.
     *
     */
    public class SelectImageTask extends AsyncTask<Void, Void, Void> {

        /** Method required by the AsyncTask class that causes the gallery to show up
         *  and have the user select photos.
         *
         * @param unused                    param is not used.
         * @return                          returns nothing.
         */
        @Override
        protected Void doInBackground(Void... unused) {
            try {
                Intent chooseImage = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(chooseImage, 1);
            }   catch(Exception e) {
            }
            return null;
        }
    }

    /** Inner Class which handles the Fill bucket function that actually adds the photo
     *  to the existing bucket.
     *
     */
    public class FillBucketTask extends AsyncTask<String, Integer, Void> {

        /** Method that adds the selected photo to the bucket
         *
         * @param strings                       string array that passes the file
         * @return                              returns nothing
         */
        protected Void doInBackground(String... strings) {
            try {
                bucketManager.fillBucket(new File(strings[0]));
            }   catch(Exception e) {}
            return null;
        }

        /** Method that tells the user that the upload was successful.
         *
         * @param result                        returns nothing. The Toast takes care of that.
         */
        protected void onPostExecute(Void result) {
            Toast.makeText(getApplicationContext(), "Upload Complete", Toast.LENGTH_LONG).show();
        }
    }

    /** Method call dealt with having the photo selected. This method processes that selection
     *  and accordingly passing the data along.
     *
     * @param requestCode                       code that requests for the gallery.
     * @param resultCode                        code given back to the app from the gallery based
     *                                          upon the user's selection.
     * @param data                              file selected.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //If the code is proper the data is there and the image is selected,
        //we're going to upload the image.
        if (requestCode == 1 && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            new FillBucketTask().execute(picturePath);
        }
    }

    /** Android Studio auto generated method calls.
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /** Android Studio auto generated method calls.
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent();
            i.setClass(BucketActionActivity.this, MainActivity.class);
            //Launch the next activity.
            finish();
            startActivity(i);
            Toast.makeText(getApplicationContext(), "Log out complete!",
                    Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}