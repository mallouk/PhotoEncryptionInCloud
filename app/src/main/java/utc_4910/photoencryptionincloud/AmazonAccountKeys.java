package utc_4910.photoencryptionincloud;

/**
 * Created by Matthew Jallouk on 2/26/2015.
 */
public class AmazonAccountKeys {

    //Defined instance variables
    private String publicKey = "";
    private String privateKey = "";

    /** Constructor that defines initial properties
     *
     */
    public AmazonAccountKeys(){

    }

    /** Method that returns hardcoded public key to the AWS account.
     *
     * @return                      returns public key.
     */
    public String getPublicKey(){
        return this.publicKey;
    }

    /** Method that returns the hardcoded private key to the AWS account.
     *
     * @return                      returns private key.
     */
    public String getPrivateKey(){
        return this.privateKey;
    }
}
