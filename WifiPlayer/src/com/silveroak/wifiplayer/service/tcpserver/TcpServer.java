/**
 * 
 */
package com.silveroak.wifiplayer.service.tcpserver;

import com.silveroak.wifiplayer.constants.SystemConstant;
import com.silveroak.wifiplayer.service.handler.InitChannel;
import com.silveroak.wifiplayer.utils.LogUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.util.Set;

/**
 * 
 * @author starry
 * 
 */
public class TcpServer {

    private final static String TAG = TcpServer.class.getSimpleName();
    private static TcpServer tcpServer =null;
    public synchronized static TcpServer init(){
        if(tcpServer ==null){
            tcpServer = new TcpServer();
            tcpServer.start();
        }
        return tcpServer;
    }

    private void running() throws Exception {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.option(ChannelOption.TCP_NODELAY, true)
					.option(ChannelOption.SO_KEEPALIVE, true)
					.handler(new LoggingHandler(LogLevel.INFO))
					.childHandler(new InitChannel());

			// Bind and start to accept incoming connections.
			ChannelFuture f = b.bind(getPort()).sync();
			// Wait until the server socket is closed.
			f.channel().closeFuture().sync();
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}

	public static int getPort() {
		return SystemConstant.PORT.TCP_SERVER_PORT;
	}

    private void listenChannel(){
        while(true){
            try {
                Set<String> aliveSet = ServerCache.getAliveSet();
                for(String alive:aliveSet){
                    ServerCache.findChannel(alive);
                }
                Thread.sleep(500);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isRunning = false;
    private synchronized void start(){
        if(isRunning){
            LogUtils.warn(TAG,"Already running");
            return;
        }
        isRunning = true;
       Runnable runnable = new Runnable() {
            @Override
            public void run() {

                try {
                    running();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(runnable).start();

        runnable = new Runnable() {
            @Override
            public void run() {
                listenChannel();
            }
        };
        new Thread(runnable).start();
    }

}
