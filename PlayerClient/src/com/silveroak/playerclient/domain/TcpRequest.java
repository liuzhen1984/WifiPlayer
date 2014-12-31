package com.silveroak.playerclient.domain;

/**
 * Created by zliu on 14/12/29.
 */
public class TcpRequest {
    private String url;
    private String payload;

    @Override
    public String toString() {
        return "TcpRequest{" +
                "url='" + url + '\'' +
                ", payload='" + payload + '\'' +
                '}';
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}
