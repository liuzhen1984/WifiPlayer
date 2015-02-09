package com.silveroak.playerclient.domain;

import java.io.Serializable;

/**
 * Created by zliu on 15/2/9.
 */
public class MusicObj implements Serializable{

    private String name;
    private String url;
    private Long length;

    @Override
    public String toString() {
        return "MusicObj{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", length=" + length +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getLength() {
        return length;
    }

    public void setLength(Long length) {
        this.length = length;
    }
}
