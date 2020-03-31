package com.echen.androidcommon.utility;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.widget.Toast;

import com.echen.androidcommon.R;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.Objects;
import io.milton.common.ContentTypeUtils;
import wseemann.media.FFmpegMediaMetadataRetriever;

/**
 * Created by echen on 2015/2/15.
 */
public class FileUtility {
    private static Logger log = LoggerFactory.getLogger(FileUtility.class);

    public static boolean isExist(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    public static boolean copyFile(File fromFile, File toFile) {
        boolean bRel = true;
        try {
            FileInputStream inputStream = new FileInputStream(fromFile);
            FileOutputStream outputStream = new FileOutputStream(toFile);
            byte bt[] = new byte[1024];
            int c;
            while ((c = inputStream.read(bt)) > 0) {
                outputStream.write(bt, 0, c); //Write content into new file
            }
            inputStream.close();
            outputStream.close();
        } catch (FileNotFoundException e) {
            bRel = false;
            e.printStackTrace();
        } catch (IOException e) {
            bRel = false;
            e.printStackTrace();
        }
        return bRel;
    }

    public static boolean createFile(File file){
        boolean bRel = false;
        try{
            if(file.getParentFile().exists()){
                log.debug("----- Create file" + file.getAbsolutePath());
                bRel = file.createNewFile();
            }
            else {
                if (createDir(file.getParentFile().getAbsolutePath())) {
                    bRel = file.createNewFile();
                    log.debug("----- Create file" + file.getAbsolutePath());
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return bRel;
    }

    public static boolean createDir(String dirPath){
        boolean bRel = false;
        try{
            File file=new File(dirPath);
            if(file.getParentFile().exists()){
                log.debug("----- Create folder" + file.getAbsolutePath());
                bRel =file.mkdir();
            }
            else {
                createDir(file.getParentFile().getAbsolutePath());
                log.debug("----- Create folder" + file.getAbsolutePath());
                bRel = file.mkdir();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return bRel;
    }


    public static File createNewFile(String directory, String fileName) {
        File newFile = null;
        File tmpDirFile = new File(directory);
        if (tmpDirFile == null) {
            String tmpDir = System.getProperty("java.io.tmpdir", ".");
            tmpDirFile = new File(tmpDir);
        }
        try {
            do {
                newFile = new File(tmpDirFile, fileName);
                if (newFile.exists())
                    newFile.delete();
            } while (!newFile.createNewFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return newFile;
    }

    public static void deleteFile(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }
        if (file.isDirectory()) {
            File[] childFile = file.listFiles();
            if (childFile == null || childFile.length == 0) {
                file.delete();
                return;
            }
            for (File f : childFile) {
                deleteFile(f);
            }
            file.delete();
        }
    }

    public static StringBuffer readFromFile(String filePath) {
        FileInputStream inputStream = null;
        StringBuffer strBuffer = new StringBuffer();
        try {
            inputStream = new FileInputStream(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String line = null;
        BufferedReader bufferReader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            while ((line = bufferReader.readLine()) != null) {
                strBuffer.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return strBuffer;
    }

    public static boolean writeToFile(String strData, String filePath) {
        boolean bRel = true;
        try {
            BufferedReader reader = new BufferedReader(new StringReader(strData));
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
            int len = 0;
            char[] buffer = new char[1024];
            while ((len = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, len);
            }
            writer.flush();
            writer.close();
            reader.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            bRel = false;
        }
        return bRel;
    }

    public static String guessMimeType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = fileNameMap.getContentTypeFor(path);
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }

    public static String getFileName(String path) {
        int separatorIndex = path.lastIndexOf("/");
        return (separatorIndex < 0) ? path : path.substring(separatorIndex + 1, path.length());
    }

    public static File getInternalStorageFile() {
        return Environment.isExternalStorageEmulated() && !Environment.isExternalStorageRemovable() ? Environment.getExternalStorageDirectory() : null;
    }

    public static File getSDCardFile(Context context) {
        File storageDirectory = Environment.getExternalStorageDirectory();
        if (Environment.isExternalStorageRemovable()) {
            if (Environment.isExternalStorageEmulated()) {
                return storageDirectory;
            } else {
                return null;
            }
        }

        File[] externalFilesDirs = context.getExternalFilesDirs(null);
        String storagePath = storageDirectory.getAbsolutePath();
        for (File filesDir : externalFilesDirs) {
            if (null != filesDir) {
                String filesPath = filesDir.getAbsolutePath();
                if (!filesPath.startsWith(storagePath)) {
                    int endIndex = filesPath.indexOf("/Android");
                    return new File(filesPath.substring(0, endIndex));
                }
            }
        }
        return null;
    }

    public static Bitmap createAudioThumbnail(String filePath) {
        Bitmap bitmap = null;
        File file = new File(filePath);
        if (!file.exists())
            return null;
        FFmpegMediaMetadataRetriever retriever = new FFmpegMediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            retriever.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ALBUM);
            retriever.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ARTIST);
            byte[] art = retriever.getEmbeddedPicture();
            bitmap = BitmapFactory.decodeByteArray(art, 0, art.length);
        } catch (IllegalArgumentException ex) {
        } catch (RuntimeException ex) {
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException ex) {
                // Ignore failures while cleaning up.
            }
        }
        return bitmap;
    }

    public static Bitmap createVideoThumbnail(String filePath, long usFrameAtTime) {
        Bitmap bitmap = null;
        File file = new File(filePath);
        if (!file.exists())
            return null;
        FFmpegMediaMetadataRetriever retriever = new FFmpegMediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            retriever.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ALBUM);
            retriever.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ARTIST);
            bitmap = retriever.getFrameAtTime(usFrameAtTime, FFmpegMediaMetadataRetriever.OPTION_CLOSEST);
        } catch (IllegalArgumentException ex) {
        } catch (RuntimeException ex) {
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException ex) {
                // Ignore failures while cleaning up.
            }
        }
        return bitmap;
    }

    public static void openFileByDefaultApp(Context context, String filePath, String packageName){
        if (null == context || TextUtils.isEmpty(filePath) || TextUtils.isEmpty(packageName))
            return;
        try {
            File file = new File(filePath);
            if (!file.exists())
                return;
            Intent intent = new Intent(Intent.ACTION_VIEW);
            //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //String type = ContentTypeUtils.findContentTypes(file);
            String type = guessMimeType(filePath);
            Uri uri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                uri = FileProvider.getUriForFile(context, packageName + ".fileprovider", file);
            } else {
                uri = Uri.fromFile(file);
            }
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            String[] typeArray = type.split(",");
            String convertedType = "";
            if (typeArray.length > 0)
            {
                convertedType = typeArray[0];
            }
            intent.setDataAndType(uri, convertedType);
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(Intent.createChooser(intent, context.getString(R.string.common_select_application_to_open)));
            }
            else {
                Objects.requireNonNull(log).warn("Cannot open file \'%s\' by default player", filePath);
                Toast.makeText(context, R.string.common_msg_install_default_player, Toast.LENGTH_LONG).show();
            }
        }
        catch (Exception e)
        {
            Objects.requireNonNull(log).error("Open file \'%s\' exception: ", e.getCause());
        }
    }
}
