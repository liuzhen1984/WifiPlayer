package com.silveroak.wifiplayer.constants;

/**
 * Created by zliu on 15/1/4.
 */
public enum  MusicType {
    MP3(1),
    WAV(2);
    private int type;
    MusicType(int type){
        this.type = type;
    }
    public int getType(){
        return this.type;
    }
}
