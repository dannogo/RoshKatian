package player.com.roshkatian;

import android.content.Context;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Дом on 24.01.2015.
 */
public class PlaylistHelper {
    Context context;
    ArrayList<SongInformation>  sI;
    public  PlaylistHelper(Context context){
        this.context = context;

    }
    public ArrayList<SongInformation> createSongInformation(List<String> songsArtist,List<String> songsTitle, List<Integer> songsIsLoad,List<String> songsLink ) {
        ArrayList<SongInformation> sI = new ArrayList<SongInformation>();
        for (int i = 0; i < songsTitle.size(); i++) {
            sI.add(new SongInformation());
            sI.get(i).setArtist(songsArtist.get(i));
            sI.get(i).setSongTitle(songsTitle.get(i));
            sI.get(i).setSongIsLoad(songsIsLoad.get(i));
            sI.get(i).setSongLink(songsLink.get(i));
        }
        this.sI = sI;
        return this.sI;
    }
    public boolean Existing(int obj, String txtEror){
        boolean flag = true;
        if (obj==-1) {
            if (!txtEror.equals("")) {
                Toast.makeText(context, txtEror, Toast.LENGTH_SHORT).show();
            }
            flag = false;
        }
        return flag;
    }

    public String SeparateAtristTitle(String s){
        String title;
        title = "";
        for(int i=0; i<s.length();i++)
        {
            if (s.charAt(i) == '-'){

                title = (s.substring(i + 2));
            }
        }
        return title;
    }

    //Создаю массив "Артист - Название песни" для передачи в адаптер ListView песен жанра исключительно для отображения
    public  ArrayList<String> CreateListAtristTitle() {
        ArrayList<String> ArT = new ArrayList<String>();
        for(SongInformation s: this.sI){
            ArT.add(s.UniteAtristTitle());
        }
        return ArT;
    }
}
