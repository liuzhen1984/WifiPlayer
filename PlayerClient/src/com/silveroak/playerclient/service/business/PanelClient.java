package com.silveroak.playerclient.service.business;/*

import android.content.Context;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.List;

/**
 * Sends one message when a connection is open and echoes back any received data
 * to the server. Simply put, the echo client initiates the ping-pong traffic
 * between the echo client and server by sending the first message to the
 * server.
 */

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import com.silveroak.playerclient.domain.TcpRequest;
import com.silveroak.playerclient.preference.data.SystemInfo;
import com.silveroak.playerclient.ui.base.PlayerBaseFragment;
import com.silveroak.playerclient.utils.JsonUtils;
import com.silveroak.playerclient.utils.LogUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.List;

public class PanelClient {
	private final static String TAG = PanelClient.class.getSimpleName();

	private static Context context = null;

	private static PanelClient panelClient = null;
	private PanelClient(Context context) {
		 this.context = context;

	}
	public static PanelClient getClient(){
		return panelClient;
	}
	public synchronized static PanelClient init(Context context){
		if(panelClient==null){
			panelClient = new PanelClient(context);
		}
		return panelClient;
	}

	private SystemInfo systemInfo = null;

	private Channel channel = null;
	private ChannelFuture channelFuture = null;
	public Channel getChannel(){
		return channel;
	}

	public Boolean start(SystemInfo systemInfo) {
		if(systemInfo==null || systemInfo.getPort()==null||systemInfo.getServer()==null){
			return false;
		}
		this.systemInfo = systemInfo;
		String host= systemInfo.getServer();
		Integer port=systemInfo.getPort();

		if(host==null || "".equalsIgnoreCase(host.trim())){
			LogUtils.warn(TAG,"No server host");
			return false;
		}
		if(port==null||port<8080){
			LogUtils.warn(TAG,"No server port");
			return false;
		}
		if(channelFuture !=null && channelFuture.channel().isOpen()){
            try{
                channelFuture.channel().close();
            }catch (Exception ex){}

            channelFuture=null;
            channel=null;
		}

		// Configure the client.
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(group).channel(NioSocketChannel.class)
					.option(ChannelOption.TCP_NODELAY, true)
					.handler(new ChannelInitializer<SocketChannel>() {
						@Override
						public void initChannel(SocketChannel ch)
								throws Exception {
							ch.pipeline().addLast(
//									new LoggingHandler(LogLevel.INFO),
									new StringDecoder() {
										@Override
									    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
									        super.decode(ctx, msg, out);
									    }
									},
                                    new StringEncoder() {
                                        @Override
                                        protected void encode(ChannelHandlerContext ctx, CharSequence msg, List<Object> out) throws Exception {
                                            super.encode(ctx, msg, out);
                                        }
                                    },
									new PanelClientHandler());
						}
					});

			// Start the client.
			channelFuture = b.connect(host, port).sync();
			channel = channelFuture.channel();

			TcpRequest tcpRequest = new TcpRequest();
			tcpRequest.setUrl("/play/info");
			channelFuture.channel().writeAndFlush(JsonUtils.object2String(tcpRequest));
			channelFuture.channel().closeFuture().sync();

		} catch (Exception ex){
			LogUtils.error(TAG,ex);
			return false;
		}
		finally {
			group.shutdownGracefully();
		}
		return true;
	}

	public void close(){
		try {
			if(channelFuture!=null) {
				channelFuture.channel().close();
			}
		}catch (Exception ex){
			LogUtils.error(PanelClient.class.getSimpleName(),ex);
		} finally {
			channelFuture = null;
            channel = null;
		}
	}

    /**
     * /play/*       参数
         * sync,     获取当前CurrentPlayer 信息
         * info,     根据name获取music信息
         * list,     获取播放列表
         * start,
         * paused,
         * stop,
         * next,
         * previous,
         * delete,    name= all清空， 删除新的
         * play      传过来字符串 music id
         * add       当前播放的操作 参数是Music 对象的json串
         * volume    1 /-1
     * @param dst
     * @param payload
     */
    public void sendTo(final String dst, final String payload){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (channel != null && !channel.isOpen()) {
                    start(systemInfo);
                }
                if (channel != null && channel.isOpen()) {
                    TcpRequest tcpRequest = new TcpRequest();
                    tcpRequest.setUrl(dst);
                    tcpRequest.setPayload(payload);
                    channel.writeAndFlush(JsonUtils.object2String(tcpRequest));
                } else {
                    Message toUI = new Message();
                    Bundle bundle = new Bundle();
                    toUI.setData(bundle);
                    toUI.what = PlayerBaseFragment.CONNECT_DEVICE_ERROR;
                    PlayerBaseFragment.sendMessages(toUI);
                    sendMessage("Connected device error!");
                }
            }
        }).start();

    }


    private void sendMessage(String msg){
        Message toUI = new Message();
        Bundle bundle = new Bundle();
        bundle.putString(PlayerBaseFragment.MESSAGE_KEY, msg);
        toUI.setData(bundle);
        toUI.what = PlayerBaseFragment.MESSAGE;
        PlayerBaseFragment.sendMessages(toUI);
    }
}
