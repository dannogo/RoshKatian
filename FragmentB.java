package player.com.roshkatian;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Display;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import player.com.roshkatian.draglist.ImageHelper;

/**
 * Created by oleh on 1/9/15.
 */
public class FragmentB extends Fragment {

    RelativeLayout relativeLayout;
    ImageView iconPlay;
    ImageView iconHide;


    LinearLayout playlistLaunchBar;
//    RotateAnimation rotateAnimation;

    DragSortListView list;
    private ArrayAdapter<String> adapter;

//    ArrayList<KatianFloat> katians;
    ArrayList<String> songsAuthors;

    int playListQuantity = 0;
    MainActivity mainActivity;
    String[] banderol = new String[4];
    View.OnDragListener drop;
    View.OnTouchListener touchFAB;

    ViewPager viewPager;
    ArrayList<String> playlists;
    FragmentC.Communicator comm;

    String[] fakeList = {"http://s1.300kbit.ru/music/Dami%20Im%20-%20Gladiator%20(7th%20Heaven%20Pop%20Dance%20Radio%20Mix).mp3",
                        "http://s1.300kbit.ru/music/Ola%20-%20This%20Could%20Be%20Paradise.mp3",
                        "http://s1.300kbit.ru/music/Darude%20Feat.%20Jo%20Angel%20-%20In%20The%20Darkness%20(Affecting%20Noise%20Remix).mp3",
                        "http://s1.300kbit.ru/music/Robin%20Schulz%20-%20Sun%20Goes%20Down%20%20(ft.%20Jasmine%20Thompson).mp3",
                        "http://s1.300kbit.ru/music/MaxiGroove%20-%20Come%20On%20(Radio%20Mix).mp3",
                        "http://s1.300kbit.ru/music/DJ%20Deka%20feat.%20Eniko%20-%20Szabaditsd%20Fel%20(Video%20Mix).mp3"};
    int cnt = 0;



//    HashMap<Integer, Float[]> positions = new HashMap<>();

    public interface changeFragmentCountCallback{
        void change(int delta);
    }

    public changeFragmentCountCallback myAdapter;


    private DragSortListView.DropListener onDrop =
            new DragSortListView.DropListener() {
                @Override
                public void drop(int from, int to) {
                    if (from != to) {
                        DragSortListView list = (DragSortListView) getActivity().findViewById(R.id.list);
                        String item = adapter.getItem(from);
                        adapter.remove(item);
                        adapter.insert(item, to);
                        list.moveCheckState(from, to);
                        Log.d("DSLV", "Selected item is " + list.getCheckedItemPosition());
                    }
                }
            };

    public void refreshList(){

        mainActivity = (MainActivity) getActivity();
        songsAuthors.clear();
        for(int i=0; i<mainActivity.songsTitle.size(); i++){
            songsAuthors.add(mainActivity.songsArtist.get(i)+" - "+ mainActivity.songsTitle.get(i));
        }

        for(String el:mainActivity.songsTitle){
            mainActivity.songsIsLoad.add(0);
        }
        mainActivity.songInfo = mainActivity.PlHelper.createSongInformation(mainActivity.songsArtist, mainActivity.songsTitle, mainActivity.songsIsLoad, mainActivity.songsLink);
        adapter.notifyDataSetChanged();

    }

    interface Communicator{
        public void respondB();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_b, container, false);


    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        comm = (FragmentC.Communicator) activity;
    }

    boolean enoughLoading = false;
    WebView webView;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewPager = (ViewPager) getActivity().findViewById(R.id.pager);

        webView =(WebView) getActivity().findViewById(R.id.webView1);
        webView.getSettings().setJavaScriptEnabled(true);
//        mainActivity.wv.getSettings().setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
                if (!enoughLoading){
                    Log.i("LOG", "OnLoad: " + webView.getUrl());
//                        if (webView.getUrl().contains("http://s1.300kbit.ru")){
                    if (webView.getUrl().contains("http://s1.300kbit.ru")){
                        enoughLoading = true;

//                            mainActivity.p = new Player(getActivity(), webView.getUrl());
                        mainActivity.p = new Player(getActivity(), webView.getUrl());
                    }
                }



            }
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url){
                view.loadUrl(url);
                return true;
            }
        });

        list = (DragSortListView) getActivity().findViewById(R.id.list);
        relativeLayout= (RelativeLayout) getActivity().findViewById(R.id.main_of_fragment_b);


        mainActivity = (MainActivity) getActivity();

        songsAuthors = new ArrayList<String>();
        for(int i=0; i<mainActivity.songsTitle.size(); i++){

            songsAuthors.add(mainActivity.songsArtist.get(i)+" - "+ mainActivity.songsTitle.get(i));
            Log.i("LOG", songsAuthors.get(i));
        }

        // Related to DragSortList UNDER

        adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_radio, R.id.text, songsAuthors);

        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//                final WebView wvt = new WebView(getActivity());
//                wvt.getSettings().setJavaScriptEnabled(true);
//                wvt.setWebViewClient(new WebViewClient(){
//                    @Override
//                    public void onLoadResource(WebView view, String url) {
//                        super.onLoadResource(view, url);
//                        if (!enoughLoading){
//                            Log.i("LOG", "OnLoad: " + wvt.getUrl());
////                        if (webView.getUrl().contains("http://s1.300kbit.ru")){
//                            if (wvt.getUrl().contains("http://s1.300kbit.ru")){
//                                enoughLoading = true;
//
//                                mainActivity.p = new Player(getActivity(), wvt.getUrl());
////                                mainActivity.p = new Player(getActivity(), webView.getUrl());
//                            }
//                        }
//
//
//
//                    }
//                    @Override
//                    public boolean shouldOverrideUrlLoading(WebView view, String url){
//                        view.loadUrl(url);
//                        return true;
//                    }
//                });



//                mainActivity.p = new Player(getActivity(), fakeList[cnt]);
//                cnt++;

                enoughLoading = false;

//                webView = null;
//                webView =(WebView) getActivity().findViewById(R.id.webView1);
//                webView.getSettings().setJavaScriptEnabled(true);
//                webView.setWebViewClient(new WebViewClient(){
//                    @Override
//                    public void onLoadResource(WebView view, String url) {
//                        super.onLoadResource(view, url);
//                        if (!enoughLoading){
//                            Log.i("LOG", "OnLoad: " + webView.getUrl());
////                        if (webView.getUrl().contains("http://s1.300kbit.ru")){
//                            if (webView.getUrl().contains("http://s1.300kbit.ru")){
//                                enoughLoading = true;
//
//                            mainActivity.p = new Player(getActivity(), webView.getUrl());
////                                mainActivity.p = new Player(getActivity(), webView.getUrl());
//                            }
//                        }
//
//
//
//                    }
//                    @Override
//                    public boolean shouldOverrideUrlLoading(WebView view, String url){
//                        view.loadUrl(url);
//                        return true;
//                    }
//                });
////                mainActivity.p = null;
                webView.loadUrl(mainActivity.songsLink.get(position));

            }
        });

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                ViewGroup viewGroup = (ViewGroup) view;
                View linearLayout = ((ViewGroup)view).getChildAt(0);
                View maybeTextView = ((ViewGroup) linearLayout).getChildAt(1);
                TextView text = (TextView) maybeTextView;
                view.setTag(text.getText());
                view.startDrag(null, shadowBuilder, view, 0);
                view.setVisibility(View.INVISIBLE);
                mainActivity.selectedSongPos = position;
//                mainActivity.playlistSongs = mainActivity.UpdateSongInformation();
                mainActivity.selectedGenreSong = (String) text.getText();
                return true;
            }
        });


        list.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                if (event.getAction() == DragEvent.ACTION_DRAG_ENDED){
                    View view = (View) event.getLocalState();
                    v.setVisibility(View.VISIBLE);
                }

                return false;
            }
        });

        list.setDropListener(onDrop);
        list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);




        // Related to DragSortList ABOVE


//        katians= new ArrayList<KatianFloat>();

//        ArrayList<Integer> katianIcons = new ArrayList<Integer>();
//
//        for(int i=0; i<15; i++){
//            katianIcons.add(R.drawable.p1+i);
//        }
//


        iconPlay = (ImageView) getActivity().findViewById(R.id.iconPlay);
        iconHide = (ImageView) getActivity().findViewById(R.id.iconHide);

        playlistLaunchBar = (LinearLayout) getActivity().findViewById(R.id.playlist_launch_bar);

        View.OnDragListener dropToPlay = new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                if(event.getAction() == DragEvent.ACTION_DRAG_ENTERED){
                    v.startAnimation(mainActivity.rotateAnimation);
                }
                if(event.getAction() == DragEvent.ACTION_DROP){
                    View view = (View) event.getLocalState();
                    view.setVisibility(View.VISIBLE);
                    playlistLaunchBar.setVisibility(View.INVISIBLE);

                    myAdapter.change(3);
                    viewPager.setCurrentItem(2);
                    playlists = mainActivity.dbM.getTitlePlaylists();
//                    mainActivity.selectedPL = playlists.get(view.getId());
//                    mainActivity.selectedPlaylist = playlists.get(view.getId());

                    mainActivity.selectedPlaylistPos = view.getId();

                    mainActivity.selectedPlaylist = playlists.get(mainActivity.selectedPlaylistPos).toString();
//                    Toast.makeText(getActivity(), "Playlist: " + playlists.get(view.getId()), Toast.LENGTH_SHORT).show();
                    mainActivity.activePlaylist = playlists.get(mainActivity.selectedPlaylistPos).toString();
//                    mainActivity.fragC.getPlaylistSongs(mainActivity.selectedPlaylist);
                    mainActivity.setTitle(mainActivity.activePlaylist);
                    mainActivity.getSupportActionBar().setIcon(Graphics.katianIcons.get(5));

                    mainActivity.idOfCurrentActivePlaylist = view.getId();
//                    mainActivity.tempList = mainActivity.dbM.getArtistTitleSong(mainActivity.selectedPlaylist);
                    comm.respondC();
//                    mainActivity.fragC.adapter.notifyDataSetChanged();
//                    Toast.makeText(getActivity(), "selectedPlaylist: " + mainActivity.selectedPlaylist, Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        };


        View.OnDragListener dropToHide = new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                View view = (View) event.getLocalState();
                if(event.getAction() == DragEvent.ACTION_DRAG_ENTERED){
                    v.startAnimation(mainActivity.rotateAnimation);
                }
                if(event.getAction() == DragEvent.ACTION_DROP){
                    playlistLaunchBar.setVisibility(View.INVISIBLE);

                    view.setTag("INVISIBLE");
                    view.setVisibility(View.INVISIBLE);
                    mainActivity.stor.putVis(view.getId(), "INVISIBLE");
                    mainActivity.editor.putString("iconPositions", mainActivity.gson.toJson(mainActivity.stor));
                    mainActivity.editor.commit();
//                    Toast.makeText(getActivity(), "Hide. Tag: "+view.getTag(), Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        };


        View.OnDragListener dropToReplace = new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                View view = (View) event.getLocalState();
                if(event.getAction() == DragEvent.ACTION_DROP){


                    if(view instanceof KatianFloat){

                        float posX = event.getX()-view.getWidth()/2;
                        float posY = event.getY()-view.getHeight()/2;
                        view.setX(posX);
                        view.setY(posY);

                        mainActivity.stor.put(view.getId(), posX, posY);
                        mainActivity.editor.putString("iconPositions", mainActivity.gson.toJson(mainActivity.stor));
                        mainActivity.editor.commit();

//                        Toast.makeText(getActivity(), "" +mainActivity.mPrefs.getString("iconPositions", "Any data"), Toast.LENGTH_SHORT).show();

                        playlistLaunchBar.setVisibility(View.INVISIBLE);
                    }else{

                    }
                    view.setVisibility(View.VISIBLE);

                }
                return true;
            }
        };

        iconPlay.setOnDragListener(dropToPlay);
        iconHide.setOnDragListener(dropToHide);
        relativeLayout.setOnDragListener(dropToReplace);

        touchFAB = new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                    {

                        v.setVisibility(View.INVISIBLE);
                        playlistLaunchBar.setVisibility(View.VISIBLE);

                        View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                        ClipData.Item item = new ClipData.Item((CharSequence) v.getTag());
                        String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};
                        ClipData dragData = new ClipData(v.getTag().toString(), mimeTypes, item);

                        v.startDrag(null, shadowBuilder, v, 0);

//                        Toast.makeText(getActivity(), "id: "+v.getId(), Toast.LENGTH_SHORT).show();

//                        v.startDrag(dragData, shadowBuilder, null, 0);

                        return true;
                    }
                    case MotionEvent.ACTION_UP:
                    {

                        playlistLaunchBar.setVisibility(View.INVISIBLE);
                        v.setVisibility(View.VISIBLE);



                        return true;
                    }
                }
                return false;
            }
        };

        drop = new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                if (event.getAction() == DragEvent.ACTION_DRAG_ENTERED) {


                }
                if (event.getAction() == DragEvent.ACTION_DROP) {
                    View view = (View) event.getLocalState();
                    view.setVisibility(View.VISIBLE);
                    // Тут вызвать метод добавления песни в соответствующий плейлист
//                    String[] songsAndArtistsSeparated = view.getTag().toString().split(" - ");

//                    String selectedPlaylist = "";
                    mainActivity.selectedPlaylist = "";
                    String selectedSong = "";
                    mainActivity.selectedPlaylistPos = v.getId();
                    playlists = mainActivity.dbM.getTitlePlaylists();

                    if (mainActivity.PlHelper.Existing(mainActivity.selectedPlaylistPos,""))
                    {
                        mainActivity.selectedPlaylist = playlists.get(mainActivity.selectedPlaylistPos).toString();

                    }

                    mainActivity.dbM.addNewSongToPlaylist(mainActivity.IMainUpd, mainActivity.songInfo,mainActivity.selectedGenreSong,mainActivity.selectedPlaylist);
                    Toast.makeText(getActivity(), "Song added", Toast.LENGTH_SHORT).show();
                    mainActivity.selectedSongPos = -1;

                    if (mainActivity.isFragmentCExists) {
                        comm.respondC();
                    }

                }
                return true;
            }
        };


        Graphics.setGraphics();
        playlists = mainActivity.dbM.getTitlePlaylists();

        updateIcons(playlists.size());


    }

    public void updateIcons(int quantity){

        for (int j=0; j<mainActivity.katians.size(); j++){
            View kat = getActivity().findViewById(mainActivity.katians.get(j).getId());
            ((ViewGroup)kat.getParent()).removeView(kat);
        }
        mainActivity.katians.clear();

        playListQuantity = quantity;
        // Создаю иконки плейлистов
        int colorsCounter = 0;
        int iconsCounter = 0;
        if (mainActivity.removedIcons.size() >0) {
//            Toast.makeText(getActivity(), "removed: " + mainActivity.removedIcons, Toast.LENGTH_SHORT).show();
        }
        for(int i=0; i<playListQuantity; i++){
//            if (mainActivity.removedIcons.contains(colorsCounter)){
//                for (int o=0; o<mainActivity.removedIcons.size(); o++){
//                    if (mainActivity.removedIcons.get(o) == o) {
//                        colorsCounter++;
//                        iconsCounter++;
//                    }
//                }
//            }

            if (mainActivity.removedIcons.contains(i)){
                for (int o=0; o<mainActivity.removedIcons.size(); o++){
                    if (mainActivity.removedIcons.get(o) == i) {
                        colorsCounter++;
                        iconsCounter++;
                    }
                }
            }



            if (colorsCounter >= Graphics.katianColors.size()-1){
                colorsCounter = 0;
            }
            if (iconsCounter >= Graphics.katianIcons.size()-1){
                iconsCounter = 0;
            }

            mainActivity.katians.add(new KatianFloat.Builder(getActivity())
                    .withDrawable(getResources().getDrawable(Graphics.katianIcons.get(iconsCounter)))
                    .withButtonColor(Graphics.katianColors.get(colorsCounter))
                    .withButtonSize(90)
                    .create());

            mainActivity.katians.get(i).setId(i);
            mainActivity.katians.get(i).setTag("VISIBLE");
            mainActivity.katians.get(i).setOnTouchListener(touchFAB);
            mainActivity.katians.get(i).setOnDragListener(drop);
            View v = getActivity().findViewById(mainActivity.katians.get(i).getId());

            Log.i("LOGb", mainActivity.stor.toString());
            if(mainActivity.stor.containsKey(v.getId())){
                v.setX(mainActivity.stor.getX(v.getId()));
                v.setY(mainActivity.stor.getY(v.getId()));

                String tempVisibility = mainActivity.stor.getVis(v.getId());

                if(!(tempVisibility == null)){
                    v.setTag(tempVisibility);
                    if(tempVisibility.equals("INVISIBLE")){
                       v.setVisibility(View.INVISIBLE);
                    }else if(tempVisibility.equals("VISIBLE")){
                       v.setVisibility(View.VISIBLE);
                    }
                }
            }else{
                v.setX(mainActivity.size.x - 270);
                mainActivity.stor.put(v.getId(), mainActivity.size.x - 270, 0.0f);
                mainActivity.editor.putString("iconPositions", mainActivity.gson.toJson(mainActivity.stor));
                mainActivity.editor.commit();
            }

            colorsCounter++;
            iconsCounter++;
        }

    }


}

















