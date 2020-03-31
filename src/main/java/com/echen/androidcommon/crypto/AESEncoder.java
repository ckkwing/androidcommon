package com.echen.androidcommon.crypto;

import android.support.annotation.NonNull;
import com.echen.androidcommon.utility.FileUtility;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by echen on 3/31/2020
 */
public class AESEncoder {
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int KEY_SIZE = 128;
    private static final int LENGTH_KEY = 16;
    private static final int LENGTH_IV = 12;
    private static  byte[] CACHE = new byte[1024];

    private byte[] mKEY;
    private byte[] mIV;

    public AESEncoder(){
        SecureRandom secureRandom = new SecureRandom();
        mKEY = new byte[LENGTH_KEY];
        secureRandom.nextBytes(mKEY);

        mIV = new byte[LENGTH_IV]; //NEVER REUSE THIS IV WITH SAME KEY
        secureRandom.nextBytes(mIV);
    }

    private SecretKey generateSecretKey(){
        return new SecretKeySpec(mKEY, ALGORITHM);
    }

    public void setSeed(@NonNull byte[] key,@NonNull byte[] iv){
        if(key.length != LENGTH_KEY) { // check input parameter
            throw new IllegalArgumentException("invalid key length");
        }
        mKEY = key;

        if(iv.length < LENGTH_IV || iv.length >= LENGTH_KEY) { // check input parameter
            throw new IllegalArgumentException("invalid iv length");
        }
        mIV = iv;
    }

    private Cipher initAESCipher(int cipherMode) {
        Cipher cipher = null;
        try {
            SecretKey secretKey = generateSecretKey();
            cipher = Cipher.getInstance(TRANSFORMATION);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(KEY_SIZE, mIV); //128 bit auth tag length
            cipher.init(cipherMode, secretKey, parameterSpec);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return cipher;
    }

    public boolean encryptFile(@NonNull File sourceFile, @NonNull File targetFile) throws FileNotFoundException {
        if (!sourceFile.exists())
            throw new FileNotFoundException("Source file is not exist");
        if (!targetFile.exists()){
            if (!FileUtility.createFile(targetFile))
                throw new FileNotFoundException("Target file is not exist");
        }

        InputStream inputStream = new FileInputStream(sourceFile);
        OutputStream outputStream = new FileOutputStream(targetFile);
        boolean bRel = false;
        try{
            bRel = encrypt(inputStream, outputStream);
        }catch (Exception e){
            bRel = false;
        }
        finally {
            try{
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try{
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return bRel;
    }

    public boolean encrypt(@NonNull InputStream is, @NonNull OutputStream os) {
        Cipher cipher = initAESCipher(Cipher.ENCRYPT_MODE);
        CipherInputStream cipherInputStream = new CipherInputStream(is, cipher);
        int nRead = 0;
        try {
            while ((nRead = cipherInputStream.read(CACHE)) > 0) {
                os.write(CACHE, 0, nRead);
                os.flush();
            }
            cipherInputStream.close();
        }catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean decryptFile(@NonNull File sourceFile, @NonNull File targetFile) throws FileNotFoundException {
        if (!sourceFile.exists())
            throw new FileNotFoundException("Source file is not exist");
        if (!targetFile.exists()){
            if (!FileUtility.createFile(targetFile))
                throw new FileNotFoundException("Target file is not exist");
        }

        InputStream inputStream = new FileInputStream(sourceFile);
        OutputStream outputStream = new FileOutputStream(targetFile);
        boolean bRel = false;
        try{
            bRel = decrypt(inputStream, outputStream);
        }catch (Exception e){
            bRel = false;
        }
        finally {
            try{
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try{
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return bRel;
    }

    public boolean decrypt(@NonNull InputStream is, @NonNull OutputStream os) {
        Cipher cipher = initAESCipher(Cipher.DECRYPT_MODE);
        CipherOutputStream cipherOutputStream = new CipherOutputStream(os, cipher);
        try{
            int nRead = 0;
            while ((nRead = is.read(CACHE)) >= 0) {
                cipherOutputStream.write(CACHE, 0, nRead);
            }
            cipherOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
