package com.echen.androidcommon.crypto;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by echen on 2016/6/8.
 */
public class MD5Utility {
    public static final String ALGORITHM = "MD5";
    public static String getMD5(String content)
    {
        String strMD5 = "";
        try {
            MessageDigest md5 = MessageDigest.getInstance(ALGORITHM);
            byte[] bytes = md5.digest(content.getBytes("utf-8"));
            strMD5 = getHashString(bytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return strMD5;
    }

    public static String getHashString(byte[] data)
    {
        StringBuilder builder = new StringBuilder();
        for (byte b : data) {
            builder.append(Integer.toHexString((b >> 4) & 0xf)); //0xf = 00001111, 取高四位
            builder.append(Integer.toHexString(b & 0xf)); //取低四位

        }
        return builder.toString().toLowerCase();
    }
}
