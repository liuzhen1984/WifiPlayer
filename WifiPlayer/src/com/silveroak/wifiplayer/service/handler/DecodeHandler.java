package com.silveroak.wifiplayer.service.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.string.StringDecoder;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: zliu
 * Date: 14-4-15
 * Time: 上午12:52
 * To change this template use File | Settings | File Templates.
 */
public class DecodeHandler extends StringDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        super.decode(ctx,msg,out);
    }
}
