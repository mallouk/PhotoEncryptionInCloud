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

/**
 * Created by Matthew Jallouk on 2/28/2015.
 */
public class AmazonPartialEncryptionS3Manager implements Serializable {

    private String amazonAccessKeyID = new AmazonAccountKeys().getPublicKey();
    private String amazonPrivateKey = new AmazonAccountKeys().getPrivateKey();
    public AmazonS3 amazonS3Client;

    public AmazonPartialEncryptionS3Manager(){
        amazonS3Client = new AmazonS3Client(new BasicAWSCredentials(amazonAccessKeyID, amazonPrivateKey));
        amazonS3Client.setRegion(Region.getRegion(Regions.US_EAST_1));
    }

    public void createBucket(String bucketName){
        amazonS3Client.createBucket(bucketName);
    }


    public void putObjectInBucket(String bucketName, File file) {
        try {
            File folder = new File(Environment.getExternalStorageDirectory() + "/.AWS");
            String fileName = "/.keys";
            File keyFile = new File(folder + fileName);
            Scanner scan = new Scanner(keyFile);
            String record = "";
            String key1 = "";
            String key2 = "";
            String key3 = "";
            String key4 = "";

            while (scan.hasNextLine()) {
                record = scan.nextLine();
                String[] info = record.split(":::");
                key1 = info[2];
                key2 = info[3];
                key3 = info[4];
                key4 = info[5];
            }
            SSECustomerKey sseKey1 = new SSECustomerKey(key1);
            SSECustomerKey sseKey2 = new SSECustomerKey(key2);
            SSECustomerKey sseKey3 = new SSECustomerKey(key3);
            SSECustomerKey sseKey4 = new SSECustomerKey(key4);

            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, file.getName(), file)
                    .withSSECustomerKey(sseKey1).withSSECustomerKey(sseKey2)
                    .withSSECustomerKey(sseKey3).withSSECustomerKey(sseKey4);
            amazonS3Client.putObject(putObjectRequest);
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    public void deleteBucket(String bucketName){
        amazonS3Client.deleteBucket(bucketName);
    }


    public boolean bucketExist(String bucketName){
        return amazonS3Client.doesBucketExist(bucketName);
    }

    public void deleteObjectInBucket(String bucketName, String fileName){
        amazonS3Client.deleteObject(bucketName, fileName);
    }

    public ArrayList<String> listObjectsInBucket(String bucketName){
        ArrayList<String> fileNamesList = new ArrayList<String>();
        for(S3ObjectSummary file : amazonS3Client.listObjects(bucketName).getObjectSummaries()) {
            fileNamesList.add(file.getKey());
        }
        return fileNamesList;

        /*ArrayList<String> filesInBucket = new ArrayList<String>();
        for (int i = 0; i < amazonS3Client.listObjects(bucketName).getObjectSummaries().size(); i++){
            S3ObjectSummary fileObject = amazonS3Client.listObjects(bucketName).getObjectSummaries().get(i);
            filesInBucket.add(fileObject.getKey());
        }
        return filesInBucket;*/
    }


    public S3Object getObjectInBucket(String bucketName, String file){
        S3Object s3Object = null;
        try {
            File folder = new File(Environment.getExternalStorageDirectory() + "/.AWS");
            String fileName = "/.keys";
            File keyFile = new File(folder + fileName);
            Scanner scan = new Scanner(keyFile);

            String record = "";
            String key1 = "";
            String key2 = "";
            String key3 = "";
            String key4 = "";

            while (scan.hasNextLine()) {
                record = scan.nextLine();
                String[] info = record.split(":::");
                key1 = info[2];
                key2 = info[3];
                key3 = info[4];
                key4 = info[5];
            }
            SSECustomerKey sseKey1 = new SSECustomerKey(key1);
            SSECustomerKey sseKey2 = new SSECustomerKey(key2);
            SSECustomerKey sseKey3 = new SSECustomerKey(key3);
            SSECustomerKey sseKey4 = new SSECustomerKey(key4);
            GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, file)
                    .withSSECustomerKey(sseKey1).withSSECustomerKey(sseKey2)
                    .withSSECustomerKey(sseKey3).withSSECustomerKey(sseKey4);

            s3Object= amazonS3Client.getObject(getObjectRequest);
        }catch(Exception e){
            e.printStackTrace();
        }
        return s3Object;
    }
}
