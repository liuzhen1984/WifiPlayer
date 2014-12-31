package com.silveroak.wifiplayer.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import com.silveroak.wifiplayer.domain.muisc.PlayerList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by haoquanqing on 14-6-11.
 */
public class PlayerListHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "wifi_player_db";
    private static final int DB_VERSION = 1;

    private static boolean isCreate = false;

    private static final String TABLE_NAME = "player_list";
    private static final String INDEX_NAME = "index_player_list";
    private static final String _ID = "_id";
    private static final String ALIAS_NAME = "alias_name";

    public PlayerListHelper(Context context) {
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
                + ALIAS_NAME + " varchar(50), "
                + ");");
        db.execSQL("CREATE INDEX IF NOT EXISTS " + INDEX_NAME + " ON " + TABLE_NAME + " ("
                + ALIAS_NAME + ");");
        isCreate = true;

    }

    //查询
    public List<PlayerList> queryLast() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = null;
        List<PlayerList> musicAPIs = new ArrayList<PlayerList>();
        try {

            if (db != null) {
                c = db.rawQuery("SELECT * FROM " + TABLE_NAME +  " order by "+_ID+" DESC;",new String[0]);
                while(c!=null && c.moveToNext()) {
                    PlayerList tasks = new PlayerList();
                    tasks.set_id(c.getLong(c.getColumnIndex(_ID)));
                    tasks.setAliasName(c.getString(c.getColumnIndex(ALIAS_NAME)));
                    musicAPIs.add(tasks);
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
        return musicAPIs;
    }

    //根据id查询
    public PlayerList findById(long id) {
        PlayerList result = null;
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = null;
        try {
            if (db != null) {
                c = db.rawQuery("SELECT * FROM " + TABLE_NAME +  "where "+_ID+"="+id +";",new String[0]);
                if (c != null && c.moveToFirst()) {
                    PlayerList tasks = new PlayerList();

                    tasks.set_id(c.getLong(c.getColumnIndex(_ID)));
                    tasks.setAliasName(c.getString(c.getColumnIndex(ALIAS_NAME)));
                    result = tasks;
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
        return result;
    }

    //插入
    public void insert(PlayerList tasks){
        String sql = "insert into "+TABLE_NAME+"("+ALIAS_NAME+ ") values(?)";
        SQLiteDatabase db = getReadableDatabase();
        SQLiteStatement stat = db.compileStatement(sql);

        db.beginTransaction();
        stat.bindString(1, tasks.getAliasName());
        stat.executeInsert();
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }
    //插入
    public void update(long _id,PlayerList tasks){
        String sql = "UPDATE "+TABLE_NAME+" SET "
                +ALIAS_NAME+"=? where "+_ID+"="+_id;
        SQLiteDatabase db = getReadableDatabase();
        SQLiteStatement stat = db.compileStatement(sql);

        db.beginTransaction();
        stat.bindString(1, tasks.getAliasName());
        stat.executeUpdateDelete();
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }
    //插入
    public void delete(long _id){
        String sql = "DELETE FROM "+TABLE_NAME+" where "+_ID+"="+_id;
        SQLiteDatabase db = getReadableDatabase();
        SQLiteStatement stat = db.compileStatement(sql);
        db.beginTransaction();
        stat.executeUpdateDelete();
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }


}
