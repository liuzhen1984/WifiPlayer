package com.silveroak.wifiplayer.constants;

/**
 * Created by zliu on 15/1/4.
 */
public enum WifiKeyMgmtEnum {
    NONE("NONE"),
    WEP("WEP"),
    WPA("WPA");
    private String status;
    WifiKeyMgmtEnum(String status){
        this.status = status;
    }
    public String getStatus(){
        return this.status;
    }
}
