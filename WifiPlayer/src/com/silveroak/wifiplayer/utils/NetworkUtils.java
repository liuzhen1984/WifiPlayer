package com.silveroak.wifiplayer.utils;

import android.content.Context;
import com.silveroak.wifiplayer.constants.MusicType;
import com.silveroak.wifiplayer.domain.muisc.DownloadMusicFile;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by zliu on 14/10/30.
 */
public class NetworkUtils {
    private static final String TAG = NetworkUtils.class.getSimpleName();

    public static DownloadMusicFile downloadMusicFile(Context context,String urlPath) {
        OutputStream outputStream = null;
        InputStream inStream = null;
        try {
            //new一个URL对象
            URL url = new URL(urlPath);
            //打开链接
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            //设置请求方式为"GET"
            conn.setRequestMethod("GET");
            //超时响应时间为5秒
            conn.setConnectTimeout(5 * 1000);
            ;
            //通过输入流获取图片数据
            inStream = conn.getInputStream();

            String disposition =  conn.getHeaderField("Content-Disposition");
            String type = null;
            if(disposition==null){
                type = urlPath.substring(urlPath.lastIndexOf(".")+1,urlPath.length());
            }else{
                disposition = disposition.replaceAll("\"","");
                type = disposition.substring(disposition.lastIndexOf(".")+1,disposition.length());
            }
            MusicType musicType = null;
            try {
                musicType = MusicType.valueOf(type);
            }catch (Exception ex){}
            if(musicType==null){
                return null;
            }

            DownloadMusicFile downloadMusicFile = new DownloadMusicFile();
            downloadMusicFile.setType(musicType);
            String filename = System.currentTimeMillis() + "." + type;
            downloadMusicFile.setName(filename);
            downloadMusicFile.setPath(context.getExternalCacheDir() + File.separator + filename);
            File toTempMusic = new File(downloadMusicFile.getPath());
            if (toTempMusic.exists()) {
                toTempMusic.delete();
            }
            //得到图片的二进制数据，以二进制封装得到数据，具有通用性
            byte[] data = new byte[1024];
            int i = 0;
            outputStream = new FileOutputStream(toTempMusic);
            while ((i = inStream.read(data)) > 0) {
                outputStream.write(data, 0, i);
            }
            outputStream.flush();

            downloadMusicFile.setSize(conn.getContentLength());
            return downloadMusicFile;
        }catch (Exception ex){
            LogUtils.error(TAG,ex);
        } finally {
            if(outputStream!=null){
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(inStream!=null){
                try {
                    inStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }
}
