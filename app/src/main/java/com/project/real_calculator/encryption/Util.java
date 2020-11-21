package com.project.real_calculator.encryption;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.MessageDigest;


public class Util
{
    private Util(){}

    public static String makeRandomString(int length)
    {
        char[] chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
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

    public static byte[] encryptToByte(String password, String iv, byte[] content){
        AES.setKey(password);
        AES.setIV(iv);
        AES.encrypt(content);
        return AES.getEncryptedBytes();
    }
    public static byte[] encryptToByte(String password, String iv, String content){
        AES.setKey(password);
        AES.setIV(iv);
        AES.encrypt(content);
        return AES.getEncryptedBytes();
    }

    public static byte[] decryptToByte(String password, String iv, byte[] content){
        AES.setKey(password);
        AES.setIV(iv);
        AES.encrypt(content);
        return AES.getDecryptedBytes();
    }
    public static String decryptToString(String password, String iv, byte[] content){
        AES.setKey(password);
        AES.setIV(iv);
        AES.encrypt(content);
        return new String(AES.getDecryptedBytes(), StandardCharsets.UTF_8);
    }

    public static void main(String[] args)
    {
        System.out.println(Util.makeRandomString(128));
        System.out.println(Util.makeHashSha256("123456"));
        System.out.println(Util.makeHashSha512("123456"));
    }


}