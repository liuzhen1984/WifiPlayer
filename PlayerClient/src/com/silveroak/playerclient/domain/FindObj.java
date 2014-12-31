package com.silveroak.playerclient.domain;

/**
 * Created by zliu on 14/12/30.
 */
public class FindObj {
    private String server;
    private Integer port;

    @Override
    public String toString() {
        return "FindObj{" +
                "server='" + server + '\'' +
                ", port=" + port +
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
}
