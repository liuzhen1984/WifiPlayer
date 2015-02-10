package com.silveroak.playerclient.domain;

/**
 * Created by zliu on 15/2/10.
 */
public class WifiConfig {
    private String ssid;
    private String keyMgmt;
    private String password;


    @Override
    public String toString() {
        return "WifiConfig{" +
                "ssid='" + ssid + '\'' +
                ", keyMgmt='" + keyMgmt + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    public String getKeyMgmt() {
        return keyMgmt;
    }

    public void setKeyMgmt(String keyMgmt) {
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
