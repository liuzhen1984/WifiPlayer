package com.silveroak.wifiplayer.service.business;

import android.content.Context;
import com.silveroak.wifiplayer.database.PlayerListHelper;
import com.silveroak.wifiplayer.domain.ErrorCode;
import com.silveroak.wifiplayer.domain.Result;
import com.silveroak.wifiplayer.domain.muisc.PlayerList;
import com.silveroak.wifiplayer.utils.JsonUtils;
import com.silveroak.wifiplayer.utils.LogUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by zliu on 14/12/26.
 * list:
 *           all: 列出所有播放列表
 *           get:（name：。。。）列出某个列表的所有歌曲
*            play：
*            delete:
*           insert:
 *           /list/[get|play|delete|insert]
 */
public class ListService implements IProcessService {
    private final static String TAG = ListService.class.getSimpleName();
    private final String uri = "list";

    private static PlayerListHelper playerListHelper;

    private static ListService iService = null;
    public synchronized static ListService getService(Context context){
        if(iService==null){
            iService = new ListService();
            playerListHelper = new PlayerListHelper(context);
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
            if ("get".equals(type)) {
                return get(JsonUtils.string2Map(params));
            } else if ("play".equals(type)) {
                return play(JsonUtils.string2Map(params));
            } else if ("delete".equals(type)) {
                return delete(JsonUtils.string2Map(params));
            } else if ("insert".equals(type)) {
                return insert(JsonUtils.string2Map(params));
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
        List<PlayerList> playerList = playerListHelper.queryLast();
        result.setPayload(playerList);
        result.setResult(ErrorCode.SUCCESS);
        return result;
    }

    /**
     *
     * @param params
     *    params: null 或者 params.name ==null  return 所有的播放列表
     *    params.name = 某个播放列表的内容
     * @return
     */
    private Result get(Map<String,Object> params){
        LogUtils.debug(TAG," params:"+params);
        Result result = new Result();
        result.setResult(ErrorCode.SUCCESS);
        return result;
    }
    private Result play(Map<String,Object> params){
        LogUtils.debug(TAG, " params:" + params);
        Result result = new Result();
        result.setResult(ErrorCode.SUCCESS);

        return result;
    }
    private Result delete(Map<String,Object> params){
        LogUtils.debug(TAG," params:"+params);

        Result result = new Result();
        result.setResult(ErrorCode.SUCCESS);

        return result;
    }
    private Result insert(Map<String,Object> params){
        LogUtils.debug(TAG," params:"+params);

        Result result = new Result();
        result.setResult(ErrorCode.SUCCESS);

        return result;
    }
}
