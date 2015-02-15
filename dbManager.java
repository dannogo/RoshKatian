package player.com.roshkatian;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;

/**
 * Created by Дом on 21.01.2015.
 */
public class dbManager {
    DB db;
    Cursor cursor;
    Context context;
    public dbManager(Context c){
        // подключаемся к БД
        this.context = c;
        db = new DB(this.context);
        db.open();
    }

    ArrayList<String> getTitlePlaylists( ){
        ArrayList<String> pl;
        try {
            cursor = db.getPlaylistData();
            pl = db.convertMassiveCursorToString(cursor, DB.PLAYLIST_COLUMN_TITLE );
        }
        catch (Exception e){
            pl = null;
        }

        return pl;
    }

    ArrayList<String> getArtistTitleSong(String selPl){
        ArrayList<String> s = new ArrayList<String>();
        try {
            cursor = db.getPlid(selPl);
            int id = db.convertCursorToInt(cursor);
            cursor = db.getSongData(id);
            ArrayList<String>  s1 = db.convertMassiveCursorToString(cursor, DB.SONG_COLUMN_ARTIST );
            ArrayList<String>  s2 = db.convertMassiveCursorToString(cursor, DB.SONG_COLUMN_TITLE );
            for(int i=0; i<s1.size();i++){
                s.add(s1.get(i)+" - "+s2.get(i));
            }

        }
        catch (Exception e){
            s = null;
        }

        return s;
    }

    void addNewPlaylist(String title, String currentDateTime){
        db.addPlaylist(title,currentDateTime);
        cursor = db.getPlaylistData();
        cursor.requery();
    }

    void updSelectedPlaylist(String selectedPlaylist,String title, String currentDateTime){
        db.updPlaylist(selectedPlaylist,title,currentDateTime);
        cursor.requery();

    }
    void delSelectedPlaylist(Dialogs.UPDATECONTINUE IUpd, PlaylistHelper PlHelper, String selectedPlaylist){

        ArrayList<String> s = getArtistTitleSong(selectedPlaylist);
        for (int i=0;i<s.size();i++)
        {
            delSelectedSong(IUpd, PlHelper.SeparateAtristTitle(s.get(i).toString()), selectedPlaylist);
        }


        IUpd.continueUpdateSongs();
        db.delPlaylist(selectedPlaylist);
        cursor.requery();
        IUpd.continueUpdatePlaylist();

    }

    void addNewSongToPlaylist( Dialogs.UPDATECONTINUE IUpd, ArrayList<SongInformation> sInf, String selGS,String selPl) {
        for (int i = 0; i < sInf.size(); i++) {
            if (sInf.get(i).UniteAtristTitle().equals(selGS)) {
                SongInformation s = sInf.get(i);
                db.addSong(s.getArtist(), s.getSongTitle(), s.getSongIsLoad(), s.getSongLink(), selPl);
                cursor.requery();
            }
        }
        IUpd.continueUpdateSongs();
    }


    void updSelectedSong( String selS, String a, String t, String selPl){
      db.updSong(selS, a, t, selPl);
      cursor.requery();
    }

    void delSelectedSong(Dialogs.UPDATECONTINUE IUpd, String selS, String selPl){

        db.delSong(selS);
        Cursor c = db.getPlid(selPl);
        int plid = db.convertCursorToInt(c);
        cursor = db.getSongData(plid);

        cursor.requery();
        IUpd.continueUpdateSongs();
    }
    void close() {
        db.close();
    }
}
