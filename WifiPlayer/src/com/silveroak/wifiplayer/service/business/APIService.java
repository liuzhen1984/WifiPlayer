package com.silveroak.wifiplayer.service.business;

import android.content.Context;
import com.silveroak.wifiplayer.domain.ErrorCode;
import com.silveroak.wifiplayer.domain.Result;
import com.silveroak.wifiplayer.utils.JsonUtils;
import com.silveroak.wifiplayer.utils.LogUtils;

import java.util.Map;

/**
 * Created by zliu on 14/12/26.
 *
 * api:   list,user(add,delete)
 */
public class APIService implements IProcessService {
    private final static String TAG = APIService.class.getSimpleName();
    private final String uri = "api";
    private static APIService iService = null;
    public synchronized static APIService getService(Context context){
        if(iService==null){
            iService = new APIService();
        }
        return iService;
    }


    @Override
    public String getUri(){
        return uri;
    }
    @Override
    public Result path(String type, String params){
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
            if ("list".equals(type)) {
                return list(JsonUtils.string2Map(params));
            } else if ("user".equals(type)) {
                return user(JsonUtils.string2Map(params));
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


    private Result list(Map<String,Object> params){
        LogUtils.debug(TAG," params:"+params);
        Result result = new Result();
        return result;
    }
    private Result user(Map<String,Object> params){
        LogUtils.debug(TAG," params:"+params);

        Result result = new Result();
        return result;
    }
}
