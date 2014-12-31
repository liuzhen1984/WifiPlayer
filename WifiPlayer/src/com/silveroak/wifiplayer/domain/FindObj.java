package com.silveroak.wifiplayer.domain;

/**
 * Created by zliu on 14/12/30.
 */
public class FindObj {
    private String server;
    private int port;

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

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
