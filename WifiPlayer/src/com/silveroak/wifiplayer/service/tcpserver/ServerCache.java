/**
 * 
 */
package com.silveroak.wifiplayer.service.tcpserver;

import io.netty.channel.Channel;
import io.netty.util.internal.PlatformDependent;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * 
 * @author starry
 * 
 */
public class ServerCache {
	private final static ConcurrentMap<String, Channel> channelMap = PlatformDependent
			.newConcurrentHashMap();

	public static boolean addClientChannel(Channel channel) {
        boolean added = true;
        if(!channelMap.containsKey(channel.remoteAddress().toString())) {
            added = channelMap.put(channel.remoteAddress().toString(), channel) == null;
        }
		return added;
	}


    public static Channel findChannelOnly(String key){
        return channelMap.get(key);
    }
	
	public static Channel findChannel(String key) {
		Channel channel = channelMap.get(key);
        if(channel!=null){
           if(!channel.isOpen()){
               removeChannel(key);
           } else{
               return channel;
           }
        } else{
            removeChannel(key);
        }
        return null;
	}

	public static void removeChannel(String key) {
        Channel channel = findChannelOnly(key);
        if(channel!=null){
            channelMap.remove(key);
        }
	}

    public static Set<String> getAliveSet() {
		return channelMap.keySet();
	}
    public static Collection<Channel> getChannels() {
        return channelMap.values();
    }
}
