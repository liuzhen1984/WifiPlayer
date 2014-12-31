package com.silveroak.wifiplayer.service.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.string.StringEncoder;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: zliu
 * Date: 14-4-15
 * Time: 上午12:55
 * To change this template use File | Settings | File Templates.
 */
public class EncodeHandler extends StringEncoder {
    @Override
    protected void encode(ChannelHandlerContext ctx, CharSequence msg, List<Object> out) throws Exception {
        super.encode(ctx, msg, out);
    }
}
