package com.silveroak.playerclient.utils;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/*
 *
 * @author starry
 *
 */

@SuppressWarnings("restriction")
public class EncryptUtil {

    public static String urlEncode(String src) {
        try {
            return new BASE64Encoder().encodeBuffer(src.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return "";
        }
    }

    public static String urlDecode(String src) {
        try {
            byte[] encrypted = new BASE64Decoder().decodeBuffer(src);
            return new String(encrypted,"utf-8");
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return "";
        }

    }
}