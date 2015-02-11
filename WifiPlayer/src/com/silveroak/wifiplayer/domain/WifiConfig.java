package com.silveroak.wifiplayer.domain;

import com.silveroak.wifiplayer.constants.WifiKeyMgmtEnum;

/**
 * Created by zliu on 15/2/10.
 */
public class WifiConfig {
    private String ssid;
    private WifiKeyMgmtEnum keyMgmt;
    private String password;

    @Override
    public String toString() {
        return "WifiConfig{" +
                "ssid='" + ssid + '\'' +
                ", keyMgmt=" + keyMgmt +
                ", password='" + password + '\'' +
                '}';
    }

    public WifiKeyMgmtEnum getKeyMgmt() {
        return keyMgmt;
    }

    public void setKeyMgmt(WifiKeyMgmtEnum keyMgmt) {
        this.keyMgmt = keyMgmt;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
