package utc_4910.screenActivities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import utc_4910.photoencryptionincloud.BucketManager;
import utc_4910.photoencryptionincloud.R;

/**
 * Created by Matthew Jallouk on 2/27/2015.
 */
public class CreateBucketActivity extends ActionBarActivity {

    //Define instance variables.
    private EditText bucketNameEdit = null;
    private Spinner encryptionPolicySpinner = null;
    private Button createBucketButton = null;
    private BucketManager bucketManager = new BucketManager("capstone");
    private RelativeLayout relativeLayout = null;
    private String userName;

    /** Method that is run at the start of this activity being called
     *
     * @param savedInstanceState        state of the previous instance being saved.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Initialize main properities of screen.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_bucket_activity);
        setTitle("Create Bucket Screen");

        //Obtain passed data and initialize private variables.
        userName = (String)getIntent().getSerializableExtra("UserName");
        bucketNameEdit = (EditText)findViewById(R.id.bucketNameEdit);
        encryptionPolicySpinner = (Spinner)findViewById(R.id.encryptionPolicySpinner);
        createBucketButton = (Button)findViewById(R.id.createBucket);
        relativeLayout = (RelativeLayout)findViewById(R.id.relativeLayout);

        //Set encryption policies
        ArrayList<String> encryptionPolicies = new ArrayList<String>();
        encryptionPolicies.add("No Encryption");
        encryptionPolicies.add("Strong Encryption");
        encryptionPolicies.add("Very Strong Encryption");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(CreateBucketActivity.this,
                android.R.layout.simple_spinner_item, encryptionPolicies);
        encryptionPolicySpinner.setAdapter(adapter);

        bucketManager.setBucketName(bucketNameEdit.getText().toString());

        //Listen for events.
        this.runButtonListeners();
    }

    /** Method that holds all of the event detections. When a button is clicked this method
     * holds and runs a series of commands based upon which button was clicked.
     *
     */
    public void runButtonListeners(){

        /** Event listener that clears away the keyboard when the user taps any other region of the screen,
         *  with the exception of the editText field.
         *
         */
        relativeLayout.setOnClickListener(new RelativeLayout.OnClickListener(){
            public void onClick(View v){
                Activity activity = CreateBucketActivity.this;
                InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
            }
        });

        /** Event listener that listens for when the spinner is tapped. It modifies the current selected
         *  encryption policy.
         *
         */
        encryptionPolicySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedItem = encryptionPolicySpinner.getSelectedItem().toString();
                String bucketName = bucketNameEdit.getText().toString().toLowerCase().trim();
                bucketName = bucketName.replace(" ", "");
                if (selectedItem.equals("No Encryption")){
                    bucketName = "non-" + userName + "-" + bucketName;
                    bucketManager.setBucketName(bucketName);
                }else if (selectedItem.equals( "Strong Encryption")){
                    bucketName = "som-" + userName + "-" + bucketName;
                    bucketManager.setBucketName(bucketName);
                }else if (selectedItem.equals("Very Strong Encryption")){
                    bucketName = "all-" + userName + "-" + bucketName;
                    bucketManager.setBucketName(bucketName);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

        /** Event listener that occurs when the user taps the "Create Bucket" button.
         *  This listener throws an error if we have ill-formed bucket names.
         */
        createBucketButton.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                //Check for illegal bucket names.
                String badSymbols = "~`!@#$%^&*()_+=,<>?/;:'[{}]";
                String bucketName = bucketNameEdit.getText().toString().toLowerCase().trim();
                String selectedItem = encryptionPolicySpinner.getSelectedItem().toString();
                bucketName = bucketName.replace(" ", "");
                if (bucketName.startsWith(".")){
                    Toast.makeText(getApplicationContext(), "Sorry! A bucket can't start with a period.",
                            Toast.LENGTH_LONG).show();
                }else if ( (bucketName.contains(badSymbols.substring(0, 1 ))) ||
                             (bucketName.contains(badSymbols.substring(1, 2 ))) ||
                             (bucketName.contains(badSymbols.substring(2, 3 ))) ||
                             (bucketName.contains(badSymbols.substring(3, 4 ))) ||
                             (bucketName.contains(badSymbols.substring(4, 5 ))) ||
                             (bucketName.contains(badSymbols.substring(5, 6 ))) ||
                             (bucketName.contains(badSymbols.substring(6, 7 ))) ||
                             (bucketName.contains(badSymbols.substring(7, 8 ))) ||
                             (bucketName.contains(badSymbols.substring(8, 9 ))) ||
                             (bucketName.contains(badSymbols.substring(9, 10 ))) ||
                             (bucketName.contains(badSymbols.substring(10, 11 ))) ||
                             (bucketName.contains(badSymbols.substring(11, 12 ))) ||
                             (bucketName.contains(badSymbols.substring(12, 13 ))) ||
                             (bucketName.contains(badSymbols.substring(13, 14 ))) ||
                             (bucketName.contains(badSymbols.substring(14, 15 ))) ||
                             (bucketName.contains(badSymbols.substring(15, 16 ))) ||
                             (bucketName.contains(badSymbols.substring(16, 17 ))) ||
                             (bucketName.contains(badSymbols.substring(17, 18 ))) ||
                             (bucketName.contains(badSymbols.substring(18, 19 ))) ||
                             (bucketName.contains(badSymbols.substring(19, 20 ))) ||
                             (bucketName.contains(badSymbols.substring(20, 21 ))) ||
                             (bucketName.contains(badSymbols.substring(21, 22 ))) ||
                             (bucketName.contains(badSymbols.substring(22, 23 ))) ||
                             (bucketName.contains(badSymbols.substring(23, 24 ))) ||
                             (bucketName.contains(badSymbols.substring(25, 26 ))) ||
                             (bucketName.contains(badSymbols.substring(26, 27 ))) ||
                             (bucketName.contains("\""))) {
                    Toast.makeText(getApplicationContext(), "Sorry! A bucket can't contain symbols (except for periods).",
                            Toast.LENGTH_LONG).show();

                    //Otherwise, construct the appropriate bucket.
                }else{
                    if (selectedItem.equals("No Encryption")) {
                        bucketName = "non-" + userName + "-" + bucketName;
                        bucketManager.setBucketName(bucketName);
                    } else if (selectedItem.equals("Strong Encryption")) {
                        bucketName = "som-" + userName + "-" + bucketName;
                        bucketManager.setBucketName(bucketName);
                    } else if (selectedItem.equals("Very Strong Encryption")) {
                        bucketName = "all-" + userName + "-" + bucketName;
                        bucketManager.setBucketName(bucketName);
                    }

                    //Call our create bucket via a thread.
                    Runnable r = new Runnable() {
                        public void run() {
                            bucketManager.createBucket();
                        }
                    };
                    ExecutorService executeT1 = Executors.newFixedThreadPool(1);
                    executeT1.execute(r);
                    executeT1.shutdown();
                    while (!executeT1.isTerminated()) {};

                    //Return message and transfer back to original screen.
                    Toast.makeText(getApplicationContext(), "'" + bucketManager.getBucketName() + "' bucket created!",
                            Toast.LENGTH_LONG).show();
                    Intent i = new Intent();
                    i.putExtra("UserName", userName);
                    i.setClass(CreateBucketActivity.this, BucketActionActivity.class);
                    finish();
                    startActivity(i);
                }
            }
        });
    }
}