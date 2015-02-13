package com.silveroak.wifiplayer.service.handler;

import com.silveroak.wifiplayer.constants.MessageConstant;
import com.silveroak.wifiplayer.domain.TcpRequest;
import com.silveroak.wifiplayer.service.RouteService;
import com.silveroak.wifiplayer.service.tcpserver.ServerCache;
import com.silveroak.wifiplayer.utils.JsonUtils;
import com.silveroak.wifiplayer.utils.LogUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Created with IntelliJ IDEA.
 * User: zliu
 * Date: 14-4-13
 * Time: 下午11:42
 * To change this template use File | Settings | File Templates.
 */
public class BusinessServiceHandler extends SimpleChannelInboundHandler<Object> {

    private static final String TAG =
            BusinessServiceHandler.class.getSimpleName();

    private String tempStr="";

    @Override
    public void channelActive(ChannelHandlerContext ctx){
        LogUtils.info(TAG, ctx.channel().remoteAddress().toString());
    }

    /**
     *
     * @param ctx
     * @param msg
     *    map:
     *        url: /play/....
     *        payload:{}
     * @throws InterruptedException
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        String errorMsg = "From client msg format error!";
        if(msg==null){
            LogUtils.error(TAG,errorMsg);
            ctx.writeAndFlush(errorMsg);
            ctx.close();
            return;
        }
        Channel channel = ServerCache.findChannel(ctx.channel().remoteAddress().toString());
        if(channel==null){
            ServerCache.addClientChannel(ctx.channel());
        }
        try{
            if(MessageConstant.HB_STR.equalsIgnoreCase(msg.toString())){
                ctx.writeAndFlush(MessageConstant.HB_STR);
            }else {
                String strMsg = msg.toString();
                if(strMsg.startsWith("{") && strMsg.endsWith("}")){
                    tempStr = strMsg;
                }else{
                    if(!strMsg.startsWith("{")){
                        if(tempStr.startsWith("{")){
                            tempStr = tempStr+strMsg;
                            if(!strMsg.endsWith("}")){
                                return;
                            }
                        }else{
                            tempStr="";
                        }
                    }else{
                        //结束不等于} ，但是开始是{表示没有结束
                        tempStr = tempStr+strMsg;
                        return;
                    }
                }
                //表示tempStr有缓存
                if(!"".equals(tempStr)){
                    strMsg = tempStr;
                }
                //否则 strMsg 没有开头也没有结尾

                if(!(strMsg.startsWith("{") && strMsg.endsWith("}"))) {
                    tempStr = tempStr + msg.toString();
                    if (!tempStr.endsWith("}")) {
                        return;
                    }
                    strMsg = tempStr;
                }
                tempStr = "";
                String r = RouteService.init().tcpRequest(
                        JsonUtils.string2Object(strMsg, TcpRequest.class)
                );
                if(r!=null && !"".equalsIgnoreCase(r)){
                    ctx.writeAndFlush(r);
                }
            }
        }catch (Exception ex){
            LogUtils.error(TAG,ex);
        }

        return;
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        LogUtils.info(TAG,"ChannelReadComplete "+ctx.channel().remoteAddress().toString());
        ctx.flush();
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
        cause.getCause().printStackTrace();
        Channel channel = ctx.channel();
        System.out.println("Exit client channel：" + channel);
        channel.close();
    }
}
