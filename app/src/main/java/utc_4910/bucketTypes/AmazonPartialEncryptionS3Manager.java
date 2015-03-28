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


    public AmazonPartialEncryptionS3Manager(){
        amazonS3Client = new AmazonS3Client(new BasicAWSCredentials(amazonAccessKeyID, amazonPrivateKey));
        amazonS3Client.setRegion(Region.getRegion(Regions.US_EAST_1));
        File folder = new File(Environment.getExternalStorageDirectory() + "/.AWS");
        String fileName = AmazonAccountKeys.getKeyFileName();
        keyFile = new File(folder + fileName);
    }

    public void createBucket(String bucketName){
        amazonS3Client.createBucket(bucketName);
    }


    public void putObjectInBucket(String bucketName, File file) {
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
    }


    public S3Object getObjectInBucket(String bucketName, String file){
        S3Object s3Object = null;
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

    public void parseFileAndGenerateKeys() throws Exception{
        FileKeyEncryption.decrypt(FileKeyEncryption.getSpecialKey(), keyFile, keyFile);

        Scanner scan = new Scanner(keyFile);
        String record = "";
        String[] keyStrings = new String[partialKeyArrayLen];

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
