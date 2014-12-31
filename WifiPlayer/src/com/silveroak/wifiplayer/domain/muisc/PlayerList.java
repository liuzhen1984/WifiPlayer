package com.silveroak.wifiplayer.domain.muisc;

/**
 * Created by zliu on 14/12/9.
 */
public class PlayerList {
    private long _id;
    private String aliasName;

    @Override
    public String toString() {
        return "PlayerList{" +
                "_id=" + _id +
                ", aliasName='" + aliasName + '\'' +
                '}';
    }

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public String getAliasName() {
        return aliasName;
    }

    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }
}
