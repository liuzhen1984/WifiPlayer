package com.silveroak.wifiplayer.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import com.silveroak.wifiplayer.domain.muisc.MusicAPIUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by haoquanqing on 14-6-11.
 */
public class MusicAPIUserHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "wifi_player_db";
    private static final int DB_VERSION = 1;

    private static boolean isCreate = false;

    private static final String TABLE_NAME = "music_api_user";
    private static final String INDEX_NAME = "index_music_api_user";
    private static final String _ID = "_id";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String API_ID = "api_id";
    private static final String SESSION = "session";
    private static final String IS_DEFAULT = "is_default";     //0,1 1表示默认

    public MusicAPIUserHelper(Context context) {
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
     private static final String _ID = "_id";
     private static final String USERNAME = "username";
     private static final String PASSWORD = "password";
     private static final String API_URL = "api_url";
     private static final String SESSION = "session";
     private static final String IS_DEFAULT = "is_default";
     * @param db
     */
    private void createTableAndIndex(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + USERNAME + " varchar(50), "
                + PASSWORD + " varchar(100)  ,"
                + API_ID + " INTEGER  ,"
                + SESSION + " varchar(255)  ,"
                + IS_DEFAULT + " tiny(1) "
                + ");");
        db.execSQL("CREATE INDEX IF NOT EXISTS " + INDEX_NAME + " ON " + TABLE_NAME + " ("
                + USERNAME + ");");
        isCreate = true;

    }

    /**
     * 根据接口获取用户列表
     * @param apiId
     * @return
     */
    public List<MusicAPIUser> queryLast(long apiId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = null;
        List<MusicAPIUser> musicAPIs = new ArrayList<MusicAPIUser>();
        try {

            if (db != null) {
                c = db.rawQuery("SELECT * FROM " + TABLE_NAME +  "  where "+API_ID+"="+apiId,new String[0]);
                while(c!=null && c.moveToNext()) {
                    MusicAPIUser tasks = new MusicAPIUser();
                    tasks.set_id(c.getLong(c.getColumnIndex(_ID)));
                    tasks.setApiId(c.getLong(c.getColumnIndex(API_ID)));
                    tasks.setIsDefault(c.getInt(c.getColumnIndex(IS_DEFAULT)));
                    tasks.setUsername(c.getString(c.getColumnIndex(USERNAME)));
                    tasks.setPassword(c.getString(c.getColumnIndex(PASSWORD)));
                    tasks.setSession(c.getString(c.getColumnIndex(SESSION)));
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
    public MusicAPIUser findById(long id) {
        MusicAPIUser result = null;
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = null;
        try {
            if (db != null) {
                c = db.rawQuery("SELECT * FROM " + TABLE_NAME +  "where "+_ID+"="+id +";",new String[0]);
                if (c != null && c.moveToFirst()) {
                    MusicAPIUser tasks = new MusicAPIUser();

                    tasks.set_id(c.getLong(c.getColumnIndex(_ID)));
                    tasks.setApiId(c.getLong(c.getColumnIndex(API_ID)));
                    tasks.setIsDefault(c.getInt(c.getColumnIndex(IS_DEFAULT)));
                    tasks.setUsername(c.getString(c.getColumnIndex(USERNAME)));
                    tasks.setPassword(c.getString(c.getColumnIndex(PASSWORD)));
                    tasks.setSession(c.getString(c.getColumnIndex(SESSION)));
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

    /**
     * private static final String USERNAME = "username";
     private static final String PASSWORD = "password";
     private static final String API_URL = "api_url";
     private static final String SESSION = "session";
     private static final String IS_DEFAULT = "is_default";
     * @param tasks
     */
    public void insert(MusicAPIUser tasks){
        String sql = "insert into "+TABLE_NAME+"("+USERNAME+","
                +PASSWORD+","+API_ID+","+SESSION+","+IS_DEFAULT+","
                + ") values(?,?,?,?,?,)";
        SQLiteDatabase db = getReadableDatabase();
        SQLiteStatement stat = db.compileStatement(sql);

        db.beginTransaction();
        stat.bindString(1, tasks.getUsername());
        stat.bindString(2, tasks.getPassword());
        stat.bindLong(3, tasks.getApiId());
        stat.bindString(4, tasks.getSession());
        stat.bindString(5, String.valueOf(tasks.getIsDefault()));
        stat.executeInsert();
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }
    //插入
    public void update(long _id,MusicAPIUser tasks){
        String sql = "UPDATE "+TABLE_NAME+" SET "
                +USERNAME+"=?,"+PASSWORD+"=?,"
                +API_ID+"=?,"+SESSION+"=?,"
                +IS_DEFAULT+"=? where "+_ID+"="+_id;
        SQLiteDatabase db = getReadableDatabase();
        SQLiteStatement stat = db.compileStatement(sql);

        db.beginTransaction();
        stat.bindString(1, tasks.getUsername());
        stat.bindString(2, tasks.getPassword());
        stat.bindLong(3, tasks.getApiId());
        stat.bindString(4, tasks.getSession());
        stat.bindString(5, String.valueOf(tasks.getIsDefault()));
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
