package com.silveroak.playerclient.service.business;

import android.content.Context;
import android.util.Xml;
import com.silveroak.playerclient.domain.MusicObj;
import com.silveroak.playerclient.utils.LogUtils;
import com.silveroak.playerclient.utils.NetworkUtils;
import org.xmlpull.v1.XmlPullParser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zliu on 15/2/9.
 */
public class SearchMusic {
    private final static String TAG = SearchMusic.class.getSimpleName();

    private final String BAIDU_API = "http://box.zhangmen.baidu.com/x?op=12&count=1&title=";

    private static Context context = null;

    private static SearchMusic searchMusic = null;
    private SearchMusic(Context context) {
        this.context = context;

    }
    public static SearchMusic getSearchMusic(Context context){
        if(searchMusic==null){
            searchMusic = new SearchMusic(context);
        }
        return searchMusic;
    }

    public String getUrl(String musicName){

        String url = null;
        try {
            musicName = URLEncoder.encode(musicName, "utf8");
            url = BAIDU_API + musicName + "$$";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            LogUtils.error(TAG,e);
            return null;
        }
        String musicXml = NetworkUtils.requestUrlGet(url);
        if(musicXml==null){
            return null;
        }
        LogUtils.debug(TAG,musicXml);
        List<MusicObj> musicObjList = paramsResult(musicXml);
        LogUtils.debug(TAG,musicObjList.toString());
        if(musicObjList.size()>0){
           return musicObjList.get(0).getUrl();
        }
        return null;
    }

    private List<MusicObj> paramsResult(String musicXml){
        InputStream in = new ByteArrayInputStream(musicXml.getBytes());
        List<MusicObj> musicObjList = new ArrayList<MusicObj>();
        XmlPullParser parser = Xml.newPullParser();
        MusicObj music = null;
        try {
            parser.setInput(in, "utf-8");
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if (parser.getName().equals("durl")) {
                            music = new MusicObj();
                            music.setUrl("");
                        } else if (parser.getName().equals("encode")) {
                            if(music!=null) {
                                music.setUrl(parser.nextText() + music.getUrl());
                            }
                        } else if (parser.getName().equals("decode")) {
                            if(music!=null) {
                                music.setUrl(music.getUrl() + parser.nextText());
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (parser.getName().equals("durl")) {
                            if(music!=null && !"".equals(music.getUrl())) {
                                musicObjList.add(music);
                            }
                            music = null;
                        }
                        break;
                }
                eventType = parser.next();
            }
        }catch(Exception ex){
            LogUtils.error(TAG, ex);
        }
        return musicObjList;
    }
}
