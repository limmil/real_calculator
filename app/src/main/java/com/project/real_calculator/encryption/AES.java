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
    //~~~thread flag
    private static boolean running = false;
    //~~~setting data
    private static SecretKeySpec secretKey;
    private static IvParameterSpec iv;

    private static byte[] decryptedBytes;
    private static byte[] encryptedBytes;

    private AES(){}

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
            initVector = initVector.substring(0,16);
            iv = new IvParameterSpec(initVector.getBytes(StandardCharsets.UTF_8));
        }
        catch (Exception e) {
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


    //~~~Encrypt and Decrypt
    public static byte[] doCrypto(byte[] content, int mode){
        try{
            Cipher c = Cipher.getInstance("AES/CBC/PKCS7Padding");
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