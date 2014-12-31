package com.silveroak.wifiplayer.utils;

import android.util.Log;
import com.silveroak.wifiplayer.constants.CompileParams;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

//import com.emoney.service.YMDataService;

/**
 * ===========================================
 * modify date:   25 5 2014
 * modify by: haoquanqing
 * description: 日志信息类
 * ===========================================
 */
public class LogUtils {
    public static final boolean isNeedDataFromService = true;

    public static void debug(String Tag, String info) {
        if (info == null) {
            info = "null";
        }
        if (CompileParams.IS_DEBUG){
            Log.d(Tag, info);
        }
    }
    public static void warn(String Tag, String info) {
        if (info == null) {
            info = "null";
        }
        if (CompileParams.TO_WARN){
            Log.w(Tag, info);
        }
    }

    public static void info(String Tag, String strinfo) {
        if (strinfo == null) {
            strinfo = "null";
        }
        if (CompileParams.TO_INFO){
            Log.i(Tag, strinfo);

        }
    }

    public static void error(String Tag, String strinfo) {
        if (strinfo == null) {
            strinfo = "null";
        }
        if (CompileParams.TO_ERROR) {
            Log.e(Tag, "Error:"+strinfo);

        }
    }

    public static void verbose(String Tag, String strinfo) {
        if (strinfo == null) {
            strinfo = "null";
        }
        if (CompileParams.TO_VERBOSE){
            Log.v(Tag, strinfo);

        }
    }
    public static void userOperat(String Tag,String strinfo){
        if (strinfo == null) {
            strinfo = "null";
        }
        if (CompileParams.TO_OPERAT) {
            Log.v(Tag, strinfo);
        }
    }

    public static void error(String tag,Throwable throwable){
        if(throwable==null){
            return;
        }
        if(CompileParams.TO_ERROR){
            Log.e(tag,exceptionToString(throwable));
        }
    }

    public static String exceptionToString(Throwable throwable) {
        StringBuffer sb = new StringBuffer();
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        throwable.printStackTrace(printWriter);
        Throwable cause = throwable.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        sb.append(result);
        return sb.toString();
    }
}
