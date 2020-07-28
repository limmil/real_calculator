package com.project.real_calculator.encryption;

import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.security.MessageDigest;

public class Util
{
    public static String makeRandomString(int length)
    {
        char[] chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
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

        byte byteData[] = md.digest();

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

    public static void main(String[] args)
    {
        System.out.println(Util.makeRandomString(128));
        System.out.println(Util.makeHashSha256("123456"));
        System.out.println(Util.makeHashSha512("123456"));
    }


}
