package com.project.real_calculator.encryption;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES encryption created by Quan on 6/1/2016.
 */
public class AES
{
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
    public static void encrypt(byte[] arr)
    {
        try{
            Cipher c = Cipher.getInstance("AES/CBC/PKCS7Padding");
            c.init(Cipher.ENCRYPT_MODE, secretKey, iv);
            setEncryptedBytes(c.doFinal(arr));
        }
        catch(Exception e){
            System.out.println("Error occurred while " +
                    "encrypting: " + e.toString());
        }
    }
    public static byte[] encryptb(byte[] arr)
    {
        try{
            Cipher c = Cipher.getInstance("AES/CBC/PKCS7Padding");
            c.init(Cipher.ENCRYPT_MODE, secretKey, iv);
            return c.doFinal(arr);
        }
        catch(Exception e){
            System.out.println("Error occurred while " +
                    "encrypting: " + e.toString());
        }
        return new byte[0];
    }
    public static void encrypt(String str)
    {
        try {
            encrypt(str.getBytes(StandardCharsets.UTF_8));
        }
        catch (Exception e) {
            System.out.println("Error occurred while " +
                    "encrypting: " + e.toString());
        }
    }
    public static void decrypt(byte[] arr)
    {
        try {
            Cipher c = Cipher.getInstance("AES/CBC/PKCS7Padding");
            c.init(Cipher.DECRYPT_MODE, secretKey, iv);
            setDecryptedBytes(c.doFinal(arr));
        }
        catch (Exception e) {
            System.out.println("Error occurred " +
                    "while decrypting: " + e.toString());
        }
    }
    public static byte[] decryptb(byte[] arr){
        try {
            Cipher c = Cipher.getInstance("AES/CBC/PKCS7Padding");
            c.init(Cipher.DECRYPT_MODE, secretKey, iv);
            return c.doFinal(arr);
        }
        catch (Exception e) {
            System.out.println("Error occurred " +
                    "while decrypting: " + e.toString());
        }
        return new byte[0];
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
        AES.encrypt(strToEncrypt.trim());
        // debug with println
        System.out.println("String to Encrypt: " + strToEncrypt);
        System.out.println("Encrypted: " + java.util.Arrays.toString(AES.getEncryptedBytes()));
        String tmp = new String(AES.getEncryptedBytes(), StandardCharsets.ISO_8859_1);
        System.out.println("getEncryptedBytes: "+ tmp);
        //decrypting, and debugging
        AES.decrypt(tmp.getBytes(StandardCharsets.ISO_8859_1));

        byte[] d = AES.getDecryptedBytes();
        decoded = new String(d, StandardCharsets.UTF_8);
        System.out.println("Decrypted Bytes: " + decoded);

        //encrypting byte array
        AES.encrypt(byteArrToEncrypt);
        System.out.println("Byte array to encrypt: " + java.util.Arrays.toString(byteArrToEncrypt));
        System.out.println("Encrypted array: " + java.util.Arrays.toString(AES.getEncryptedBytes()));

        //decrypting byte array
        AES.decrypt(AES.getEncryptedBytes());
        System.out.println("Decrypted array: " + java.util.Arrays.toString(AES.getDecryptedBytes()));

    }
}