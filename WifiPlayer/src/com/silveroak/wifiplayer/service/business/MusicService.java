package com.silveroak.wifiplayer.service.business;

import android.content.Context;
import com.silveroak.wifiplayer.domain.ErrorCode;
import com.silveroak.wifiplayer.domain.Result;
import com.silveroak.wifiplayer.utils.JsonUtils;
import com.silveroak.wifiplayer.utils.LogUtils;

import java.util.Map;

/**
 * Created by zliu on 14/12/26.
 *    music: add,delete,collect
 */
public class MusicService implements IProcessService {
    private final static String TAG = MusicService.class.getSimpleName();
    private final String uri = "music";
    private static MusicService iService = null;
    public synchronized static MusicService getService(Context context){
        if(iService==null){
            iService = new MusicService();
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
                return add(JsonUtils.string2Map(params));
            } else if ("delete".equals(type)) {
                return delete(JsonUtils.string2Map(params));
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
     * params
     *
     * @param params
     * @return
     */
    private Result add(Map<String,Object> params){
        LogUtils.debug(TAG," params:"+params);
        Result result = new Result();
        result.setResult(ErrorCode.SUCCESS);
        return result;
    }
    private Result delete(Map<String,Object> params){
        LogUtils.debug(TAG, " params:" + params);
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

}
