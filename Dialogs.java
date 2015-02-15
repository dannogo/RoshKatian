package player.com.roshkatian;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Date;

/**
 * Created by Дом on 22.01.2015.
 */
public class Dialogs {
    /*private static final int DIALOG_ADD_PLAYLIST = 11;
    private static final int DIALOG_UPD_PLAYLIST = 12;
    private static final int DIALOG_UPD_SONG = 13;
*/
    int id;
    Context context;
    dbManager dbM;
    String selectedSong;
    String selectedPlaylist;
    UPDATECONTINUE UpdateContinue;
    public interface UPDATECONTINUE{
        public void continueUpdatePlaylist();

        public void continueUpdateSongs();
    }
    public Dialogs(Context context, final UPDATECONTINUE UpdateContinue, final dbManager dbM, final int id, final String selectedSong, final String selectedPlaylist) {

        this.id = id;
        this.context = context;
        this.dbM = dbM;
        this.selectedSong = selectedSong;
        this.selectedPlaylist = selectedPlaylist;
        this.UpdateContinue = UpdateContinue;
    }
    public void show(){
        switch (id) {
            case MainActivity.DIALOG_ADD_PLAYLIST:
            case MainActivity.DIALOG_UPD_PLAYLIST: {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater = LayoutInflater.from(context);//getLayoutInflater();
                builder.setMessage("Enter a title of the playlist:");
                View layout = inflater.inflate(R.layout.item_pl, null);
                builder.setView(layout);
                final EditText edTitlePl = (EditText) layout.findViewById(R.id.edTitlePl);

                builder.setCancelable(true);


                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    String currentDateTime;

                    public void onClick(DialogInterface dialog, int which) {
                        String title = edTitlePl.getText().toString();
                        currentDateTime = (String) DateFormat.format("yyyy-MM-dd kk:mm:ss", new Date());
                        switch (id) {
                            case MainActivity.DIALOG_ADD_PLAYLIST: {
                                dbM.addNewPlaylist(title, currentDateTime);
                                break;
                            }
                            case MainActivity.DIALOG_UPD_PLAYLIST: {
                                dbM.updSelectedPlaylist(selectedPlaylist, title, currentDateTime);
                            }
                        }
                        UpdateContinue.continueUpdatePlaylist();
                        UpdateContinue.continueUpdateSongs();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
                break;
            }
            case MainActivity.DIALOG_UPD_SONG: {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater = LayoutInflater.from(context);
                builder.setMessage("Enter a title of the playlist:");
                View layout = inflater.inflate(R.layout.item_s, null);
                builder.setView(layout);
                final EditText edTitleSong = (EditText) layout.findViewById(R.id.edTitleSong);
                final EditText edArtist = (EditText) layout.findViewById(R.id.edArtist);
                builder.setCancelable(true);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String artist = edArtist.getText().toString();
                        String title = edTitleSong.getText().toString();
                        dbM.updSelectedSong(selectedSong, artist, title, selectedPlaylist);
                        UpdateContinue.continueUpdateSongs();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
                break;
            }
        }
    }
}
