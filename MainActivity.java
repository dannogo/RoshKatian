package player.com.roshkatian;

import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.SparseArray;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends ActionBarActivity implements FragmentB.Communicator,  Dialogs.UPDATECONTINUE, FragmentC.Communicator {
    private FragmentB.Communicator fragmentBCommunicator;
    private FragmentC.Communicator fragmentCCommunicator;
    private Dialogs.UPDATECONTINUE dialogsUpdateContinue;

    boolean isFragmentCExists = false;
//    boolean isFragmentCAlreadyRan = false;
    boolean isAfterFragmentC = false;

    Point size;

    @Override
    public void respondB() {
        FragmentB fragB = (FragmentB) pagerAdapter.getRegisteredFragment(1);
        fragB.refreshList();
    }

    @Override
    public void respondC() {
        FragmentC fragC = (FragmentC) pagerAdapter.getRegisteredFragment(2);
        fragC.getPlaylistSongs(selectedPlaylist);
        isAfterFragmentC = true;
        isFragmentCExists = true;
    }

    @Override
    public void continueUpdatePlaylist() {
        playlists = UpdatePlaylistInformation();
    }

    public ArrayList<String> UpdatePlaylistInformation(){
        ArrayList<String> pl;
        pl = dbM. getTitlePlaylists();
//        Toast.makeText(this, ""+pl.size(), Toast.LENGTH_SHORT).show();
        if(readyForIconUpdate) {
            fragB.updateIcons(pl.size());
        }
        readyForIconUpdate = true;
        return pl;
    }

    @Override
    public void continueUpdateSongs() {
        playlistSongs = UpdateSongInformation();
    }

    public ArrayList<String> UpdateSongInformation(){
        ArrayList<String> s;
        String selectedPlaylist="";

        if (PlHelper.Existing(selectedPlaylistPos,"")){
            selectedPlaylist = playlists.get(selectedPlaylistPos).toString();
        }
        s = dbM.getArtistTitleSong(selectedPlaylist);
        // Здесь обновить отображение песен в плейлисте
//        Toast.makeText(this, "s: "+ s, Toast.LENGTH_SHORT).show();
        return s;
    }

    MyAdapter pagerAdapter;
    boolean readyForIconUpdate = false;
    boolean isFirstTimeRan;
    ViewPager viewPager=null;
    FragmentB fragB;
    FragmentC fragC;
    String selectedPlaylist;
    String activePlaylist;
    RotateAnimation rotateAnimation;



    //---- For database UNDER -----

    final int MENU_ADD_PLAYLIST = 1;
    final int MENU_DEL_PLAYLIST = 2;
    final int MENU_UPD_PLAYLIST = 3;
    final int MENU_ADD_SONG = 4;
    final int MENU_DEL_SONG = 5;
    final int MENU_UPD_SONG = 6;

    public static final int DIALOG_ADD_PLAYLIST = 11;
    public static final int DIALOG_UPD_PLAYLIST = 12;
    public static final int DIALOG_UPD_SONG = 13;

    private final String CHOOSE_PLAYLIST = "Playlist isn't selected";
    protected final String CHOOSE_SONG = "Song isn't selected";

    Context MainContext;
    Dialogs.UPDATECONTINUE IMainUpd;
    ArrayList<SongInformation> songInfo;

    ListView lvGenreSongs;
    ListView lvPl;
    ListView lvSong;
    dbManager dbM;
    PlaylistHelper PlHelper;
    String selectedGenreSong;

    int selectedPlaylistPos = -1;
    int selectedSongPos = -1;

    //Главные выходные данные: массив наименований плелистов, массив песен выбранного плейлиста
    ArrayList<String> playlists;
    ArrayList<String> playlistSongs;
    FloatingActionButton editPlaylistButton;

    String selectedPL;
    //---- For database ABOVE -----
    

    // -------- For parsing UNDER

    private ProgressDialog pd;

    List<String> genrePath = new ArrayList<String>();
    List<String> genreName = new ArrayList<String>();
    List<String> songsArtist = new ArrayList<String>();
    List<String> songsTitle = new ArrayList<String>();
    List<String> songsLink = new ArrayList<String>();
    List<Integer> songsIsLoad = new ArrayList<Integer>();

//    HashMap<Integer, Float[]> positions = new HashMap<>();

    Stor stor = new Stor();
    SharedPreferences mPrefs;
    SharedPreferences.Editor editor;
    Gson gson = new Gson();

    int idOfCurrentActivePlaylist;


    HashMap<String, ArrayList<List<String>>> temporaryStorage = new HashMap<>();

    public void refreshTemporaryStorage(){

        ArrayList<String> TitleForStorage= new ArrayList<>();
        for (int i=0; i<songsTitle.size(); i++){
            TitleForStorage.add(songsTitle.get(i));
        }

        ArrayList<String> ArtistForStorage= new ArrayList<>();
        for (int i=0; i<songsTitle.size(); i++){
            ArtistForStorage.add(songsArtist.get(i));
        }

        ArrayList<String> LinkForStorage= new ArrayList<>();
        for (int i=0; i<songsTitle.size(); i++){
            LinkForStorage.add(songsLink.get(i));
        }

        ArrayList<List<String>> convertingArray = new ArrayList<List<String>>();
        convertingArray.add(TitleForStorage);
        convertingArray.add(ArtistForStorage);
        convertingArray.add(LinkForStorage);

        temporaryStorage.put(selectedGenre, convertingArray);
    }

    String selectedGenre = "String";
    // -------- For parsing ABOVE



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Graphics.setGraphics();

        Display display = getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size);

//        editPlaylistButton = new FloatingActionButton.Builder(this)
//                .withDrawable(getResources().getDrawable(R.drawable.edit32))
//                .withButtonColor(Graphics.katianColors.get(6))
//                .withGravity(Gravity.BOTTOM | Gravity.RIGHT)
//                .withMargins(0, 0, 16, 16)
//                .withButtonSize(90)
//                .create();
//
//
//
//        editPlaylistButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FragmentManager fM = getSupportFragmentManager();
////                android.support.v4.app.FragmentTransaction fT = fM.beginTransaction();
////                fT.hide
//                MyAdapter myAdap = new MyAdapter(fM);
//                myAdap.change(2);
//                viewPager.setCurrentItem(1);
//                dbM.delSelectedPlaylist(IMainUpd, PlHelper, activePlaylist);
//                isFragmentCExists = false;
//
////                mainActivity.
//                Toast.makeText(getApplicationContext(), "Ха-Ха", Toast.LENGTH_SHORT).show();
//            }
//        });


        activePlaylist = "";

        rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, .5f, Animation.RELATIVE_TO_SELF, .5f);
        rotateAnimation.setDuration(700);

        AnimationSet setAnimation = new AnimationSet(true);
        setAnimation.addAnimation(rotateAnimation);

        isFirstTimeRan = true;

        setContentView(R.layout.activity_main);
        viewPager = (ViewPager) findViewById(R.id.pager);
        FragmentManager fragmentManager = getSupportFragmentManager();
        pagerAdapter = new MyAdapter((fragmentManager));
        viewPager.setAdapter(pagerAdapter);
        PlHelper = new PlaylistHelper(this);

        MainContext = this;
        IMainUpd = this;

        dbM = new dbManager(this);

        playlists = new ArrayList<String>();
        playlists = UpdatePlaylistInformation();

        mPrefs = getSharedPreferences("RoshKatian", MODE_PRIVATE);
        editor = mPrefs.edit();

//        editor.remove("iconPositions");
//        editor.commit();
//        dbM.context.deleteDatabase("mydbPlv2");

        if (mPrefs.contains("iconPositions")){
            stor = gson.fromJson(mPrefs.getString("iconPositions",""), Stor.class);
//            Toast.makeText(this, "+ there is data", Toast.LENGTH_LONG).show();
        }else{
//            Toast.makeText(this, "- no data", Toast.LENGTH_SHORT).show();
        }


//        fragC = (FragmentC) pagerAdapter.getRegisteredFragment(2);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position)
            {
                fragB = (FragmentB) pagerAdapter.getRegisteredFragment(1);
                fragC = (FragmentC) pagerAdapter.getRegisteredFragment(2);
                if(position == 0){
                    setTitle("300kbit.ru");
                }
                if(position == 1){
                    // Удаляет иконку edit фрагмента С
                    if (isAfterFragmentC && isAfterFragmentC) {
                        View editB = findViewById(R.id.edit_button);
                        if (editB != null) {
                            ((ViewGroup) editB.getParent()).removeView(editB);
                            fragC.editPlaylistButton.setVisibility(View.INVISIBLE);
                            fragC.editPlaylistButton = null;
                        }else{

//                        Toast.makeText(getApplicationContext(), "editButton is null", Toast.LENGTH_SHORT).show();
                        }
                    }
                    isAfterFragmentC = false;
//                    if(isFragmentCExists && isAfterFragmentC) {
//                    if(isFragmentCExists) {
//                        fragC.editPlaylistButton.setVisibility(View.INVISIBLE);
//                    }
                    setTitle(selectedGenre);
//                    fragC.editPlaylistButton.setVisibility(View.INVISIBLE);
                    for (int i=0; i<fragB.katians.size(); i++){
                        // Если конкретный плейлист не скрыт насильно - сделать его видимым
                        if (fragB.katians.get(i).getTag().equals("VISIBLE")){
                            fragB.katians.get(i).setVisibility(View.VISIBLE);
                        }
//                        else if (fragB.katians.get(i).getTag().equals("INVISIBLE")){
//                            fragB.katians.get(i).setVisibility(View.INVISIBLE);
//                        }else{
//                            Toast.makeText()
//                        }

                    }
//                    if (!isFragmentCExists && isFragmentCAlreadyRan){
//                        fragC = (FragmentC) pagerAdapter.getRegisteredFragment(2);
//                        fragC.removeFragment();
//                        Toast.makeText(getApplicationContext(), "if сработал", Toast.LENGTH_SHORT).show();
//                    }
                }else{
                    for (int i=0; i<fragB.katians.size(); i++){
                        fragB.katians.get(i).setVisibility(View.INVISIBLE);
                    }
                }
                if(position == 2){
//                    isFragmentCAlreadyRan = true;
                    setTitle(activePlaylist);
                    fragC.createEditPlaylistButton();
//                    editPlaylistButton.setVisibility(View.VISIBLE);
//                    fragC.editPlaylistButton.setVisibility(View.VISIBLE);
                }else{

                }

            }
            @Override
            public void onPageScrollStateChanged(int state)
            {
            }
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.add(0, 1, 0, "Добавить плейлист");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        String selectedPlaylist = "";
        String selectedSong = "";

//        if (PlHelper.Existing(selectedPlaylistPos,""))
//        {
//            selectedPlaylist = playlists.get(selectedPlaylistPos).toString();
//            if (PlHelper.Existing(selectedSongPos,""))
//            {
//                selectedSong = playlistSongs.get(selectedSongPos).toString();
//            }
//        }

        switch(id) {
            case MENU_ADD_PLAYLIST: {
                new Dialogs(MainContext, IMainUpd, dbM, DIALOG_ADD_PLAYLIST, selectedSong, selectedPlaylist).show();
                selectedPlaylistPos = -1;
//                Toast.makeText(this, ""+playlists.size(), Toast.LENGTH_SHORT).show();
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

}


class MyAdapter extends FragmentPagerAdapter implements FragmentA.changeFragmentCountCallback, FragmentB.changeFragmentCountCallback, FragmentC.changeFragmentCountCallback {
    private FragmentA.changeFragmentCountCallback fragACFCC;
    private FragmentB.changeFragmentCountCallback fragBCFCC;
    private FragmentC.changeFragmentCountCallback fragCCFCC;

    int fragmentQuantity = 1;
    SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();
    private long baseId = 0;



    MyAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        Fragment fragment = null;

        if(i==0){
            fragment = new FragmentA();
            ((FragmentA)fragment).myAdapter = this;
        }
        if(i==1){
            fragment = new FragmentB();
            ((FragmentB)fragment).myAdapter = this;
        }
        if(i==2){
            fragment = new FragmentC();
            ((FragmentC)fragment).myAdapter = this;
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return fragmentQuantity;
    }

    @Override
    public void change(int delta) {
        fragmentQuantity = delta;
        notifyDataSetChanged();
    }



    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container,position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }
}