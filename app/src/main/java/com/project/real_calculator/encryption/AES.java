package com.project.real_calculator.encryption;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES encryption
 * Cipher is not thread safe
 * Use encrypt and decrypt one at a time
 */
public class AES
{
    //~~~setting data
    private static SecretKeySpec secretKey;
    private static IvParameterSpec iv;
    private IvParameterSpec mIv;

    public static final String AES_TRANSFORMATION_CTR = "AES/CTR/NoPadding";
    public static final String AES_TRANSFORMATION_CBC = "AES/CBC/PKCS7Padding";

    // some Intend within the app calls the AppController background method unintentionally
    // allowClearing set to false right before the intend is called
    // when the activity is done, set allowClear back to true
    private static boolean allowClearing = true;

    private static byte[] decryptedBytes;
    private static byte[] encryptedBytes;

    public AES(byte[] nonce){
        if (nonce.length>=12){
            int rngLen = 12;
            byte[] slice = Arrays.copyOfRange(nonce, 0, rngLen);
            byte[] myIv = new byte[16];
            System.arraycopy(slice, 0, myIv, 0, rngLen);
            mIv = new IvParameterSpec(myIv);
        }else{
            byte[] myIv = new byte[16];
            System.arraycopy(nonce, 0, myIv, 0, nonce.length);
            mIv = new IvParameterSpec(myIv);
        }
    }
    public byte[] glideDataFetcherDecrypt(byte[] content){

        Cipher c = null;
        byte[] result = new byte[0];
        try{
            c = Cipher.getInstance(AES_TRANSFORMATION_CTR);
            c.init(Cipher.DECRYPT_MODE, secretKey, mIv);
            result = c.doFinal(content);
        }
        catch(Exception e){
            System.out.println("Error occurred while " +
                    "encrypting: " + e.toString());
        }
        return result;
    }
    public Cipher glideDecryptCipher(){
        Cipher c = null;
        try{
            c = Cipher.getInstance(AES_TRANSFORMATION_CTR);
            c.init(Cipher.DECRYPT_MODE, secretKey, mIv);
        }
        catch(Exception e){
            System.out.println("Error occurred while " +
                    "encrypting: " + e.toString());
        }
        return c;
    }

    //~~~set key method
    public static void setKey(String myKey)
    {
        MessageDigest sha = null;
        try{
            byte[] key = myKey.getBytes(StandardCharsets.UTF_8);
            sha = MessageDigest.getInstance("SHA-256"); // makes 256 bit hash
            key = sha.digest(key);
            key = Arrays.copyOf(key, 32); // SecretKeySpec only supports 16, 24, 32 bytes
            secretKey = new SecretKeySpec(key, "AES");
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
    }

    //~~~set IV method
    public static void setIV(String initVector)
    {
        try
        {
            initVector = initVector.trim();
            initVector = initVector.substring(0,12);
            // IV for CTR mode, reusing IV is bad
            // IV is a nonce followed by a counter (starting at 0). The IV is always 128 bit long.
            // IV in hex looks for example: ffffffffffffffffffffffff00000000
            //                              |nonce                  |counter
            byte[] nonce = initVector.getBytes(StandardCharsets.UTF_8);
            byte[] myIv = new byte[16];
            System.arraycopy(nonce, 0, myIv, 0, nonce.length);
            iv = new IvParameterSpec(myIv);
        }
        catch (Exception e) {
            System.out.println("Error while setting " +
                    "initial vector: " + e.toString());
        }
    }
    public static void setIV(byte[] nonce){
        try{
            if (nonce.length>=12){
                int rngLen = 12;
                byte[] slice = Arrays.copyOfRange(nonce, 0, rngLen);
                byte[] myIv = new byte[16];
                System.arraycopy(slice, 0, myIv, 0, rngLen);
                iv = new IvParameterSpec(myIv);
            }else{
                byte[] myIv = new byte[16];
                System.arraycopy(nonce, 0, myIv, 0, nonce.length);
                iv = new IvParameterSpec(myIv);
            }
        }
        catch (Exception e){
            System.out.println("Error while setting " +
                    "initial vector: " + e.toString());
        }
    }

    //~~~Getters and Setters
    public static byte[] getDecryptedBytes()
    {
        return decryptedBytes;
    }
    public static void setDecryptedBytes(byte[] arr)
    {
        AES.decryptedBytes = arr;
    }
    public static byte[] getEncryptedBytes()
    {
        return encryptedBytes;
    }
    public static void setEncryptedBytes(byte[] arr)
    {
        AES.encryptedBytes = arr;
    }
    public static SecretKeySpec getSecretKey(){
        return secretKey;
    }
    public static IvParameterSpec getIv(){
        return iv;
    }

    public static boolean getAllowClearing() {
        return allowClearing;
    }

    public static void setAllowClearing(boolean allowClearing) {
        AES.allowClearing = allowClearing;
    }

    //~~~Encrypt and Decrypt
    private static byte[] doCrypto(byte[] content, int mode){
        try{
            Cipher c = Cipher.getInstance(AES_TRANSFORMATION_CTR);
            c.init(mode, secretKey, iv);
            return c.doFinal(content);
        }
        catch(Exception e){
            System.out.println("Error occurred while " +
                    "encrypting: " + e.toString());
        }
        return new byte[0];
    }
    public static byte[] encrypt(byte[] content){
        return doCrypto(content, Cipher.ENCRYPT_MODE);
    }
    public static byte[] decrypt(byte[] content){
        return doCrypto(content, Cipher.DECRYPT_MODE);
    }

    private static Cipher getCipher(int mode) throws Exception {
        Cipher c = Cipher.getInstance(AES_TRANSFORMATION_CTR);
        c.init(mode, secretKey, iv);
        return c;
    }
    public static Cipher getEncryptionCipher(){
        try{
            return getCipher(Cipher.ENCRYPT_MODE);
        }
        catch(Exception e){
            System.out.println("Error occurred while " +
                    "encrypting: " + e.toString());
        }
        return null;
    }
    public static Cipher getDecryptionCipher(){
        try{
            return getCipher(Cipher.DECRYPT_MODE);
        }
        catch(Exception e){
            System.out.println("Error occurred while " +
                    "decrypting: " + e.toString());
        }
        return null;
    }

    public static void clear(){
        if (allowClearing) {
            secretKey = null;
            iv = null;
        }
    }

    //~~~~Test Codes~~~~
    public static void main(String[]args)
    {
        //set the initial values
        final String strToEncrypt = "hello";
        final byte[] byteArrToEncrypt = new byte[] {-1,-40,-1,0,0,0,0,0,0,0,0,0,0,(byte)0xd9};
        final String strPassword = "Password123";
        final String iv = "Randomstuff23456"; //has to be 16 characters
        String decoded = "";

        //set key, set IV, encrypt string
        AES.setKey(strPassword);
        AES.setIV(iv);
        byte[] b = AES.encrypt(strToEncrypt.trim().getBytes(StandardCharsets.UTF_8));
        // debug with println
        System.out.println("String to Encrypt: " + strToEncrypt);
        System.out.println("Encrypted: " + java.util.Arrays.toString(b));
        String tmp = new String(b, StandardCharsets.ISO_8859_1);
        System.out.println("getEncryptedBytes: "+ tmp);
        //decrypting, and debugging
        b = AES.decrypt(tmp.getBytes(StandardCharsets.ISO_8859_1));

        decoded = new String(b, StandardCharsets.UTF_8);
        System.out.println("Decrypted Bytes: " + decoded);

        //encrypting byte array
        b = AES.encrypt(byteArrToEncrypt);
        System.out.println("Byte array to encrypt: " + java.util.Arrays.toString(byteArrToEncrypt));
        System.out.println("Encrypted array: " + java.util.Arrays.toString(b));

        //decrypting byte array
        b = AES.decrypt(b);
        System.out.println("Decrypted array: " + java.util.Arrays.toString(b));

    }
}