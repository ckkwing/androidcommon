package com.echen.androidcommon.HTTP;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Created by echen on 2016/8/11.
 */
public class URLHelper {
    public static String encodeUrl(String urlToEncode) throws UnsupportedEncodingException {
        return URLEncoder.encode(urlToEncode, "UTF-8");
    }

    public static String decodeUrl(String urlToDecode) throws UnsupportedEncodingException {
        return URLDecoder.decode(urlToDecode, "UTF-8");
    }
}
