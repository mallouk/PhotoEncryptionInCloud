package utc_4910.photoencryptionincloud;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Matthew Jallouk on 3/27/2015.
 */
public class FileKeyEncryption {

    //Define instance variables
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES";
    private static final String generatedKey = "NVlfZePyBORf6c4l";

    /** Method to encrypt the contents of the 'inputFile' and place them into the 'outputFile'.
     * The key to do this is the String 'key' variable. All of this is actually done within the
     * doCrypto method, the encrypt method acts as a wrapper.
     *
     * @param key                       key used to encrypt the file.
     * @param inputFile                 input file, the contents of this will be encrypted and placed
     *                                  within the outputFile
     * @param outputFile                this file will be encrypted with the contents of the inputFile.
     * @throws Exception                throws Exception if the files aren't found.
     */
    public static void encrypt(String key, File inputFile, File outputFile)
            throws Exception {
        doCrypto(Cipher.ENCRYPT_MODE, key, inputFile, outputFile);
    }

    /** Method to decrypt the contents of the 'encryptedFile' and place them into the 'decryptedFile'.
     * The key to do this is the String 'key' variable. All of this is actually done within the
     * doCrypto method, the decrypt method acts as a wrapper.
     *
     * @param key                       key used to encrypt the file.
     * @param inputFile                 input file, the contents of this will be decrypted and placed
     *                                  within the outputFile
     * @param outputFile                this file will be decrypted with the contents of the inputFile.
     * @throws Exception                throws Exception if the files aren't found.
     */
    public static void decrypt(String key, File inputFile, File outputFile)
            throws Exception {
        doCrypto(Cipher.DECRYPT_MODE, key, inputFile, outputFile);
    }

    /** Method that actually does the encryption/decryption and writes it the appropriate files.
     *
     * @param cipherMode                cipher mode, which algorithm will we use (AES in this case);
     *                                  as well as if we're decrypting or encrypting.
     * @param key                       actual key used for encryption/decryption.
     * @param inputFile                 file that will be encrypted/decrypted.
     * @param outputFile                the encrypted or decrypted file
     * @throws Exception
     */
    private static void doCrypto(int cipherMode, String key, File inputFile,
                                 File outputFile) throws Exception {
        try {
            //Generate the particular key and cipher that we will use for the
            //encryption or decryption.
            Key secretKey = new SecretKeySpec(key.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(cipherMode, secretKey);

            //Generate the FileInput Stream to get the encrypted/decrypted
            //contents of the file to either respectively decrypt/encrypt.
            FileInputStream inputStream = new FileInputStream(inputFile);
            byte[] inputBytes = new byte[(int) inputFile.length()];
            inputStream.read(inputBytes);

            //New encrypted/decrypted bytes to be written to the file in order to be secured
            //(if encrypted) or read by the program (if decrypted).
            byte[] outputBytes = cipher.doFinal(inputBytes);

            //Write these new bytes to the file.
            FileOutputStream outputStream = new FileOutputStream(outputFile);
            outputStream.write(outputBytes);

            //Close the streams.
            inputStream.close();
            outputStream.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /** Method that obtains the special hardcoded key to encrypt our key file
     *
     * @return                  returns special key
     */
    public static String getSpecialKey(){
        return generatedKey;
    }
}
