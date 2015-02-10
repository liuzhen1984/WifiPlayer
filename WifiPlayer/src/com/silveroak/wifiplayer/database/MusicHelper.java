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
 * Created by  on 14-6-11.
 *
 *   private String songName;
 private String artistName;
 private String albumName;
 private String songPicSmall;
 private String songPicBig;
 private String songPicRadio;
 private String lrcLink;
 private String songLink;
 private String format;
 private int rate;
 private int size;
 private String relateStatus;
 private String resourceType;
 */
public class MusicHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "wifi_player_db";
    private static final int DB_VERSION = 1;

    private static boolean isCreate = false;

    private static final String TABLE_NAME = "music";
    private static final String INDEX_NAME = "index_music";
    private static final String _ID = "_id";
    private static final String SONG_NAME = "song_name";
    private static final String ARTIST_NAME = "artist_name";
    private static final String ALBUM_NAME = "album_name";
    private static final String SONG_PIC_SMALL = "song_pic_small";
    private static final String SONG_PIC_BIG = "song_pic_big";
    private static final String SONG_PIC_RADIO = "song_pic_radio";
    private static final String LRC_LINK = "lrc_link";
    private static final String SONG_LINK = "song_link";
    private static final String FORMAT = "format";
    private static final String RATE = "rate";
    private static final String SIZE = "size";
    private static final String RELATE_STATUS = "relate_status";
    private static final String RESOURCE_TYPE = "resource_type";

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
     private static final String TABLE_NAME = "music";
     private static final String INDEX_NAME = "index_music";
     private static final String _ID = "_id";
     private static final String SONG_NAME = "song_name";
     private static final String ARTIST_NAME = "artist_name";
     private static final String ALBUM_NAME = "album_name";
     private static final String SONG_PIC_SMALL = "song_pic_small";
     private static final String SONG_PIC_BIG = "song_pic_big";
     private static final String SONG_PIC_RADIO = "song_pic_radio";
     private static final String LRC_LINK = "lrc_link";
     private static final String SONG_LINK = "song_link";
     private static final String FORMAT = "format";
     private static final String RATE = "rate";
     private static final String SIZE = "size";
     private static final String RELATE_STATUS = "relate_status";
     private static final String RESOURCE_TYPE = "resource_type";
     * @param db
     */
    private void createTableAndIndex(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + SONG_NAME + " varchar(50), "
                + ARTIST_NAME + " varchar(50), "
                + ALBUM_NAME + " varchar(50), "
                + SONG_PIC_SMALL + " varchar(255) ,"
                + SONG_PIC_BIG + " varchar(255) ,"
                + SONG_PIC_RADIO + " varchar(255) , "
                + LRC_LINK + " varchar(255) , "
                + SONG_LINK + " varchar(255) , "
                + FORMAT + " varchar(50) , "
                + RATE + " int(11) , "
                + SIZE + " int(1) , "
                + RELATE_STATUS + " varchar(50) ,"
                + RESOURCE_TYPE + " varchar(50) "
                + ");");
        db.execSQL("CREATE INDEX IF NOT EXISTS " + INDEX_NAME + " ON " + TABLE_NAME + " ("
                + SONG_LINK + ");");
        isCreate = true;

    }

    //查询
    public List<Music> queryFromSpecial(String special) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = null;
        List<Music> musicAPIs = new ArrayList<Music>();
        try {

            if (db != null) {
                c = db.rawQuery("SELECT * FROM " + TABLE_NAME +  " where "+ALBUM_NAME+"="+special+";",new String[0]);
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
                c = db.rawQuery("SELECT * FROM " + TABLE_NAME +  " where "+ARTIST_NAME+"="+singer+";",new String[0]);
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
                c = db.rawQuery("SELECT * FROM " + TABLE_NAME +  " where "+SONG_LINK+"=\""+url +"\";",new String[0]);
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
     private static final String TABLE_NAME = "music";
     private static final String INDEX_NAME = "index_music";
     private static final String _ID = "_id";
     private static final String SONG_NAME = "song_name";
     private static final String ARTIST_NAME = "artist_name";
     private static final String ALBUM_NAME = "album_name";
     private static final String SONG_PIC_SMALL = "song_pic_small";
     private static final String SONG_PIC_BIG = "song_pic_big";
     private static final String SONG_PIC_RADIO = "song_pic_radio";
     private static final String LRC_LINK = "lrc_link";
     private static final String SONG_LINK = "song_link";
     private static final String FORMAT = "format";
     private static final String RATE = "rate";
     private static final String SIZE = "size";
     private static final String RELATE_STATUS = "relate_status";
     private static final String RESOURCE_TYPE = "resource_type";
     * @param tasks
     */
    //插入
    public void insert(Music tasks){
        String sql = "insert into "+TABLE_NAME+"("
                +SONG_NAME+","
                +ARTIST_NAME+","
                +ALBUM_NAME+","
                +SONG_PIC_SMALL+","
                +SONG_PIC_BIG+","
                +SONG_PIC_RADIO+","
                +LRC_LINK+","
                +SONG_LINK+","
                +FORMAT+","
                +RATE+","
                +SIZE+","
                +RELATE_STATUS+","
                +RESOURCE_TYPE
                + ") values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
        SQLiteDatabase db = getReadableDatabase();
        SQLiteStatement stat = db.compileStatement(sql);

        db.beginTransaction();
        stat.bindString(1, tasks.getSongName());
        stat.bindString(2, tasks.getArtistName());
        stat.bindString(3, tasks.getAlbumName());
        stat.bindString(4, tasks.getSongPicSmall());
        stat.bindString(5, tasks.getSongPicBig());
        stat.bindString(6, tasks.getSongPicRadio());
        stat.bindString(7,tasks.getLrcLink());
        stat.bindString(8,tasks.getSongLink());
        stat.bindString(9,tasks.getFormat()==null?"":tasks.getFormat());
        stat.bindLong(10, tasks.getRate());
        stat.bindLong(11, tasks.getSize());
        stat.bindString(12,tasks.getRelateStatus());
        stat.bindString(13,tasks.getResourceType());
        stat.executeInsert();
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    //
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
        tasks.setSongName(c.getString(c.getColumnIndex(SONG_NAME)));
        tasks.setArtistName(c.getString(c.getColumnIndex(ARTIST_NAME)));
        tasks.setAlbumName(c.getString(c.getColumnIndex(ALBUM_NAME)));
        tasks.setSongPicSmall(c.getString(c.getColumnIndex(SONG_PIC_SMALL)));
        tasks.setSongPicBig(c.getString(c.getColumnIndex(SONG_PIC_BIG)));
        tasks.setSongPicRadio(c.getString(c.getColumnIndex(SONG_PIC_RADIO)));
        tasks.setLrcLink(c.getString(c.getColumnIndex(LRC_LINK)));
        tasks.setSongLink(c.getString(c.getColumnIndex(SONG_LINK)));
        tasks.setFormat(c.getString(c.getColumnIndex(FORMAT)));
        tasks.setRate(c.getInt(c.getColumnIndex(RATE)));
        tasks.setSize(c.getInt(c.getColumnIndex(SIZE)));
        tasks.setRelateStatus(c.getString(c.getColumnIndex(RELATE_STATUS)));
        tasks.setResourceType(c.getString(c.getColumnIndex(RESOURCE_TYPE)));
        return tasks;
    }
}
