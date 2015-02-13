package com.jacp.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Utils
{

    /**
     * 加载图片
     * @param url 图片的url
     * @param listener 回调监听器
     */
    public static void loadImage(final String url, final OnLoadImageListener listener)
    {
        if (StringUtils.isEmpty(url) || null == listener)
        {
            return;
        }

        final Handler handler = new Handler()
        {
            public void handleMessage(Message msg)
            {
                listener.onLoadImage((Bitmap) msg.obj, url);
            }
        };

        // 之前根据url写入本地缓存的路径

        String path = "covers/" + url.substring(url.lastIndexOf("/"));
        File file = new File(path);
        if (file.exists())
        {
            Bitmap bm = BitmapFactory.decodeFile(path);
            sendMessage(handler, bm);
            return;
        }

        new Thread(new Runnable()
        {
            public void run()
            {
                try
                {
                    // 网络加载图片，还可以加入延迟(time out)条件
                    URL u = new URL(url);
                    HttpURLConnection httpConnection = (HttpURLConnection) u.openConnection();
                    if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK)
                    {
                        Bitmap bm = BitmapFactory.decodeStream(httpConnection.getInputStream());
                        sendMessage(handler, bm);
                        // 同时对图片进行缓存...
                        return;
                    }

                    // 没有请求到图片
                    sendMessage(handler, null);
                } catch (MalformedURLException e)
                {
                    sendMessage(handler, null);
                } catch (IOException e)
                {
                    sendMessage(handler, null);
                }
            }
        }).start();

    }

    /**
     * 向handler发送处理的消息
     * @param handler
     * @param bm
     */
    private static void sendMessage(Handler handler, Bitmap bm)
    {
        Message msg = handler.obtainMessage();
        msg.obj = bm;
        handler.sendMessage(msg);
    }

    /**
     * 加载图片时的回调
     *
     */
    public interface OnLoadImageListener
    {
        public void onLoadImage(Bitmap bm, String imageUrl);
    }
}
