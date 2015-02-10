package com.silveroak.playerclient.service.business;

import android.content.Context;
import com.silveroak.playerclient.domain.Music;
import com.silveroak.playerclient.domain.MusicObj;
import com.silveroak.playerclient.utils.JsonUtils;
import com.silveroak.playerclient.utils.LogUtils;
import com.silveroak.playerclient.utils.NetworkUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zliu on 15/2/9.
 */
public class SearchMusic {
    private final static String TAG = SearchMusic.class.getSimpleName();

    private final String BAIDU_SERRCH_API = "http://tingapi.ting.baidu.com/v1/restserver/ting?from=webapp_music&method=baidu.ting.search.catalogSug&format=json&callback=&query=";
    private final String BAIDU_GET_API = "http://ting.baidu.com/data/music/links?songIds=";

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

    public Music getMusic(String musicName){
        List<MusicObj> musicObjList = search(musicName);
        if(musicObjList!=null && musicObjList.size()>0){
            return get(musicObjList.get(0).getId());
        }
        return null;
    }

    public Music get(String id){
        if(id==null){
            return null;
        }
        String musicJson = NetworkUtils.requestUrlGet(BAIDU_GET_API+id);
        Map<String,Object> resultMap = JsonUtils.string2Map(musicJson);
        if(resultMap!=null && resultMap.containsKey("data")){
            Map<String,Object> value = (Map<String, Object>) resultMap.get("data");
            if(value!=null && value.containsKey("songList")){
                 ArrayList<Object> list = (ArrayList<Object>) value.get("songList");
                 value = (Map<String, Object>) list.get(0);
                  Music music = new Music();
                music.setSongName(String.valueOf(value.get("songName")));
                music.setArtistName(String.valueOf(value.get("artistName")));
                music.setAlbumName(String.valueOf(value.get("albumName")));
                music.setSongPicSmall(String.valueOf(value.get("songPicSmall")));
                music.setSongPicBig(String.valueOf(value.get("songPicBig")));
                music.setSongPicRadio(String.valueOf(value.get("songPicRadio")));
                music.setLrcLink(String.valueOf(value.get("lrcLink")));
                music.setSongLink(String.valueOf(value.get("songLink")));
                music.setFormat(String.valueOf(value.get("format")));
                if(value.containsKey("rate")){
                    if( !String.valueOf(value.get("rate")).equals("")){
                        music.setRate(Integer.valueOf(String.valueOf(value.get("rate"))));
                    }
                }
                if(value.containsKey("size")){
                    if( !String.valueOf(value.get("size")).equals("")){
                        music.setSize(Integer.valueOf(String.valueOf(value.get("size"))));
                    }
                }
                music.setResourceType(String.valueOf(value.get("resourceType")));
                music.setRelateStatus(String.valueOf(value.get("relateStatus")));
                return  music;
            }
        }
        return null;

    }

    public List<MusicObj> search(String musicName){
        List<MusicObj> musicObjList = new ArrayList<MusicObj>();
        try {
            musicName = URLEncoder.encode(musicName, "utf8");
            String url = BAIDU_SERRCH_API + musicName;

            String musicJson = NetworkUtils.requestUrlGet(url);
            if(musicJson==null){
                return null;
            }
            LogUtils.debug(TAG,musicJson);
            Map<String,Object> resultMap = JsonUtils.string2Map(musicJson);
            if(resultMap!=null && resultMap.containsKey("song")){
                ArrayList<Object> values = (ArrayList<Object>) resultMap.get("song");
                if(values.size()>0){
                    for(Object v:values) {

                        Map<String, Object> musicMap = (Map<String, Object>) v;
                        if (musicMap != null) {
                            MusicObj musicObj = new MusicObj();
                            musicObj.setArtistName(String.valueOf(musicMap.get("artistname")));
                            musicObj.setName(String.valueOf(musicMap.get("songname")));
                            musicObj.setId(String.valueOf(musicMap.get("songid")));
                            musicObjList.add(musicObj);
                        }
                    }
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            LogUtils.error(TAG,e);
        }

        return musicObjList;
    }

}
