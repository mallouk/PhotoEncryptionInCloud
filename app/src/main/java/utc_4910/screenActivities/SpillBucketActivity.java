package utc_4910.screenActivities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.amazonaws.services.s3.model.S3Object;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import utc_4910.photoencryptionincloud.BucketManager;
import utc_4910.photoencryptionincloud.R;

/**
 * Created by Matthew Jallouk on 2/18/2015.
 */
public class SpillBucketActivity extends Activity{

    //Variables defined here for use throughout the class
    private ListView spilledBucketList = null;
    private BucketManager bucketManager = null;
    private Button unselectAllButton = null;
    private Button downloadItemsButton = null;
    private Button deleteItemsButton = null;
    private ProgressDialog downloadProgress = null;
    private ArrayList<String> checkedItems = new ArrayList<String>();
    private String bucketName = "";
    private TextView emptyView = null;

    private int numItemsInBucket = 0;
    /** Method that is run at the start of this activity being called
     *
     * @param savedInstanceState        state of the previous instance being saved.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_bucketlist_activity);

        //Instantiation of the various objects on the screen and other instance variables defined
        spilledBucketList = (ListView) findViewById(R.id.spilledBucketList);
        TextView bucketView = (TextView) findViewById(R.id.bucketView);
        unselectAllButton = (Button)findViewById(R.id.unselectAllButton);
        downloadItemsButton = (Button)findViewById(R.id.downItemsButton);
        deleteItemsButton = (Button)findViewById(R.id.deleteItemsButton);
        downloadProgress = new ProgressDialog(SpillBucketActivity.this);
        bucketName = (String)getIntent().getSerializableExtra("BucketName");
        bucketManager = new BucketManager(bucketName);

        bucketView.setText(bucketName + " objects listed below...");

        emptyView = (TextView)findViewById(R.id.emptyView);
        //Spill the contents of the current bucket onto the screen in a list form.
        new SpillBucketTask(bucketName).execute();

        //Listen for events such as button clicks.
        this.runListeners();
    }

    /** Method that holds all of the event detections. When a button is clicked this method
     * holds and runs a series of commands based upon which button was clicked.
     *
     */
    public void runListeners(){

        /** This AdapterView listener runs when any of the items in the ListView are clicked.
         *  The items that are clicked, those file names are "checked" and added to an ArrayList
         *  of checked items so that they can be downloaded/deleted, depending on the next action
         *  that the user chooses.
         *
         */
        spilledBucketList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String fileName = String.valueOf(parentView.getItemAtPosition(position));
                if (spilledBucketList.isItemChecked(position)){ //Item is selected
                    checkedItems.add(fileName);
                }else{
                    checkedItems.remove(fileName);
                }
            }
        });

        /** This button listener unselects all of the items in the ListView. It also clears the
         *  ArrayList and makes it empty.
         *
         */
        unselectAllButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                for (int i = 0; i < spilledBucketList.getAdapter().getCount(); i++) {
                    spilledBucketList.setItemChecked(i, false);
                }

                for (int i = 0; i < checkedItems.size(); i++){
                    checkedItems.remove(i);
                }
            }
        });

        /** This button listener runs when the download button is selected, this downloads
         *  all of the checked files.
         *
         */
        downloadItemsButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                for (int i = 0; i < checkedItems.size(); i++) {
                    DownloadFile downloadTask = new DownloadFile(i);
                    downloadTask.execute();
                }
            }
        });

        /** In a similar fashion as the button listener above for downloading, this function
         *  runs when the delete button is selected, this function deletes all of the checked
         *  items from the bucket and updates the listView accordingly.
         *
         */
        deleteItemsButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                Runnable r = new Runnable() {
                    public void run(){
                        for (int i = 0; i < checkedItems.size(); i++) {
                            String fileName = checkedItems.get(i);
                            bucketManager.deleteObjectInBucket(fileName);
                        }
                    }
                };

                ExecutorService executeT1 = Executors.newFixedThreadPool(1);
                executeT1.execute(r);
                executeT1.shutdownNow();
                while (!executeT1.isTerminated()){};
                new SpillBucketTask(bucketName).execute();

                for (int i = 0; i < checkedItems.size(); i++){
                    checkedItems.remove(i);
                }
            }
        });
    }

    /** This class is used by the listeners to download the files to the target device. It uses
     *  the AsyncTask to do this.
     *
     */
    public class DownloadFile extends AsyncTask<String, Integer, Void> {
        //Define instance variables
        private int i;

        /** Constructor defined to update the index of the loop we're on (depending on which
         * file download we're on).
         *
         * @param index
         */
        public DownloadFile(int index){
            i = index;
        }

        /** Settings set before the download takes places, this sets up the properties for the
         *  download and the download updater.
         *
         */
        public void onPreExecute() {
            downloadProgress.setIndeterminate(true);
            downloadProgress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            downloadProgress.setCancelable(true);
            downloadProgress.setMessage("Downloading... (" + (i+1) + "/" + checkedItems.size() + ")");
            downloadProgress.show();
        }

        /** Progress bar method that gets updated on each run.
         *
         * @param progress
         */
        public void onProgressUpdate(Integer... progress) {
            downloadProgress.setMessage("Downloading... (" + (i+1) + "/" + checkedItems.size() + ")");
            downloadProgress.setIndeterminate(false);
            downloadProgress.setMax(100);
            downloadProgress.setProgress(progress[0]);
        }

        /** Method to download the file.
         *
         * @param strings
         * @return
         */
        public Void doInBackground(String... strings) {
            if (checkedItems.size() != 0) {
                try {

                   //Toast.makeText(getApplicationContext(), "Download finished", Toast.LENGTH_LONG).show();
                    String file = checkedItems.get(i);

                    S3Object downloadedFile = bucketManager.spillBucket(file);
                    String downloadedFileName = bucketManager.getBucketName() + "_" + file;
                    String downloadedFilePath = Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_DOWNLOADS) + "/" + downloadedFileName;

                    FileOutputStream downloadedFileStream = new FileOutputStream(downloadedFilePath);;
                    InputStream fileContentStream = downloadedFile.getObjectContent();;

                    //How much of file has been downloaded up to this point.
                    int receivedBytes = 0;
                    //The size of the file
                    long fileSize = downloadedFile.getObjectMetadata().getContentLength();
                    int chunkSize;
                    byte[] downloadBuffer = new byte[1024];
                    while ((chunkSize = fileContentStream.read(downloadBuffer)) > 0) {
                        downloadedFileStream.write(downloadBuffer, 0, chunkSize);
                        //More of the file has been downloaded so update the progressbar
                        receivedBytes += chunkSize;
                        //Convert to percent of file downloaded
                        publishProgress((int) (receivedBytes * 100 / fileSize));
                    }
                    downloadedFileStream.close();
                    fileContentStream.close();

                } catch (Exception e) {}
            }else{
                Toast.makeText(getApplicationContext(), "No files selected.", Toast.LENGTH_LONG).show();
            }
            return null;
        }

        public void onPostExecute(ArrayList<String> filesInBucket) {

        }

    }

    /** Inner class that acts as a way to spill the contents of a particular bucket onto the listView
     *  screen.
     *
     */
    public class SpillBucketTask extends AsyncTask<Void, Void, ArrayList<String>> {
        //Define instance variables
        private String bucketName;

        /** Constructor that takes the bucketName that we are taking the contents of to spill.
         *
         * @param bucketName                    name of the bucket to spill.
         */
        public SpillBucketTask(String bucketName){
            this.bucketName = bucketName;
        }

        /** Method that runs when this task is executed. It lists the takes the objects of the
         *  bucket and lists the file names to have them placed in an Array.
         *
         * @param voids
         * @return                              return the list of items to be placed on the
         *                                      screen.
         */
        public ArrayList<String> doInBackground(Void... voids) {
            ArrayList<String> filesInBucket = null;
            try {
                filesInBucket = bucketManager.listObjectsInBucket(bucketName);
            } catch (Exception e) {}
            return filesInBucket;
        }

        /** After the execution of the method above, we wll then update our adapter to list the
         *  files on the screen and add that adapter onto the ListView to then show.
         *
         * @param filesInBucket
         */
        public void onPostExecute(ArrayList<String> filesInBucket) {
            if (filesInBucket.size() == 0){
                emptyView.setVisibility(View.VISIBLE);
            }else{
                emptyView.setVisibility(View.GONE);
            }

            ListAdapter list = new ArrayAdapter<String>(getApplicationContext(),
                    android.R.layout.simple_list_item_multiple_choice, filesInBucket);
            spilledBucketList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            spilledBucketList.setAdapter(list);
            numItemsInBucket = filesInBucket.size();

        }
    }
}