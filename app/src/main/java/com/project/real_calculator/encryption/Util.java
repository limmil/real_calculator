package com.project.real_calculator.encryption;

import android.app.ActivityManager;
import android.content.Context;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.MessageDigest;
import java.util.List;


public class Util
{
    private static final char[] hexDigits = "0123456789abcdef".toCharArray();
    private static final char[] chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();

    private Util(){}

    public static String makeRandomString(int length)
    {
        StringBuilder sb = new StringBuilder();
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < length; i++)
        {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }

        return sb.toString();
    }

    public static String makeHashSha(String password, String SHA)
    {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance(SHA);
            md.update(password.getBytes());
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }

        byte[] byteData = md.digest();

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++)
        {
            sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }

    public static String md5(InputStream is) {
        String md5 = "";

        try {
            byte[] bytes = new byte[4096];
            int read = 0;
            MessageDigest digest = MessageDigest.getInstance("MD5");

            while ((read = is.read(bytes)) != -1) {
                digest.update(bytes, 0, read);
            }

            byte[] messageDigest = digest.digest();

            StringBuilder sb = new StringBuilder(32);

            for (byte b : messageDigest) {
                sb.append(hexDigits[(b >> 4) & 0x0f]);
                sb.append(hexDigits[b & 0x0f]);
            }

            md5 = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return md5;
    }

    public static byte[] makeRandom12ByteNonce(){
        byte[] nonce = new byte[12];
        new SecureRandom().nextBytes(nonce);
        return nonce;
    }

    public static String makeHashSha256(String password)
    {
        return makeHashSha(password, "SHA-256");
    }

    public static String makeHashSha512(String password)
    {
        return makeHashSha(password, "SHA-512");
    }

    public static String makePasswordHash(String password){
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }

    public static boolean checkPassword(String password, String hash){
        return BCrypt.checkpw(password, hash);
    }

    public static void setMasterKey(String password, String iv, byte[] mkey){
        // decrypt master key
        AES.setKey(password);
        AES.setIV(iv);
        // setup master key and derive iv from master key
        String mk = new String(AES.decrypt(mkey), StandardCharsets.UTF_8);
        AES.setKey(mk);
        AES.setIV(Util.makeHashSha256(mk));
    }

    // assuming password and iv is not known yet
    public static byte[] encryptToByte(String password, String iv, String content){
        AES.setKey(password);
        AES.setIV(iv);
        return AES.encrypt(content.getBytes(StandardCharsets.UTF_8));
    }
    // assuming password and iv is already set
    public static byte[] encryptToByte(byte[] content){
        return AES.encrypt(content);
    }
    public static byte[] encryptToByte(String in){
        return encryptToByte(stringToByte(in));
    }
    // assuming password and iv is already set
    public static byte[] decryptToByte(byte[] content){
        return AES.decrypt(content);
    }

    public static String byteToString(byte[] content){
        return new String(content, StandardCharsets.UTF_8);
    }

    public static String decryptToString(byte[] content){
        return byteToString(decryptToByte(content));
    }

    public static byte[] stringToByte(String content){
        return content.getBytes(StandardCharsets.UTF_8);
    }




    public static void main(String[] args)
    {
        System.out.println(Util.makeRandomString(128));
        System.out.println(Util.makeHashSha256("123456"));
        System.out.println(Util.makeHashSha512("123456"));
    }


}