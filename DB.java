package player.com.roshkatian;

/**
 * Created by Дом on 18.12.2014.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DB {

    private static final String DB_NAME = "mydbPlv2";
    private static final int DB_VERSION = 1;

    // имя таблицы плейлистов, поля и запрос создания
    private static final String PLAYLIST_TABLE = "playlist";
    public static final String PLAYLIST_COLUMN_ID = "_id";
    public static final String PLAYLIST_COLUMN_TITLE = "title";
    public static final String PLAYLIST_COLUMN_TIME = "time";
    private static final String PLAYLIST_TABLE_CREATE = "create table "
            + PLAYLIST_TABLE + "("
            + PLAYLIST_COLUMN_ID + " integer primary key, "
            + PLAYLIST_COLUMN_TITLE + " text, "
            + PLAYLIST_COLUMN_TIME + " text"+ ");";

    // имя таблицы песен, поля и запрос создания
    private static final String SONG_TABLE = "song";
    public static final String SONG_COLUMN_ID = "_id";
    public static final String SONG_COLUMN_TITLE = "title";
    public static final String SONG_COLUMN_ARTIST = "artist";
    public static final String SONG_COLUMN_ISLOAD = "isload";
    public static final String SONG_COLUMN_LINK = "link";
    public static final String SONG_COLUMN_PLAYLIST = "plid";
    private static final String SONG_TABLE_CREATE = "create table "
            + SONG_TABLE + "("
            + SONG_COLUMN_ID + " integer primary key autoincrement, "
            + SONG_COLUMN_TITLE + " text, "
            + SONG_COLUMN_ARTIST + " text, "
            + SONG_COLUMN_ISLOAD + " integer, "
            + SONG_COLUMN_LINK + " text, "
            + SONG_COLUMN_PLAYLIST + " integer" + ");";

    private final Context mCtx;

    private DBHelper mDBHelper;
    private SQLiteDatabase mDB;

    public DB(Context ctx) {
        mCtx = ctx;
    }

    // открываем подключение
    public void open() {
        mDBHelper = new DBHelper(mCtx, DB_NAME, null, DB_VERSION);
        mDB = mDBHelper.getWritableDatabase();
    }

    // закрываем подключение
    public void close() {
        if (mDBHelper != null)
            mDBHelper.close();
    }

    // данные по плейлистам
    public Cursor getPlaylistData() {
        return mDB.query(PLAYLIST_TABLE, null, null, null, null, null, null);
    }

    // данные по песням конкретной группы
    public Cursor getSongData(long playlistID) {
        return mDB.query(SONG_TABLE, null, SONG_COLUMN_PLAYLIST + " = "
                + playlistID, null, null, null, null);
    }

    public Cursor getPlid(String plName) {
        String table = null;
        String[] columns = null;
        String selection = null;
        String[] selectionArgs = null;
        table = PLAYLIST_TABLE;
        columns = new String[] {"playlist._id as PlaylistID"};
        selection = "playlist.title = ?";
        selectionArgs = new String[]{plName} ;
        return mDB.query(table, columns, selection, selectionArgs, null, null, null);
    }
    public long addPlaylist(String title,String time){
        // подготовим данные для вставки в виде пар: наименование столбца - значение
        ContentValues cv = new ContentValues();
        cv.clear();
        cv.put("title", title);
        cv.put("time", time);
        // вставляем запись и получаем ее ID
        long rowID = mDB.insert(PLAYLIST_TABLE, null, cv);
        return rowID;
    }

    public ArrayList<String> convertMassiveCursorToString(Cursor c, String ColumnName){
        ArrayList<String> str = new ArrayList<String>();
        str.clear();
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    //str = "";
                    for (String cn : c.getColumnNames()) {
                        if  (cn.equals(ColumnName))
                        {
                            str.add(c.getString(c.getColumnIndex(cn)));
                        }
                    }
                 //   Log.d("myLog", str);
                } while (c.moveToNext());
            }
        } else
            Log.d("myLog", "Cursor is null");
            return str;
    }

    public int convertCursorToInt(Cursor c) {
        // удаляем по названию
        String str = "";
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    str = "";
                    for (String cn : c.getColumnNames()) {
                        str = c.getString(c.getColumnIndex(cn));
                    }
                    Log.d("myLog", str);
                } while (c.moveToNext());
            }
        } else
            Log.d("myLog", "Cursor is null");

        int plid = Integer.parseInt(str);
        return plid;
    }
    public boolean delPlaylist(String selectedPlaylist){
        // удаляем по названию
       Cursor c = getPlid(selectedPlaylist);
       int plid = convertCursorToInt(c);
       mDB.delete(SONG_TABLE,  SONG_COLUMN_PLAYLIST + " = " + plid, null);
       return  mDB.delete(PLAYLIST_TABLE,  PLAYLIST_COLUMN_TITLE + " = '" + selectedPlaylist + "'", null)>0;
    }

    public boolean updPlaylist(String selectedPlaylist, String title, String time) {
        // подготовим значения для обновления
        ContentValues cv = new ContentValues();
        cv.clear();
        cv.put("title", title);
        cv.put("time", time);
        return mDB.update(PLAYLIST_TABLE, cv, PLAYLIST_COLUMN_TITLE + " = '" + selectedPlaylist + "'", null)>0;
    }

    public long addSong( String artist, String title, int isload,String link, String plName){
        // подготовим данные для вставки в виде пар: наименование столбца - значение
        Cursor c = getPlid(plName);
        int plid = convertCursorToInt(c);

        ContentValues cv = new ContentValues();
        cv.clear();
        cv.put("title", title);
        cv.put("artist", artist);
        cv.put("isload", isload);
        cv.put("link", link);
        cv.put("plid", plid);
        // вставляем запись и получаем ее ID
        long rowID = mDB.insert(SONG_TABLE, null, cv);
        return rowID;
    }

    public boolean delSong(String selectedSong){
        // удаляем по названию
        return  mDB.delete(SONG_TABLE,  SONG_COLUMN_TITLE + " = '" + selectedSong + "'", null)>0;
    }
    public boolean updSong(String selectedSong, String artist, String title, String plName) {
        // подготовим значения для обновления
        Cursor c = getPlid(plName);
        int plid = convertCursorToInt(c);
        ContentValues cv = new ContentValues();
        cv.clear();
        cv.put("artist", artist);
        cv.put("title", title);
        //cv.put("isload", isload);
        //cv.put("link", link);
        cv.put("plid", plid);

        return mDB.update(SONG_TABLE, cv, SONG_COLUMN_TITLE + " = '" + selectedSong + "'", null)>0;
    }
    private class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context, String name, CursorFactory factory,
                        int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            // создаем таблицу плейлистов
            db.execSQL(PLAYLIST_TABLE_CREATE);
            // создаем таблицу песен
            db.execSQL(SONG_TABLE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }

}