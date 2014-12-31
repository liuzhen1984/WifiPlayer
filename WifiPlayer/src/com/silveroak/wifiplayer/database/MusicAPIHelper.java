package com.silveroak.wifiplayer.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import com.silveroak.wifiplayer.domain.muisc.MusicAPI;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by haoquanqing on 14-6-11.
 */
public class MusicAPIHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "wifi_player_db";
    private static final int DB_VERSION = 1;

    private static boolean isCreate = false;

    private static final String TABLE_NAME = "music_api";
    private static final String INDEX_NAME = "index_music_api";
    private static final String _ID = "_id";
    private static final String ALIAS_NAME = "alias_name";
    private static final String ORGANIZE_NAME = "organize_name";
    private static final String API_URL = "api_url";
    private static final String ICO_URL = "ico_url";
    private static final String ORGANIZE_URL = "organize_url";
    private static final String ORGANIZE_DESC = "organize_desc";
    private static final String COUNT = "count";

    public MusicAPIHelper(Context context) {
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
     private static final String ORGANIZE_NAME = "organize_name";
     private static final String API_URL = "api_url";
     private static final String ICO_URL = "ico_url";
     private static final String ORGANIZE_URL = "organize_url";
     private static final String ORGANIZE_DESC = "organize_desc";
     * @param db
     */
    private void createTableAndIndex(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ALIAS_NAME + " varchar(50), "
                + ORGANIZE_NAME + " varchar(100) , "
                + ORGANIZE_URL + " varchar(255)  ,"
                + ORGANIZE_DESC + " varchar(1024)  ,"
                + API_URL + " varchar(255)  ,"
                + ICO_URL + " varchar(255) "
                + ");");
        db.execSQL("CREATE INDEX IF NOT EXISTS " + INDEX_NAME + " ON " + TABLE_NAME + " ("
                + API_URL + ");");
        isCreate = true;

    }

    //查询
    public List<MusicAPI> queryLast() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = null;
        List<MusicAPI> musicAPIs = new ArrayList<MusicAPI>();
        try {

            if (db != null) {
                c = db.rawQuery("SELECT * FROM " + TABLE_NAME +  " order by "+_ID+" DESC;",new String[0]);
                while(c!=null && c.moveToNext()) {
                    MusicAPI tasks = new MusicAPI();
                    tasks.set_id(c.getLong(c.getColumnIndex(_ID)));
                    tasks.setAliasName(c.getString(c.getColumnIndex(ALIAS_NAME)));
                    tasks.setApiUrl(c.getString(c.getColumnIndex(API_URL)));
                    tasks.setIcoUrl(c.getString(c.getColumnIndex(ICO_URL)));
                    tasks.setOrganizeDesc(c.getString(c.getColumnIndex(ORGANIZE_DESC)));
                    tasks.setOrganizeName(c.getString(c.getColumnIndex(ORGANIZE_NAME)));
                    tasks.setOrganizeUrl(c.getString(c.getColumnIndex(ORGANIZE_URL)));
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
    public MusicAPI findById(long id) {
        MusicAPI result = null;
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = null;
        try {
            if (db != null) {
                c = db.rawQuery("SELECT * FROM " + TABLE_NAME +  "where "+_ID+"="+id +";",new String[0]);
                if (c != null && c.moveToFirst()) {
                    MusicAPI tasks = new MusicAPI();

                    tasks.set_id(c.getLong(c.getColumnIndex(_ID)));
                    tasks.setAliasName(c.getString(c.getColumnIndex(ALIAS_NAME)));
                    tasks.setApiUrl(c.getString(c.getColumnIndex(API_URL)));
                    tasks.setIcoUrl(c.getString(c.getColumnIndex(ICO_URL)));
                    tasks.setOrganizeDesc(c.getString(c.getColumnIndex(ORGANIZE_DESC)));
                    tasks.setOrganizeName(c.getString(c.getColumnIndex(ORGANIZE_NAME)));
                    tasks.setOrganizeUrl(c.getString(c.getColumnIndex(ORGANIZE_URL)));
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
    public void insert(MusicAPI tasks){
        String sql = "insert into "+TABLE_NAME+"("+ALIAS_NAME+","+API_URL+","+ORGANIZE_NAME+","+ORGANIZE_DESC+","+ORGANIZE_URL+","
                +ICO_URL+ ") values(?,?,?,?,?,?)";
        SQLiteDatabase db = getReadableDatabase();
        SQLiteStatement stat = db.compileStatement(sql);

        db.beginTransaction();
        stat.bindString(1, tasks.getAliasName());
        stat.bindString(2, tasks.getApiUrl());
        stat.bindString(3, tasks.getOrganizeName());
        stat.bindString(4, tasks.getOrganizeDesc());
        stat.bindString(5, tasks.getOrganizeUrl());
        stat.bindString(6,tasks.getIcoUrl());
        stat.executeInsert();
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }
    //插入
    public void update(long _id,MusicAPI tasks){
        String sql = "UPDATE "+TABLE_NAME+" SET "
                +ALIAS_NAME+"=?,"+API_URL+"=?,"
                +ORGANIZE_NAME+"=?,"+ORGANIZE_DESC+"=?,"
                +ORGANIZE_URL+"=?," +ICO_URL+ "=? where "+_ID+"="+_id;
        SQLiteDatabase db = getReadableDatabase();
        SQLiteStatement stat = db.compileStatement(sql);

        db.beginTransaction();
        stat.bindString(1, tasks.getAliasName());
        stat.bindString(2, tasks.getApiUrl());
        stat.bindString(3, tasks.getOrganizeName());
        stat.bindString(4, tasks.getOrganizeDesc());
        stat.bindString(5, tasks.getOrganizeUrl());
        stat.bindString(6,tasks.getIcoUrl());
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
