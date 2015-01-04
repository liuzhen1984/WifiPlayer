package com.silveroak.wifiplayer.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import com.silveroak.wifiplayer.domain.muisc.Music;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by haoquanqing on 14-6-11.
 */
public class MusicHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "wifi_player_db";
    private static final int DB_VERSION = 1;

    private static boolean isCreate = false;

    private static final String TABLE_NAME = "music";
    private static final String INDEX_NAME = "index_music";
    private static final String _ID = "_id";
    private static final String NAME = "name";
    private static final String SPECIAL = "special";
    private static final String SINGER = "singer";
    private static final String CATEGORY = "category";
    private static final String URL = "url";
    private static final String LOCAl_PATH = "localPath";
    private static final String SOURCE = "source";

    public MusicHelper(Context context) {
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
     private static final String NAME = "name";
     private static final String SPECIAL = "special";
     private static final String SINGER = "singer";
     private static final String CATEGORY = "category";
     private static final String URL = "url";
     private static final String LOCAl_PATH = "localPath";
     private static final String SOURCE = "source";
     private static final String PLAYER_LIST_ID = "play_list_id";
     * @param db
     */
    private void createTableAndIndex(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NAME + " varchar(50), "
                + SPECIAL + " varchar(50) ,"
                + SINGER + " varchar(50) ,"
                + CATEGORY + " varchar(50) , "
                + URL + " varchar(255)  ,"
                + LOCAl_PATH + " varchar(255)  ,"
                + SOURCE + " varchar(50) "
                + ");");
        db.execSQL("CREATE INDEX IF NOT EXISTS " + INDEX_NAME + " ON " + TABLE_NAME + " ("
                + URL + ");");
        isCreate = true;

    }

    //查询
    public List<Music> queryFromSpecial(String special) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = null;
        List<Music> musicAPIs = new ArrayList<Music>();
        try {

            if (db != null) {
                c = db.rawQuery("SELECT * FROM " + TABLE_NAME +  " where "+SPECIAL+"="+special+";",new String[0]);
                while(c!=null && c.moveToNext()) {
                    musicAPIs.add(trans(c));
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

    //查询
    public List<Music> queryFromSinger(String singer) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = null;
        List<Music> musicAPIs = new ArrayList<Music>();
        try {

            if (db != null) {
                c = db.rawQuery("SELECT * FROM " + TABLE_NAME +  " where "+SINGER+"="+singer+";",new String[0]);
                while(c!=null && c.moveToNext()) {
                    musicAPIs.add(trans(c));
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
    public Music findById(long id) {
        Music result = null;
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = null;
        try {
            if (db != null) {
                c = db.rawQuery("SELECT * FROM " + TABLE_NAME +  " where "+_ID+"="+id +";",new String[0]);
                if (c != null && c.moveToFirst()) {
                    result = trans(c);
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

    //根据url查询
    public Music findByUrl(String url) {
        Music result = null;
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = null;
        try {
            if (db != null) {
                c = db.rawQuery("SELECT * FROM " + TABLE_NAME +  " where "+URL+"=\""+url +"\";",new String[0]);
                if (c != null && c.moveToFirst()) {
                    result = trans(c);
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
    /**
     *  private static final String NAME = "name";
     private static final String SPECIAL = "special";
     private static final String SINGER = "singer";
     private static final String CATEGORY = "category";
     private static final String URL = "url";
     private static final String LOCAl_PATH = "localPath";
     private static final String SOURCE = "source";
     private static final String PLAYER_LIST_ID = "play_list_id";
     * @param tasks
     */
    //插入
    public void insert(Music tasks){
        String sql = "insert into "+TABLE_NAME+"("
                +NAME+","
                +SPECIAL+","
                +SINGER+","
                +CATEGORY+","
                +URL+","
                +LOCAl_PATH +","
                +SOURCE +","
                + ") values(?,?,?,?,?,?,?)";
        SQLiteDatabase db = getReadableDatabase();
        SQLiteStatement stat = db.compileStatement(sql);

        db.beginTransaction();
        stat.bindString(1, tasks.getName());
        stat.bindString(2, tasks.getSpecial());
        stat.bindString(3, tasks.getSinger());
        stat.bindString(4, tasks.getCategory());
        stat.bindString(5, tasks.getUrl());
        stat.bindString(6,tasks.getLocalPath());
        stat.bindString(7,tasks.getSource());
        stat.executeInsert();
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }
    //插入
    public void update(long _id,Music tasks){
        String sql = "UPDATE "+TABLE_NAME+" SET "
                +NAME+"=?,"+SPECIAL+"=?,"
                +SINGER+"=?,"+CATEGORY+"=?,"
                +URL+"=?,"+LOCAl_PATH+"=?,"
                +" where "+_ID+"="+_id;
        SQLiteDatabase db = getReadableDatabase();
        SQLiteStatement stat = db.compileStatement(sql);

        db.beginTransaction();
        stat.bindString(1, tasks.getName());
        stat.bindString(2, tasks.getSpecial());
        stat.bindString(3, tasks.getSinger());
        stat.bindString(4, tasks.getCategory());
        stat.bindString(5, tasks.getUrl());
        stat.bindString(6,tasks.getLocalPath());
        stat.bindString(7,tasks.getSource());
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

    private Music trans(Cursor c){
        Music tasks = new Music();

        tasks.set_id(c.getLong(c.getColumnIndex(_ID)));
        tasks.setName(c.getString(c.getColumnIndex(NAME)));
        tasks.setSpecial(c.getString(c.getColumnIndex(SPECIAL)));
        tasks.setSinger(c.getString(c.getColumnIndex(SINGER)));
        tasks.setCategory(c.getString(c.getColumnIndex(CATEGORY)));
        tasks.setLocalPath(c.getString(c.getColumnIndex(LOCAl_PATH)));
        tasks.setUrl(c.getString(c.getColumnIndex(URL)));
        tasks.setSource(c.getString(c.getColumnIndex(SOURCE)));
        return tasks;
    }
}
