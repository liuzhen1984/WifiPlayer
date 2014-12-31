package com.silveroak.wifiplayer.domain;

import com.silveroak.wifiplayer.constants.SystemConstant;
import com.silveroak.wifiplayer.domain.muisc.Music;

import java.io.Serializable;

/**
 * Created by zliu on 14/12/30.
 */
public class PlayerInfo implements Serializable{
    private Music music;
    private SystemConstant.PLAYER_TYPE type;
    private SystemConstant.PLAYER_STATUS status;
    private Integer volume;
    private Integer volumeMax;

    @Override
    public String toString() {
        return "PlayerInfo{" +
                "music=" + music +
                ", type=" + type +
                ", status=" + status +
                ", volume=" + volume +
                ", volumeMax=" + volumeMax +
                '}';
    }

    public Integer getVolumeMax() {
        return volumeMax;
    }

    public void setVolumeMax(Integer volumeMax) {
        this.volumeMax = volumeMax;
    }

    public Music getMusic() {
        return music;
    }

    public void setMusic(Music music) {
        this.music = music;
    }

    public SystemConstant.PLAYER_TYPE getType() {
        return type;
    }

    public void setType(SystemConstant.PLAYER_TYPE type) {
        this.type = type;
    }

    public SystemConstant.PLAYER_STATUS getStatus() {
        return status;
    }

    public void setStatus(SystemConstant.PLAYER_STATUS status) {
        this.status = status;
    }

    public Integer getVolume() {
        return volume;
    }

    public void setVolume(Integer volume) {
        this.volume = volume;
    }
}
