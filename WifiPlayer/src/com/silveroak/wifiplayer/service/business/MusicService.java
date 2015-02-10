package com.silveroak.wifiplayer.service.business;

import android.content.Context;
import com.silveroak.wifiplayer.database.MusicHelper;
import com.silveroak.wifiplayer.domain.ErrorCode;
import com.silveroak.wifiplayer.domain.Result;
import com.silveroak.wifiplayer.domain.muisc.Music;
import com.silveroak.wifiplayer.utils.JsonUtils;
import com.silveroak.wifiplayer.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zliu on 14/12/26.
 *    music: add,delete,collect
 */
public class MusicService implements IProcessService {
    private final static String TAG = MusicService.class.getSimpleName();
    private final String uri = "music";
    private static MusicService iService = null;
    private Context context;
    private MusicHelper musicHelper;
    private static List<String> ADD_URL = new ArrayList<String>();
    public synchronized static MusicService getService(Context context){
        if(iService==null){
            iService = new MusicService();
            iService.context = context;
            iService.musicHelper = new MusicHelper(context);
        }
        return iService;
    }


    @Override
    public String getUri(){
        return uri;
    }
    @Override
    public Result path(String type,String params){
        LogUtils.debug(TAG,"type:"+type+" params:"+params);
        Result result = new Result();
        if(params==null||"".equals(params.trim())){
            result.setResult(ErrorCode.USER_ERROR.PARAMS_FORMAT);
            return result;
        }
        try {
            if(type==null||"".equals(type.trim())){
                all(JsonUtils.string2Map(params));
            }
            else
            if ("add".equals(type)) {
                return add(params);
            } else if ("delete".equals(type)) {
                return delete(params);
            } else if ("collect".equals(type)) {
                return collect(JsonUtils.string2Map(params));
            } else {
                result.setResult(ErrorCode.USER_ERROR.URL_INVALID);
            }
        }catch (Exception ex){
            LogUtils.error(TAG,ex);
            result.setResult(ErrorCode.USER_ERROR.PARAMS_FORMAT);
        }
        return result;
    }
    private Result all(Map<String,Object> params){
        LogUtils.debug(TAG,"get all");
        Result result = new Result();
        result.setResult(ErrorCode.SUCCESS);
        return result;
    }

    /**
     *
     * @param url    音乐的唯一链接
     * @return
     */
    private Result add(String url){
        LogUtils.debug(TAG,"Add url to music");
        //url 需要下载，解析内容
        Result result = new Result();

        Music music = musicHelper.findByUrl(url);
        if(music==null){
            //todo 下载解析
            ADD_URL.add(url);
        }
        result.setResult(ErrorCode.SUCCESS);
        return result;
    }
    private Result delete(String musicId){
        LogUtils.debug(TAG, " Delete music "+ String.valueOf(musicId) );
        Result result = new Result();
        result.setResult(ErrorCode.SUCCESS);

        return result;
    }
    private Result collect(Map<String,Object> params){
        LogUtils.debug(TAG," params:"+params);

        Result result = new Result();
        result.setResult(ErrorCode.SUCCESS);

        return result;
    }

    private void listenAddUrl(){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                while (true){
                    List<String> urls = new ArrayList<String>(ADD_URL);
                    if(urls.size()>0){
                        for(String url:urls){
                            //todo 下载解析，并保存
                            Music music = new Music();
                            music.setUrl(url);
                            musicHelper.insert(music);
                        }
                    }
                    try {
                        Thread.sleep(3000l);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        };
        new Thread(runnable).start();

    }

}
