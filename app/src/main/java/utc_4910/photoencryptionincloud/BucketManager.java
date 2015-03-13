package utc_4910.photoencryptionincloud;

import android.util.Log;

import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.S3Object;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import utc_4910.bucketTypes.AmazonFullEncryptionS3Manager;
import utc_4910.bucketTypes.AmazonPartialEncryptionS3Manager;
import utc_4910.bucketTypes.AmazonS3Manager;

/**
 * Created by Matthew Jallouk on 2/2/2015.
 */

public class BucketManager implements Serializable{

    //Instance variables to be used by the BucketManager class.
    private AmazonS3Manager amazonS3Manager;
    private AmazonPartialEncryptionS3Manager partialEncryptionS3Manager;
    private AmazonFullEncryptionS3Manager fullEncryptionS3Manager;
    private String bucketName;
    private String encryptionPolicy;

    /** Constructor that defines the initial properties of this class object.
     *
     * @param bucketName            name of the bucket that is created and managed.
     */
    public BucketManager(String bucketName){
        this.bucketName = bucketName;
        this.encryptionPolicy = bucketName.substring(0,3);
        this.amazonS3Manager = new AmazonS3Manager();
        this.partialEncryptionS3Manager = new AmazonPartialEncryptionS3Manager();
        this.fullEncryptionS3Manager = new AmazonFullEncryptionS3Manager();
    }

    /** Method that uses the AWS API to create a bucket. We do this by calling methods from the
     *  AmazonS3Manager class.
     *
     */
    public void createBucket() {
        String encryptionPolicy = bucketName.substring(0, 3);
        if (encryptionPolicy.equals("non")) {
            this.amazonS3Manager.createBucket(bucketName);
        }else  if (encryptionPolicy.equals("som")){
            partialEncryptionS3Manager.createBucket(bucketName);
        }else  if (encryptionPolicy.equals("all")){
            fullEncryptionS3Manager.createBucket(bucketName);
        }
    }

    /** Method that uses the AWS API to delete a bucket (provided that it is empty). We do this by
     *  calling methods from the AmazonS3Manager class.
     *
     */
    public void destroyBucket() {
        String encryptionPolicy = bucketName.substring(0, 3);
        if (encryptionPolicy.equals("non")) {
            this.amazonS3Manager.deleteBucket(bucketName);
        }else  if (encryptionPolicy.equals("som")){
            this.partialEncryptionS3Manager.deleteBucket(bucketName);
        }else  if (encryptionPolicy.equals("all")){
            fullEncryptionS3Manager.deleteBucket(bucketName);
        }
    }

    /** Method that deletes a specific file from a particular bucket. We do this by calling methods
     *  from the AmazonS3Manager class.
     *
     * @param file              name of the file that will be deleted from a specific bucket.
     */
    public void deleteObjectInBucket(String file){
        String encryptionPolicy = bucketName.substring(0, 3);
        if (encryptionPolicy.equals("non")) {
            this.amazonS3Manager.deleteObjectInBucket(this.bucketName, file);
        }else if (encryptionPolicy.equals("som")) {
            this.partialEncryptionS3Manager.deleteObjectInBucket(this.bucketName, file);
        }else if (encryptionPolicy.equals("all")) {
            this.fullEncryptionS3Manager.deleteObjectInBucket(this.bucketName, file);
        }
    }

    /** Method that checks if a bucket exists on the server side. We do this by using the AWS API
     *  and calling methods from the AmazonS3Manager class.
     *
     * @return                  returns true/false whether or not the bucket exists.
     */
    public boolean doesBucketExist(){
        String encryptionPolicy = bucketName.substring(0, 3);
        if (encryptionPolicy.equals("non")) {
            return amazonS3Manager.bucketExist(bucketName);
        }else if (encryptionPolicy.equals("som")){
            return partialEncryptionS3Manager.bucketExist(bucketName);
        }else{ //encryptionPolicy equals "all"
            return fullEncryptionS3Manager.bucketExist(bucketName);
        }
    }

    /** Method that places an object into a particular bucket (based upon the param). This is
     *  done by interfacing ith the AmazonS3Manager class.
     *
     * @param file              file that gets placed into a particular bucket.
     */
    public void fillBucket(File file) {
        String encryptionPolicy = bucketName.substring(0, 3);
        if (encryptionPolicy.equals("non")) {
            this.amazonS3Manager.putObjectInBucket(this.bucketName, file);
        }else if (encryptionPolicy.equals("som")) {
            this.partialEncryptionS3Manager.putObjectInBucket(this.bucketName, file);
        }else  if (encryptionPolicy.equals("all")){
            this.fullEncryptionS3Manager.putObjectInBucket(this.bucketName, file);
        }
    }

    /** Method that allows a user to obtain the particular object file of an object stored
     *  in a certain bucket. We do this by calling methods from the AmazonS3Manager class.
     *
     * @param file              name of the file that will be used to obtain the S3Object
     * @return                  returns the S3Object instance of the param filename.
     */
    public S3Object spillBucket(String file){
        String encryptionPolicy = bucketName.substring(0, 3);
        Log.d("TEST", encryptionPolicy);
        if (encryptionPolicy.equals("non")) {
            return this.amazonS3Manager.getObjectInBucket(this.bucketName, file);
        }else if (encryptionPolicy.equals("som")) {
            return this.partialEncryptionS3Manager.getObjectInBucket(this.bucketName, file);
        }else{ //encryptionPolicy equals "all"
            Log.d("TEST", "Accessing All");
            return this.fullEncryptionS3Manager.getObjectInBucket(this.bucketName, file);
        }
    }

    /** Method that lists the objects in a particular bucket.
     *
     * @param bucketName        name of the bucket that will dump all of its contents.
     * @return                  returns all of the objects from the bucket name param.
     */
    public ArrayList<String> listObjectsInBucket(String bucketName){
        String encryptionPolicy = bucketName.substring(0, 3);
        if (encryptionPolicy.equals("non")) {
            return this.amazonS3Manager.listObjectsInBucket(bucketName);
        }else if (encryptionPolicy.equals("som")) {
            return this.partialEncryptionS3Manager.listObjectsInBucket(bucketName);
        }else{ //encryptionPolicy equals "all"
            return this.fullEncryptionS3Manager.listObjectsInBucket(bucketName);
        }
    }

    /** Method that lists the names of the buckets that are tied to the specific AWS
     *  account associated with the keys hardcoded above. We do this by calling methods
     *  from the AmazonS3Manager class.
     *
     * @return                  returns a list of bucketNames
     */
    public List<Bucket> listBuckets(){
        return amazonS3Manager.listBuckets();
    }

    /** Method that checks to see if a bucket is empty. We do this by calling methods from
     *  the AmazonS3Manager class.
     *
     * @return                  returns true/false of whether or not the bucket is empty.
     */
    public boolean isBucketEmpty(){
        return amazonS3Manager.isBucketEmpty(this.bucketName);
    }

    /** Method that sets the name/rename of the bucket to be managed
     *
     * @param bucketName            new name of the bucket
     */
    public void setBucketName(String bucketName){
        this.bucketName = bucketName;
    }

    /** Method that obtains the current name of the bucket from the BucketManager object.
     *
     * @return                  returns a string of the bucket name.
     */
    public String getBucketName(){
        return bucketName;
    }
}