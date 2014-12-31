package com.silveroak.wifiplayer.service;

import com.silveroak.wifiplayer.domain.ErrorCode;
import com.silveroak.wifiplayer.domain.Result;
import com.silveroak.wifiplayer.domain.TcpRequest;
import com.silveroak.wifiplayer.service.business.IProcessService;
import com.silveroak.wifiplayer.utils.ByteUtils;
import com.silveroak.wifiplayer.utils.JsonUtils;
import com.silveroak.wifiplayer.utils.LogUtils;
import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * Created by zliu on 14/12/26.
 */
public class RouteService {
    private final static String TAG = RouteService.class.getSimpleName();

    private static RouteService routeService = null;
    public static RouteService init(){
        if(routeService==null){
            routeService = new RouteService();
        }
        return routeService;
    }
    public HttpResponse get(String path,final HttpRequest request,
                            final HttpResponse response, final HttpContext context) throws UnsupportedEncodingException {

        //get uri
        StringBuffer responseStr = new StringBuffer();
        for(Header header:request.getAllHeaders()){
            responseStr.append(header.getName()+":"+header.getValue());
            responseStr.append("\n");
        }

        responseStr.append("{method:get ,target:" + path + "}");
        response.setStatusCode(HttpStatus.SC_OK);
        StringEntity entity = new StringEntity(responseStr.toString());
        response.setEntity(entity);
        return response;
    }

    public HttpResponse post(String path,final HttpRequest request,
                       final HttpResponse response, final HttpContext context) throws UnsupportedEncodingException {
        BasicHttpEntityEnclosingRequest basicRequest = (BasicHttpEntityEnclosingRequest) request;
        //get uri
        StringBuffer responseStr = new StringBuffer();
        for(Header header:request.getAllHeaders()){
            responseStr.append(header.getName()+":"+header.getValue());
            responseStr.append("\n");
        }
        StringBuffer sb = new StringBuffer();
        byte[] sbyte = new byte[1024];
        byte[] value = new byte[0];
        try {
            int count = 0;
            InputStream is = basicRequest.getEntity().getContent();
            while( (count = is.read(sbyte))>0){
                value = ByteUtils.append(value,ByteUtils.subByte(sbyte,0,count));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        LogUtils.debug(TAG, "Body:" + new String(value, "utf-8"));

        Result result = parserPath(path,new String(value));

        responseStr.append(result);
        response.setStatusCode(HttpStatus.SC_OK);
        StringEntity entity = new StringEntity(responseStr.toString());
        response.setEntity(entity);
        return response;
    }

    public String tcpRequest(TcpRequest request){
        Result result = new Result();
        result.setWhat(IHandlerWhatAndKey.UPDATE_INFO);

        if(request==null || request.getUrl()==null){
            result.setResult(ErrorCode.USER_ERROR.PARAMS_FORMAT);
            return JsonUtils.object2String(result);
        }
        result= parserPath(request.getUrl(),request.getPayload());
        if(result.getResult()==ErrorCode.SUCCESS && result.getPayload()==null){
            return "";
        }
        return JsonUtils.object2String(result);
    }

    /**
     *
     * @param url / {main} / {subtype}    最多二级
     *             main: play,music,api,list
     *               subtype:
     *                   play:  start,paused,stop,next,previous,play  当前播放的操作
     *                   music: add,delete,collect
     *                   api:   list,user(add,delete)
     *                   list:  get:表示列出所有的列表明，（name：。。。）列出某个列表的所有歌曲
     *                          play：
     *                          delete:
     *                          insert:
     * @return
     */
    private Result parserPath(String url,String parmas){
        Result result = new Result();

        if(url.startsWith("/")){
            String path = url.substring(1);
            String[] paths = path.split("/");
            LogUtils.debug(TAG,"path:"+path);
            for(IProcessService processService:InitService.processServices()){
                String pType = null;
                LogUtils.debug(TAG,"subType:"+paths[0]);
                if(processService.getUri().equalsIgnoreCase(paths[0])){
                    if(paths.length>1) {
                        pType = paths[1];
                    }
                    LogUtils.debug(TAG,pType);
                    return processService.path(pType,parmas);
                }
            }
        }
        result.setResult(ErrorCode.USER_ERROR.URL_INVALID);
        return result;
    }


}
