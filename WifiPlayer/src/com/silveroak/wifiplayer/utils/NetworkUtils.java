package com.silveroak.wifiplayer.utils;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import com.silveroak.wifiplayer.constants.MusicType;
import com.silveroak.wifiplayer.constants.WifiKeyMgmtEnum;
import com.silveroak.wifiplayer.domain.muisc.Music;
import com.silveroak.wifiplayer.service.parser.Mp3ParserService;
import com.silveroak.wifiplayer.service.parser.Mp3ReadId3v2;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.List;

/**
 * Created by zliu on 14/10/30.
 */
public class NetworkUtils {
    private static final String TAG = NetworkUtils.class.getSimpleName();

    public static Music downloadMusicFile(Context context,String urlPath) {
        InputStream inStream = null;
        Music music = new Music();
        try {
            //new一个URL对象
            URL url = new URL(urlPath);
            //打开链接
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn .setRequestProperty("Accept-Encoding", "gzip, deflate, sdch");
            conn .setRequestProperty("Accept-Language", "en,zh-CN;q=0.8,zh;q=0.6");
            conn.setRequestProperty("User-Agent", " Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2062.120 Safari/537.36");

            //设置请求方式为"GET"
            conn.setRequestMethod("GET");
            //超时响应时间为5秒
            conn.setConnectTimeout(5 * 1000);
            ;
            //通过输入流获取图片数据
            inStream = conn.getInputStream();

            String disposition =  conn.getHeaderField("Content-Disposition");
            String type = null;
            String musicName = null;
            if(disposition==null){
                type = urlPath.substring(urlPath.lastIndexOf(".") + 1, urlPath.length());
            }else{
                disposition= URLDecoder.decode(disposition);
                disposition = disposition.replaceAll("\"","");
                type = disposition.substring(disposition.lastIndexOf(".")+1,disposition.length());
                String[] names =disposition.split("=");
                if(names.length>1){
                    musicName = names[1];
                }else{
                    musicName = disposition;
                }

            }
            MusicType musicType = null;
            try {
                musicType = MusicType.valueOf(type.toUpperCase());
            }catch (Exception ex){}
            if(musicType==null){
                return null;
            }

            //得到图片的二进制数据，以二进制封装得到数据，具有通用性
            byte[] data = readInputStream(inStream);

            if(musicType.equals(MusicType.MP3)) {
                if (Mp3ParserService.getService().checkID3V1(ByteUtils.subByte(data, 0, 128))) {
                    music = Mp3ParserService.getService().parseID3V1(ByteUtils.subByte(data, 0, 128));
                } else {
                    Mp3ReadId3v2 mp3ReadId3v2 =new Mp3ReadId3v2(data);
                    music = mp3ReadId3v2.readId3v2();
                }
            }
            return music;
        }catch (Exception ex){
            LogUtils.error(TAG,ex);
        } finally {
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

    private static byte[] readInputStream(InputStream inStream) throws Exception{
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        //创建一个Buffer字符串
        byte[] buffer = new byte[1024];
        //每次读取的字符串长度，如果为-1，代表全部读取完毕
        int len = 0;
        //使用一个输入流从buffer里把数据读取出来
        try {
            while ((len = inStream.read(buffer)) != -1) {
                //用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
                outStream.write(buffer, 0, len);
            }
        }catch (EOFException eof){
            LogUtils.error(TAG,eof);
        }
        //关闭输入流
        inStream.close();
        //把outStream里的数据写入内存
        return outStream.toByteArray();
    }


    public static WifiConfiguration createWifiInfo(WifiManager wifiManager,String SSID, String Password, WifiKeyMgmtEnum type) {
        if(wifiManager==null){
            return null;
        }
        if(SSID==null||"".equals(SSID)){
            return null;
        }
        if(Password==null){
            Password = "";
        }
        if(type==null){
            type=WifiKeyMgmtEnum.WPA;
        }
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";

        WifiConfiguration tempConfig = IsExsits(wifiManager,SSID);
        if (tempConfig != null) {
            wifiManager.removeNetwork(tempConfig.networkId);
        }
// 没有密码：
        if (type.equals(WifiKeyMgmtEnum.NONE)) {
            config.wepKeys[0] = "";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
// WEP方式加密
        if (type.equals(WifiKeyMgmtEnum.WEP)) {
            config.hiddenSSID = true;
            config.wepKeys[0] = "\"" + Password + "\"";
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
// WPA方式加密
        if (type.equals(WifiKeyMgmtEnum.WPA)) {
            config.preSharedKey = "\"" + Password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        return config;
    }

    public static WifiConfiguration IsExsits(WifiManager wifiManager,String SSID) {
        List<WifiConfiguration> existingConfigs = wifiManager.getConfiguredNetworks();
        if(existingConfigs==null){
            return null;
        }
        for (WifiConfiguration existingConfig : existingConfigs) {
            if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                return existingConfig;
            }
        }
        return null;
    }
}
