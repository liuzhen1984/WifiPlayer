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
import com.silveroak.playerclient.domain.TcpRequest;
import com.silveroak.playerclient.preference.data.SystemInfo;
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
	public static PanelClient init(Context context){
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
			return true;
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
		}
	}
}
