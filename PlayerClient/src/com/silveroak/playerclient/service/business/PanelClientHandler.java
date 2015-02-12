package com.silveroak.playerclient.service.business;/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

import android.os.Bundle;
import android.os.Message;
import com.silveroak.playerclient.constants.MessageConstant;
import com.silveroak.playerclient.domain.ErrorCode;
import com.silveroak.playerclient.domain.Result;
import com.silveroak.playerclient.ui.base.PlayerBaseFragment;
import com.silveroak.playerclient.utils.JsonUtils;
import com.silveroak.playerclient.utils.LogUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Handler implementation for the echo client.  It initiates the ping-pong
 * traffic between the echo client and server by sending the first message to
 * the server.
 */
public class PanelClientHandler  extends SimpleChannelInboundHandler<Object> {

    private static final String TAG =
            PanelClientHandler.class.getSimpleName();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        LogUtils.debug(TAG, "From client msg:" + msg);
        if(msg==null){
            return;
        }
        String strMsg = msg.toString();
        if(MessageConstant.HB_STR.equalsIgnoreCase(strMsg)){
            return;
        }

        //todo 返回给前台activity显示
        Result result = JsonUtils.string2Object(strMsg,Result.class);
        if(result==null){
            LogUtils.error(TAG,"msg format error");
            return;
        }
        Message toUI = new Message();
        Bundle bundle = new Bundle();
        if(result.getResult()!= ErrorCode.SUCCESS){
            bundle.putString(PlayerBaseFragment.MESSAGE_KEY, result.getResult() + "");
            toUI.setData(bundle);
            toUI.what = PlayerBaseFragment.MESSAGE;
            return;
        }
        LogUtils.debug(TAG, msg.toString());
        bundle.putString(PlayerBaseFragment.MESSAGE_KEY, JsonUtils.object2String(result.getPayload()));
        toUI.setData(bundle);
        toUI.what = result.getWhat();
        PlayerBaseFragment.sendMessages(toUI);
        return;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        LogUtils.warn(TAG, "Unexpected exception from downstream." + cause);
        ctx.close();
    }
}
