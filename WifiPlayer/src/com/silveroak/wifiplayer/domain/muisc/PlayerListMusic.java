package com.silveroak.wifiplayer.domain.muisc;

/**
 * Created by zliu on 14/12/9.
 */
public class PlayerListMusic {
    private long _id;
    private long palyListId;
    private long musicId;

    @Override
    public String toString() {
        return "PlayerListMusic{" +
                "_id=" + _id +
                ", palyListId=" + palyListId +
                ", musicId=" + musicId +
                '}';
    }

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public long getPalyListId() {
        return palyListId;
    }

    public void setPalyListId(long palyListId) {
        this.palyListId = palyListId;
    }

    public long getMusicId() {
        return musicId;
    }

    public void setMusicId(long musicId) {
        this.musicId = musicId;
    }
}
