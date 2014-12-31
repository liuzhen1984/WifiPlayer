package com.silveroak.wifiplayer.preference.data;

import com.silveroak.wifiplayer.constants.SystemConstant;
import com.silveroak.wifiplayer.preference.StorageHelperI;
import com.silveroak.wifiplayer.utils.JsonUtils;

import java.util.List;

/**
 * Created by haoquanqing on 14-6-1.
 * Away设置
 */
public class CurrentPlayer implements StorageHelperI {

    private final static String key = "current_player_list";
    private List<String>  playerList;     //music url 作为唯一的播放的
    private String playerMusic;
    private SystemConstant.PLAYER_TYPE type;
    private SystemConstant.PLAYER_STATUS status;

    public CurrentPlayer() {

    }

    @Override
    public String toString() {
        return "CurrentPlayer{" +
                "playerList=" + playerList +
                ", playerMusic='" + playerMusic + '\'' +
                ", type=" + type +
                ", status=" + status +
                '}';
    }

    public List<String> getPlayerList() {
        return playerList;
    }

    public void setPlayerList(List<String> playerList) {
        this.playerList = playerList;
    }

    public String getPlayerMusic() {
        return playerMusic;
    }

    public void setPlayerMusic(String playerMusic) {
        this.playerMusic = playerMusic;
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
