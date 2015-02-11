package com.silveroak.wifiplayer.constants;

/**
 * Created by zliu on 15/1/4.
 */
public enum ConfigStatusEnum {
    NONE(1),
    AP_STATUS(2),
    DO_CONFIG(3);
    private int status;
    ConfigStatusEnum(int status){
        this.status = status;
    }
    public int getStatus(){
        return this.status;
    }
}
