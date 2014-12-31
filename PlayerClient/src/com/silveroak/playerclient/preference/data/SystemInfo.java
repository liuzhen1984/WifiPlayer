package com.silveroak.playerclient.preference.data;

import com.silveroak.playerclient.preference.StorageHelperI;
import com.silveroak.playerclient.utils.JsonUtils;

/**
 * Created by haoquanqing on 14-6-1.
 * Away设置
 */
public class SystemInfo implements StorageHelperI {

    private final static String key = "player_client_systeminfo";
    private String  server;
    private Integer port;
    private Long connectTime;

    public SystemInfo() {

    }

    @Override
    public String toString() {
        return "SystemInfo{" +
                "server='" + server + '\'' +
                ", port=" + port +
                ", connectTime=" + connectTime +
                '}';
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Long getConnectTime() {
        return connectTime;
    }

    public void setConnectTime(Long connectTime) {
        this.connectTime = connectTime;
    }

    public final static String key(){
        return key;
    }

    @Override
    public final String getKey() {
        return key;
    }

    @Override
    public String onSaveData() {
        return JsonUtils.object2String(this);
    }
}
