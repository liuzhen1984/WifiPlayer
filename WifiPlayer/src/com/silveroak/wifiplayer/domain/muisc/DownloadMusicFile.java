package com.silveroak.wifiplayer.domain.muisc;

import com.silveroak.wifiplayer.constants.MusicType;

import java.util.Arrays;

/**
 * Created by zliu on 15/1/4.
 */
public class DownloadMusicFile {
    private MusicType type;
    private String name;
    private String musicName;
    private byte[] data;
    private String path;
    private Integer size;

    @Override
    public String toString() {
        return "DownloadMusicFile{" +
                "type=" + type +
                ", name='" + name + '\'' +
                ", musicName='" + musicName + '\'' +
                ", data=" + Arrays.toString(data) +
                ", path='" + path + '\'' +
                ", size=" + size +
                '}';
    }

    public String getMusicName() {
        return musicName;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public MusicType getType() {
        return type;
    }

    public void setType(MusicType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
