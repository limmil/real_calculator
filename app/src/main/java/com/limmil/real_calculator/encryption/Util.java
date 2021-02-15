package com.limmil.real_calculator.encryption;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.MessageDigest;


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

    public static String makePasswordHash(String password, int strength){
        return BCrypt.hashpw(password, BCrypt.gensalt(strength));
    }

    public static boolean checkPassword(String password, String hash){
        return BCrypt.checkpw(password, hash);
    }

    public static void setMasterKey(String password, String iv, byte[] mkey){
        // decrypt master key
        AES.setKey(password);
        String newIv = makeHashSha256(iv + password);
        AES.setIV(newIv);
        // setup master key and derive iv from master key
        String mk = new String(AES.decrypt(mkey), StandardCharsets.UTF_8);
        AES.setKey(mk);

        //AES.setIV(Util.makeHashSha256(mk)); // doesn't do anything
    }
    public static String getMasterKey(String password, String iv, byte[] mkey){
        // decrypt master key
        AES.setKey(password);
        String newIv = makeHashSha256(iv + password);
        AES.setIV(newIv);
        // setup master key and derive iv from master key
        String mk = new String(AES.decrypt(mkey), StandardCharsets.UTF_8);
        AES.setKey(mk);

        return mk;
    }

    // assuming password and iv is not known yet
    public static byte[] encryptToByte(String password, String iv, String content){
        AES.setKey(password);
        String newIv = makeHashSha256(iv + password);
        AES.setIV(newIv);
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


    public static int calculatePasswordStrength(String password){

        int iPasswordScore = 0;

        if(password.length() < 6)
            return 0;
        else if(password.length() >= 10)
            iPasswordScore += 2;
        else
            iPasswordScore += 1;

        /*
         * if password contains 2 digits, add 2 to score.
         * if contains 1 digit add 1 to score
         */
        if(password.matches("(?=.*[0-9].*[0-9]).*"))
            iPasswordScore += 2;
        else if(password.matches("(?=.*[0-9]).*"))
            iPasswordScore += 1;

        //if password contains 1 lower case letter, add 2 to score
        if(password.matches("(?=.*[a-z]).*"))
            iPasswordScore += 2;

        /*
         * if password contains 2 upper case letters, add 2 to score.
         * if contains only 1 then add 1 to score.
         */
        if(password.matches("(?=.*[A-Z].*[A-Z]).*"))
            iPasswordScore += 2;
        else if(password.matches("(?=.*[A-Z]).*"))
            iPasswordScore += 1;

        /*
         * if password contains 2 special characters, add 2 to score.
         * if contains only 1 special character then add 1 to score.
         */
        if(password.matches("(?=.*[~!@#$%^&*()_-].*[~!@#$%^&*()_-]).*"))
            iPasswordScore += 2;
        else if(password.matches("(?=.*[~!@#$%^&*()_-]).*"))
            iPasswordScore += 1;


        return iPasswordScore;

    }



    public static void main(String[] args)
    {
        System.out.println(Util.makeRandomString(128));
        System.out.println(Util.makeHashSha256("123456"));
        System.out.println(Util.makeHashSha512("123456"));
    }


}