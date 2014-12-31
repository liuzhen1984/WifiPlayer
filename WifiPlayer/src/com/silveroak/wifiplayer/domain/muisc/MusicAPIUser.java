package com.silveroak.wifiplayer.domain.muisc;

/**
 * Created by zliu on 14/12/9.
 */
public class MusicAPIUser {
    /**
     *
     private static final String _ID = "_id";
     private static final String USERNAME = "username";
     private static final String PASSWORD = "password";
     private static final String API_URL = "api_url";
     private static final String SESSION = "session";
     private static final String IS_DEFAULT = "is_default";
     */
    private long _id;
    private  String username ;
    private  String password ;
    private  long apiId ;
    private  String session;
    private  int isDefault;

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getApiId() {
        return apiId;
    }

    public void setApiId(long apiId) {
        this.apiId = apiId;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public int getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(int isDefault) {
        this.isDefault = isDefault;
    }
}
