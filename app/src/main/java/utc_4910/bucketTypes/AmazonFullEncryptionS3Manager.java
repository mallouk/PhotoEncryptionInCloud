package utc_4910.bucketTypes;

import android.os.Environment;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.model.SSECustomerKey;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import utc_4910.photoencryptionincloud.AmazonAccountKeys;

/**
 * Created by Matthew Jallouk on 3/1/2015.
 */
public class AmazonFullEncryptionS3Manager {

    //Define instance variables
    private String amazonAccessKeyID = AmazonAccountKeys.getPublicKey();
    private String amazonPrivateKey = AmazonAccountKeys.getPrivateKey();
    public AmazonS3 amazonS3Client;

    /** Constructor that is run to set the initial properties of the object.
     *
     */
    public AmazonFullEncryptionS3Manager(){
        amazonS3Client = new AmazonS3Client(new BasicAWSCredentials(amazonAccessKeyID, amazonPrivateKey));
        amazonS3Client.setRegion(Region.getRegion(Regions.US_EAST_1));
    }

    /** Method that creates the bucket in the AWS cloud, it uses the bucket name taken as a param
     *  as the name of which to give it.
     *
     * @param bucketName                    name of the bucket
     */
    public void createBucket(String bucketName){
        amazonS3Client.createBucket(bucketName);
    }

    /** Method that places a file into a particular bucket associated with the AWS keys above.
     *
     * @param bucketName        name of the bucket that will hold the uploaded file.
     * @param file              file that will uploaded to the AWS bucket.
     */
    public void putObjectInBucket(String bucketName, File file) {

        //Read the encrypted keys from the file stored on the device for the particular user.
        //We then use those keys to encrypt the photo we send up to the cloud.
        try {
            //Read file
            File folder = new File(Environment.getExternalStorageDirectory() + "/.AWS");
            String fileName = AmazonAccountKeys.getKeyFile();
            File keyFile = new File(folder + fileName);
            Scanner scan = new Scanner(keyFile);

            //Parse file to get keys
            String record = "";
            String key1 = "";
            String key2 = "";
            String key3 = "";
            String key4 = "";
            String key5 = "";
            String key6 = "";
            String key7 = "";
            String key8 = "";
            while (scan.hasNextLine()) {
                record = scan.nextLine();
                String[] info = record.split(":::");
                key1 = info[2];
                key2 = info[3];
                key3 = info[4];
                key4 = info[5];
                key5 = info[6];
                key6 = info[7];
                key7 = info[8];
                key8 = info[9];
            }

            //Generate keys
            SSECustomerKey sseKey1 = new SSECustomerKey(key1);
            SSECustomerKey sseKey2 = new SSECustomerKey(key2);
            SSECustomerKey sseKey3 = new SSECustomerKey(key3);
            SSECustomerKey sseKey4 = new SSECustomerKey(key4);
            SSECustomerKey sseKey5 = new SSECustomerKey(key5);
            SSECustomerKey sseKey6 = new SSECustomerKey(key6);
            SSECustomerKey sseKey7 = new SSECustomerKey(key7);
            SSECustomerKey sseKey8 = new SSECustomerKey(key8);

            //Place request
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, file.getName(), file)
                    .withSSECustomerKey(sseKey1).withSSECustomerKey(sseKey2).withSSECustomerKey(sseKey3)
                    .withSSECustomerKey(sseKey4).withSSECustomerKey(sseKey5).withSSECustomerKey(sseKey6)
                    .withSSECustomerKey(sseKey7).withSSECustomerKey(sseKey8);

            amazonS3Client.putObject(putObjectRequest);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /** Method that deletes a bucket on the AWS account associated with the hardcoded keys above.
     *
     * @param bucketName        name of the bucket that will be destroyed.
     */
    public void deleteBucket(String bucketName){
        amazonS3Client.deleteBucket(bucketName);
    }

    /** Method that checks if a bucket exists on the AWS account or not, it returns true/false
     *  based upon if the bucket does exist.
     *
     * @param bucketName        name of the bucket that is checked whether or not it exists.
     * @return                  returns true/false on if the bucket exists.
     */
    public boolean bucketExist(String bucketName){
        return amazonS3Client.doesBucketExist(bucketName);
    }

    /** Method that deletes an object from a specific bucket.
     *
     * @param bucketName        name of the bucket that contains the file to be deleted
     * @param fileName          name of the file to be deleted
     */
    public void deleteObjectInBucket(String bucketName, String fileName){
        amazonS3Client.deleteObject(bucketName, fileName);
    }


    public ArrayList<String> listObjectsInBucket(String bucketName){
        ArrayList<String> fileNamesList = new ArrayList<String>();
        for(S3ObjectSummary file : amazonS3Client.listObjects(bucketName).getObjectSummaries()) {
            fileNamesList.add(file.getKey());
        }
        return fileNamesList;
    }

    /** Method that obtains a particular file object from a specific bucket and downloads
     *  this object to the user's device, we use the keys to decrypt the encrypted file.
     *
     * @param bucketName        name of the bucket that contains the file to be downloaded
     * @param file              name of the file to be downloaded to the user's device
     * @return                  returns the s3Object key of the specific file from the bucket
     */
    public S3Object getObjectInBucket(String bucketName, String file){

        //Read the encrypted keys from the file stored on the device for the particular user.
        //We then use those keys to decrypt the photo we sent up to the cloud.
        S3Object s3Object = null;
        try {
            File folder = new File(Environment.getExternalStorageDirectory() + "/.AWS");
            String fileName = AmazonAccountKeys.getKeyFile();
            File keyFile = new File(folder + fileName);
            Scanner scan = new Scanner(keyFile);

            String record = "";
            String key1 = "";
            String key2 = "";
            String key3 = "";
            String key4 = "";
            String key5 = "";
            String key6 = "";
            String key7 = "";
            String key8 = "";
            //Find particular keys in file
            while (scan.hasNextLine()) {
                record = scan.nextLine();
                String[] info = record.split(":::");
                key1 = info[2];
                key2 = info[3];
                key3 = info[4];
                key4 = info[5];
                key5 = info[6];
                key6 = info[7];
                key7 = info[8];
                key8 = info[9];
            }

            //Decrypt the file and send that stream to return
            SSECustomerKey sseKey1 = new SSECustomerKey(key1);
            SSECustomerKey sseKey2 = new SSECustomerKey(key2);
            SSECustomerKey sseKey3 = new SSECustomerKey(key3);
            SSECustomerKey sseKey4 = new SSECustomerKey(key4);
            SSECustomerKey sseKey5 = new SSECustomerKey(key5);
            SSECustomerKey sseKey6 = new SSECustomerKey(key6);
            SSECustomerKey sseKey7 = new SSECustomerKey(key7);
            SSECustomerKey sseKey8 = new SSECustomerKey(key8);

            GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, file)
                    .withSSECustomerKey(sseKey1).withSSECustomerKey(sseKey2).withSSECustomerKey(sseKey3)
                    .withSSECustomerKey(sseKey4).withSSECustomerKey(sseKey5).withSSECustomerKey(sseKey6)
                    .withSSECustomerKey(sseKey7).withSSECustomerKey(sseKey8);

            s3Object= amazonS3Client.getObject(getObjectRequest);
        }catch(Exception e){
            e.printStackTrace();
        }
        return s3Object;
    }
}
