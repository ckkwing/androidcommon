package com.echen.androidcommon.http;

import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import com.echen.androidcommon.utility.FileUtility;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by echen on 2016/7/18.
 */
public class OkHttpClientManager {
    private OkHttpClient m_OkHttpClient;
    private Handler m_delivery;
    private Gson m_gson;

    private volatile static OkHttpClientManager instance;

    public static OkHttpClientManager getInstance() {
        if (null == instance) {
            synchronized (OkHttpClientManager.class) {
                if (null == instance) {
                    instance = new OkHttpClientManager();
                }
            }
        }
        return instance;
    }

    private OkHttpClientManager() {
        m_OkHttpClient = new OkHttpClient();
        //cookie enabled
//        m_OkHttpClient.setCookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ORIGINAL_SERVER));
        m_delivery = new Handler(Looper.getMainLooper());
        m_gson = new Gson();
    }

    //*************Public methods************

    public static Response getSync(String url) throws IOException
    {
        return getInstance().getSync(url);
    }


    public static String getAsString(String url) throws IOException
    {
        return getInstance()._getAsString(url);
    }

    public static void getAsync(String url, ResultCallback callback)
    {
        getInstance()._getAsync(url, callback);
    }

    public static Response post(String url, Param... params) throws IOException
    {
        return getInstance()._postSync(url, params);
    }

    public static String postAsString(String url, Param... params) throws IOException
    {
        return getInstance()._postAsString(url, params);
    }

    public static void postAsync(String url, final ResultCallback callback, Param... params)
    {
        getInstance()._postAsync(url, callback, params);
    }


    public static void postAsync(String url, final ResultCallback callback, Map<String, String> params)
    {
        getInstance()._postAsync(url, callback, params);
    }


    public static Response post(String url, File[] files, String[] fileKeys, Param... params) throws IOException
    {
        return getInstance()._post(url, files, fileKeys, params);
    }

    public static Response post(String url, File file, String fileKey) throws IOException
    {
        return getInstance()._post(url, file, fileKey);
    }

    public static Response post(String url, File file, String fileKey, Param... params) throws IOException
    {
        return getInstance()._post(url, file, fileKey, params);
    }

    public static void postAsync(String url, ResultCallback callback, File[] files, String[] fileKeys, Param... params) throws IOException
    {
        getInstance()._postAsync(url, callback, files, fileKeys, params);
    }


    public static void postAsync(String url, ResultCallback callback, File file, String fileKey) throws IOException
    {
        getInstance()._postAsync(url, callback, file, fileKey);
    }


    public static void postAsync(String url, ResultCallback callback, File file, String fileKey, Param... params) throws IOException
    {
        getInstance()._postAsync(url, callback, file, fileKey, params);
    }

    public static void displayImage(final ImageView view, String url, int errorResId) throws IOException
    {
        getInstance()._displayImage(view, url, errorResId);
    }


    public static void displayImage(final ImageView view, String url)
    {
        getInstance()._displayImage(view, url, -1);
    }

    public static boolean download(String url, String destDir, String postfix) throws IOException {
        return getInstance()._download(url, destDir, postfix);
    }

    public static void downloadAsync(String url, String destDir, String postfix, ResultCallback callback)
    {
        getInstance()._downloadAsync(url, destDir, postfix, callback);
    }

    //****************************

    /*
     * 同步的Get请求
     * @param url
     * @return Response
     */
    private Response _getSync(String url) throws IOException {
        final Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = m_OkHttpClient.newCall(request);
        Response execute = call.execute();
        return execute;
    }

    /*
     * 同步的Get请求
     * @param url
     * @return 字符串
     */
    private String _getAsString(String url) throws IOException {
        Response response = _getSync(url);
        return response.body().string();
    }

    /*
     * 异步的get请求
     * @param url
     * @param resultCallback
     */
    private void _getAsync(String url, final ResultCallback resultCallback) {
        final Request request = new Request.Builder().url(url).build();
        deliveryResult(resultCallback, request);
    }

    /*
     * 同步的Post请求
     * @param url
     * @param params post的参数
     * @return
     */
    private Response _postSync(String url, Param... params) throws IOException {
        Request request = buildPostRequest(url, params);
        Response response = m_OkHttpClient.newCall(request).execute();
        return response;
    }

    /*
     * 同步的Post请求
     * @param url
     * @param params post的参数
     * @return 字符串
     */
    private String _postAsString(String url, Param... params) throws IOException {
        Response response = _postSync(url, params);
        return response.body().string();
    }

    /*
     * 异步的post请求
     * @param url
     * @param resultCallback
     * @param params
     */
    private void _postAsync(String url, final ResultCallback resultCallback, Param... params) {
        Request request = buildPostRequest(url, params);
        deliveryResult(resultCallback, request);
    }

    /*
     * 异步的post请求
     * @param url
     * @param resultCallback
     * @param params
     */
    private void _postAsync(String url, final ResultCallback resultCallback, Map<String, String> params) {
        Param[] paramsArr = map2Params(params);
        Request request = buildPostRequest(url, paramsArr);
        deliveryResult(resultCallback, request);
    }

    /*
     * 同步基于post的文件上传
     * @param params
     * @return
     */
    private Response _post(String url, File[] files, String[] fileKeys, Param... params) throws IOException {
        Request request = buildMultipartFormRequest(url, files, fileKeys, params);
        return m_OkHttpClient.newCall(request).execute();
    }

    private Response _post(String url, File file, String fileKey) throws IOException {
        Request request = buildMultipartFormRequest(url, new File[]{file}, new String[]{fileKey}, null);
        return m_OkHttpClient.newCall(request).execute();
    }

    private Response _post(String url, File file, String fileKey, Param... params) throws IOException {
        Request request = buildMultipartFormRequest(url, new File[]{file}, new String[]{fileKey}, params);
        return m_OkHttpClient.newCall(request).execute();
    }

    /*
     * 异步基于post的文件上传
     * @param url
     * @param resultCallback
     * @param files
     * @param fileKeys
     * @throws IOException
     */
    private void _postAsync(String url, ResultCallback resultCallback, File[] files, String[] fileKeys, Param... params) throws IOException {
        Request request = buildMultipartFormRequest(url, files, fileKeys, params);
        deliveryResult(resultCallback, request);
    }

    /*
     * 异步基于post的文件上传，单文件不带参数上传
     * @param url
     * @param resultCallback
     * @param file
     * @param fileKey
     * @throws IOException
     */
    private void _postAsync(String url, ResultCallback resultCallback, File file, String fileKey) throws IOException {
        Request request = buildMultipartFormRequest(url, new File[]{file}, new String[]{fileKey}, null);
        deliveryResult(resultCallback, request);
    }

    /*
     * 异步基于post的文件上传，单文件且携带其他form参数上传
     * @param url
     * @param resultCallback
     * @param file
     * @param fileKey
     * @param params
     * @throws IOException
     */
    private void _postAsync(String url, ResultCallback resultCallback, File file, String fileKey, Param... params) throws IOException {
        Request request = buildMultipartFormRequest(url, new File[]{file}, new String[]{fileKey}, params);
        deliveryResult(resultCallback, request);
    }

    private Request buildMultipartFormRequest(String url, File[] files, String[] fileKeys, Param[] params) {
        params = validateParam(params);

        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        for (Param param : params) {
            builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + param.key + "\""),
                    RequestBody.create(null, param.value));
        }

        if (null != files) {
            RequestBody fileBody = null;
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                String fileName = file.getName();
                fileBody = RequestBody.create(MediaType.parse(FileUtility.guessMimeType(fileName)), file);
                //TODO set contentType according to file name
                builder.addPart(Headers.of("Content-Disposition",
                        "form-data; name=\"" + fileKeys[i] + "\"; filename=\"" + fileName + "\""),
                        fileBody);
            }
        }

        RequestBody requestBody = builder.build();
        return new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
    }

    private boolean _download(final String url, final String destFileDir, String postfix) throws IOException {
        boolean bRel = true;
        final Request request = new Request.Builder().url(url).build();
        final Call call = m_OkHttpClient.newCall(request);
        Response response = call.execute();
        if (null == response)
            bRel = false;
        else {
            InputStream inputStream = null;
            byte[] buffer = new byte[2048];
            int len = 0;
            FileOutputStream fileOutputStream = null;
            try {
                inputStream = response.body().byteStream();
                String name = FileUtility.getFileName(url);
                if (null != postfix)
                {
                    name += postfix;
                }
                File file = new File(destFileDir, name);
                fileOutputStream = new FileOutputStream(file);
                while ((len = inputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, len);
                }
                fileOutputStream.flush();
            } catch (IOException e) {
                bRel = false;
            } finally {
                try {
                    if (inputStream != null)
                        inputStream.close();
                } catch (IOException e) {
                }
                try {
                    if (fileOutputStream != null)
                        fileOutputStream.close();
                } catch (IOException e) {
                }
            }
        }
        return bRel;
    }

    /*
     * 异步下载文件
     * @param url
     * @param destFileDir 本地文件存储的文件夹
     * @param resultCallback
     */
    private void _downloadAsync(final String url, final String destFileDir, final String postfix, final ResultCallback resultCallback) {
        final Request request = new Request.Builder().url(url).build();
        final Call call = m_OkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                sendFailedResultCallback(request, e, resultCallback);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream inputStream = null;
                byte[] buffer = new byte[2048];
                int len = 0;
                FileOutputStream fileOutputStream = null;
                try {
                    inputStream = response.body().byteStream();
                    String name = FileUtility.getFileName(url);
                    if (null != postfix)
                    {
                        name += postfix;
                    }
                    File file = new File(destFileDir, name);
                    fileOutputStream = new FileOutputStream(file);
                    while ((len = inputStream.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, len);
                    }
                    fileOutputStream.flush();

                    sendSuccessResultCallback(file.getAbsolutePath(), resultCallback);
                } catch (IOException e) {
                    sendFailedResultCallback(response.request(), e, resultCallback);
                } finally {
                    try {
                        if (inputStream != null)
                            inputStream.close();
                    } catch (IOException e) {
                    }
                    try {
                        if (fileOutputStream != null)
                            fileOutputStream.close();
                    } catch (IOException e) {
                    }
                }
            }
        });
    }

    private void _displayImage(final ImageView view, final String url, final int errorResId){
        final Request request = new Request.Builder().url(url).build();
        Call call = m_OkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                setErrorResId(view, errorResId);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream inputStream = null;
                try
                {
                    inputStream = response.body().byteStream();

                }
                catch (Exception e){}

            }
        });
    }


    private void setErrorResId(final ImageView view, final int errorResId)
    {
        m_delivery.post(new Runnable()
        {
            @Override
            public void run()
            {
                view.setImageResource(errorResId);
            }
        });
    }

    private Param[] validateParam(Param[] params) {
        if (params == null)
            return new Param[0];
        else return params;
    }

    private Param[] map2Params(Map<String, String> params) {
        if (params == null) return new Param[0];
        int size = params.size();
        Param[] res = new Param[size];
        Set<Map.Entry<String, String>> entries = params.entrySet();
        int i = 0;
        for (Map.Entry<String, String> entry : entries) {
            res[i++] = new Param(entry.getKey(), entry.getValue());
        }
        return res;
    }

    private Request buildPostRequest(String url, Param[] params) {
        if (null == params) {
            params = new Param[0];
        }
        FormBody.Builder builder = new FormBody.Builder();
        for (Param param : params) {
            builder.add(param.key, param.value);
        }
        RequestBody requestBody = builder.build();
        return new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
    }

    private void sendFailedResultCallback(final Request request, final Exception e, final ResultCallback resultCallback) {
        m_delivery.post(new Runnable() {
            @Override
            public void run() {
                if (null != resultCallback)
                    resultCallback.onError(request, e);
            }
        });
    }

    private void sendSuccessResultCallback(final Object object, final ResultCallback resultCallback) {
        m_delivery.post(new Runnable() {
            @Override
            public void run() {
                if (null != resultCallback)
                    resultCallback.onResponse(object);
            }
        });
    }

    private void deliveryResult(final ResultCallback resultCallback, final Request request) {
        m_OkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                sendFailedResultCallback(request, e, resultCallback);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final String strResult = response.body().string();
                    if (resultCallback.type == String.class) {
                        sendSuccessResultCallback(strResult, resultCallback);
                    } else {
                        Object object = m_gson.fromJson(strResult, resultCallback.type);
                    }
                } catch (IOException e) {
                    sendFailedResultCallback(response.request(), e, resultCallback);
                } catch (JsonParseException e) {
                    sendFailedResultCallback(response.request(), e, resultCallback);
                }
            }
        });
    }

}
