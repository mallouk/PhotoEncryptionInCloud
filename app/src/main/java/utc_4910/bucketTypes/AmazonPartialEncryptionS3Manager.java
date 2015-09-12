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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Scanner;

import utc_4910.photoencryptionincloud.AmazonAccountKeys;
import utc_4910.photoencryptionincloud.FileKeyEncryption;

/**
 * Created by Matthew Jallouk on 2/28/2015.
 */
public class AmazonPartialEncryptionS3Manager implements Serializable {

    private String amazonAccessKeyID = AmazonAccountKeys.getPublicKey();
    private String amazonPrivateKey = AmazonAccountKeys.getPrivateKey();
    private int partialKeyArrayLen = 4;
    private SSECustomerKey[] sseKey;
    private File keyFile;
    public AmazonS3 amazonS3Client;

    /** Constructor that is run to set the initial properties of the object.
     *
     */
    public AmazonPartialEncryptionS3Manager(){
        amazonS3Client = new AmazonS3Client(new BasicAWSCredentials(amazonAccessKeyID, amazonPrivateKey));
        amazonS3Client.setRegion(Region.getRegion(Regions.US_EAST_1));
        File folder = new File(Environment.getExternalStorageDirectory() + "/.AWS");
        String fileName = AmazonAccountKeys.getKeyFileName();
        keyFile = new File(folder + fileName);
    }

    /** Method that creates the bucket in the AWS cloud, it uses the bucket name taken as a param
     *  as the name of which to give it.
     *
     * @param bucketName                    name of the bucket
     */
    public void createBucket(String bucketName){
        amazonS3Client.createBucket(bucketName);
    }

    /** Method that deletes a bucket on the AWS account associated with the hardcoded keys above.
     *
     * @param bucketName        name of the bucket that will be destroyed.
     */
    public void deleteBucket(String bucketName){
        amazonS3Client.deleteBucket(bucketName);
    }

    /** Method that places a file into a particular bucket associated with the AWS keys above.
     *
     * @param bucketName        name of the bucket that will hold the uploaded file.
     * @param file              file that will uploaded to the AWS bucket.
     */
    public void putObjectInBucket(String bucketName, File file) {

        //Read the encrypted keys from the file stored on the device for the particular user.
        //We then use those keys to encrypt the photo we send up to the cloud.
        //The PutObjectRequest function encapsulates the SSL connection when transferring the file
        //to the server where it will be encrypted.
        try {
            parseFileAndGenerateKeys();

            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, file.getName(), file)
                    .withSSECustomerKey(sseKey[0]).withSSECustomerKey(sseKey[1])
                    .withSSECustomerKey(sseKey[2]).withSSECustomerKey(sseKey[3]);

            amazonS3Client.putObject(putObjectRequest);
            FileKeyEncryption.encrypt(FileKeyEncryption.getSpecialKey(), keyFile, keyFile);
        }catch(Exception e){
            e.printStackTrace();
        }
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

    /** Method that calls the particular bucket we have selected and obtains all of the
     *  photos from that bucket.
     *
     * @param bucketName        name of the bucket of which the contents we are spilling.
     * @return                  returns a list of photos to be displayed in a list.
     */
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
        S3Object s3Object = null;
        //The GetObjectRequest function encapsulates the SSL connection when transferring the file
        //from the server where it is decrypted.
        try {
            parseFileAndGenerateKeys();
            GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, file)
                    .withSSECustomerKey(sseKey[0]).withSSECustomerKey(sseKey[1])
                    .withSSECustomerKey(sseKey[2]).withSSECustomerKey(sseKey[3]);

            s3Object= amazonS3Client.getObject(getObjectRequest);
            FileKeyEncryption.encrypt(FileKeyEncryption.getSpecialKey(), keyFile, keyFile);
        }catch(Exception e){
            e.printStackTrace();
        }
        return s3Object;
    }

    /** Method that decrypts the key file, parses it, and extracts the various keys used to
     *  encrypt/decrypt the photos when uploaded/downloaded.
     *
     * @throws Exception        throws a FileNotFound exception
     */
    public void parseFileAndGenerateKeys() throws Exception{
        //Decrypt file
        FileKeyEncryption.decrypt(FileKeyEncryption.getSpecialKey(), keyFile, keyFile);

        //Define variables.
        Scanner scan = new Scanner(keyFile);
        String record = "";
        String[] keyStrings = new String[partialKeyArrayLen];

        //Go through file, find username and get the keys for that username to
        //encrypt/decrypt files.
        while (scan.hasNextLine()) {
            record = scan.nextLine();
            String[] info = record.split(":::");
            keyStrings[0] = info[2];
            keyStrings[1] = info[3];
            keyStrings[2] = info[4];
            keyStrings[3] = info[5];
        }
        sseKey = new SSECustomerKey[partialKeyArrayLen];
        for (int i = 0; i < partialKeyArrayLen; i++) {
            sseKey[i] = new SSECustomerKey(keyStrings[i]);
        }
    }
}
