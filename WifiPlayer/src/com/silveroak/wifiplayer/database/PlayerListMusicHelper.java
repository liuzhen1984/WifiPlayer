package com.silveroak.wifiplayer.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import com.silveroak.wifiplayer.domain.muisc.PlayerListMusic;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by haoquanqing on 14-6-11.
 */
public class PlayerListMusicHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "wifi_player_list_music_db";
    private static final int DB_VERSION = 1;

    private static boolean isCreate = false;

    private static final String TABLE_NAME = "player_list_music";
    private static final String INDEX_NAME = "index_player_list_music";
    private static final String _ID = "_id";
    private static final String PLAYER_LIST_ID = "player_list_id";
    private static final String MUSIC_ID = "music_id";

    public PlayerListMusicHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTableAndIndex(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        if(!isCreate){
            onCreate(db);
        }
        super.onOpen(db);
    }

    private void reInitDataBase(SQLiteDatabase db) {
        db.execSQL("DROP INDEX IF EXISTS " + INDEX_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        createTableAndIndex(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        reInitDataBase(db);
    }

    /**
     *
     private static final String ALIAS_NAME = "alias_name";
     * @param db
     */
    private void createTableAndIndex(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + PLAYER_LIST_ID + " INTEGER, "
                + MUSIC_ID + " INTEGER, "
                + ");");
        db.execSQL("CREATE INDEX IF NOT EXISTS " + INDEX_NAME + " ON " + TABLE_NAME + " ("
                + PLAYER_LIST_ID + ");");
        isCreate = true;

    }

    //根据id查询

    /**
     * @param playerListId
     * @return
     */
    public List<PlayerListMusic> findByplayerListId(Long playerListId) {
        List<PlayerListMusic> musicList = new ArrayList<PlayerListMusic>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = null;
        try {
            if (db != null) {
                c = db.rawQuery("SELECT * FROM " + TABLE_NAME +  "where "+PLAYER_LIST_ID+"="+playerListId +";",new String[0]);

                while (c != null && c.moveToFirst()) {
                    PlayerListMusic music = new PlayerListMusic();
                    music.setMusicId(c.getLong(c.getColumnIndex(MUSIC_ID)));
                    music.setPalyListId(playerListId);
                    music.set_id(c.getLong(c.getColumnIndex(_ID)));
                    musicList.add(music);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(c!=null){
                c.close();
            }
            if(db!=null){
                db.close();
            }
        }
        return musicList;
    }

    //插入
    public void insert(PlayerListMusic music){
        String sql = "insert into "+TABLE_NAME+"("+PLAYER_LIST_ID+ ","+MUSIC_ID+") values(?,?)";
        SQLiteDatabase db = getReadableDatabase();
        SQLiteStatement stat = db.compileStatement(sql);

        db.beginTransaction();
        stat.bindLong(1, music.getPalyListId());
        stat.bindLong(2, music.getMusicId());
        stat.executeInsert();
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    //插入
    public void delete(PlayerListMusic music){
        String sql = "DELETE FROM "+TABLE_NAME+" where "+PLAYER_LIST_ID+"="+music.getPalyListId()+" and "+MUSIC_ID+"="+music.getMusicId();
        SQLiteDatabase db = getReadableDatabase();
        SQLiteStatement stat = db.compileStatement(sql);
        db.beginTransaction();
        stat.executeUpdateDelete();
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }
}
