package com.silveroak.wifiplayer.service.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

/**
 * Created by zliu on 14/12/18.
 */
public class InitChannel extends ChannelInitializer {
    @Override
    protected void initChannel(Channel ch) throws Exception {
//            ch.pipeline().addFirst(new TransServerHandler());
        ch.pipeline()
                .addFirst(new EncodeHandler())
                .addFirst(new DecodeHandler())
                .addLast(new BusinessServiceHandler())
        ;
    }
}
