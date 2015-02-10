package com.silveroak.playerclient.domain;

import java.io.Serializable;

/**
 * Created by zliu on 15/2/9.
 */
public class MusicObj implements Serializable{

    private String id;
    private String name;
    private String artistName;
    private Long length;

    @Override
    public String toString() {
        return "MusicObj{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", artistName='" + artistName + '\'' +
                ", length=" + length +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public Long getLength() {
        return length;
    }

    public void setLength(Long length) {
        this.length = length;
    }
}
