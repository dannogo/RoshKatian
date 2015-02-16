package player.com.roshkatian;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by oleh on 1/9/15.
 */
public class FragmentC extends Fragment {

    DragSortListView playlist;
    public ArrayAdapter<String> adapter;
    RelativeLayout relativeLayoutPlaylist;
    LinearLayout deleteSongBar;
    MainActivity mainActivity;
    ArrayList<String> currentPlaylist = new ArrayList<String>();
    ArrayList<String> tempList = new ArrayList<String>();
    ImageView iconDelete;
    ArrayList<String> tempStorageForCurrentOrder = new ArrayList<String>();
    FloatingActionButton editPlaylistButton;
    ViewPager viewPager;



//    ImageView iconTest;


    private DragSortListView.DropListener onDrop =
            new DragSortListView.DropListener() {
                @Override
                public void drop(int from, int to) {
                    if (from != to) {
                        DragSortListView list = (DragSortListView) getActivity().findViewById(R.id.playlist);
                        String item = adapter.getItem(from);
                        adapter.remove(item);
                        adapter.insert(item, to);
                        list.moveCheckState(from, to);
                        Log.d("DSLV", "Selected item is " + list.getCheckedItemPosition());

                        // Сохранение нового порядка песен в БД

                        int correctAmount = adapter.getCount();
                        tempStorageForCurrentOrder.clear();
                        for (int i=0; i<correctAmount; i++){
                            tempStorageForCurrentOrder.add(adapter.getItem(i));
                        }
//                        Toast.makeText(getActivity(), tempStorageForCurrentOrder.get(2),Toast.LENGTH_SHORT).show();
                        ArrayList<String> tempSongList = mainActivity.dbM.getArtistTitleSong(mainActivity.activePlaylist);
                        for (int i=0; i< tempSongList.size(); i++){
                            mainActivity.dbM.delSelectedSong(mainActivity.IMainUpd, mainActivity.PlHelper.SeparateAtristTitle(tempSongList.get(i)), mainActivity.activePlaylist);
//                            getPlaylistSongs(mainActivity.selectedPlaylist);
                        }


                        for(int i=0; i<tempStorageForCurrentOrder.size(); i++){
                            mainActivity.dbM.addNewSongToPlaylist(mainActivity.IMainUpd, mainActivity.songInfo,tempStorageForCurrentOrder.get(i),mainActivity.selectedPlaylist);

                        }

                        getPlaylistSongs(mainActivity.activePlaylist);

                        // В случае если при реордеринге песни записались в БД, или записались не все, повторить попытку
//                        tempList.clear();
//                        tempList = mainActivity.dbM.getArtistTitleSong(mainActivity.selectedPlaylist);
//                        while(tempList.size() != correctAmount){
//                            tempSongList = mainActivity.dbM.getArtistTitleSong(mainActivity.selectedPlaylist);
//                            for (int i=0; i< tempSongList.size(); i++){
//                                mainActivity.dbM.delSelectedSong(mainActivity.IMainUpd, mainActivity.PlHelper.SeparateAtristTitle(tempSongList.get(i)),mainActivity.activePlaylist);
//
//                            }
//
//
//                            for(int i=0; i<tempStorageForCurrentOrder.size(); i++){
//                                mainActivity.dbM.addNewSongToPlaylist(mainActivity.IMainUpd, mainActivity.songInfo,tempStorageForCurrentOrder.get(i),mainActivity.selectedPlaylist);
//
//                            }
//                        }
//                        getPlaylistSongs(mainActivity.selectedPlaylist);
//

                    }
                }
            };


    interface Communicator{
        public void respondC();
    }

    public interface changeFragmentCountCallback{
        void change(int delta);
    }

    public changeFragmentCountCallback myAdapter;

    public void recreatePlaylistAfterComeback(){
        adapter.notifyDataSetChanged();
    }

    public void getPlaylistSongs(String selectedPlaylist){
//        tempList = mainActivity.dbM.getArtistTitleSong(mainActivity.selectedPlaylist);
        tempList.clear();
        tempList = mainActivity.dbM.getArtistTitleSong(selectedPlaylist);

//        Toast.makeText(getActivity(), ""+tempList.size(), Toast.LENGTH_SHORT).show();
        currentPlaylist.clear();

        for (int i=0; i < tempList.size(); i++){
            currentPlaylist.add(tempList.get(i));
        }
        adapter.notifyDataSetChanged();


    }

    public void removeFragment(){
//
        currentPlaylist.clear();

//        editPlaylistButton.setVisibility(View.INVISIBLE);
        adapter.notifyDataSetChanged();


    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        mainActivity.editPlaylistButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                myAdapter.change(2);
//                viewPager.setCurrentItem(1);
//                mainActivity.dbM.delSelectedPlaylist(mainActivity.IMainUpd, mainActivity.PlHelper, mainActivity.activePlaylist);
//                mainActivity.isFragmentCExists = false;
//                removeFragment();
////                mainActivity.
//                Toast.makeText(getActivity(), "Ха-Ха", Toast.LENGTH_SHORT).show();
//            }
//        });

    }
    
    public void createEditPlaylistButton(){
        editPlaylistButton = new FloatingActionButton.Builder(getActivity())
                .withDrawable(getResources().getDrawable(R.drawable.edit32))
                .withButtonColor(Graphics.katianColors.get(5))
                .withGravity(Gravity.BOTTOM | Gravity.RIGHT)
                .withMargins(0, 0, 16, 16)
                .withButtonSize(90)
                .create();

        editPlaylistButton.setId(R.id.edit_button);
//        editPlaylistButton.setVisibility(View.INVISIBLE);

        editPlaylistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final EditText input = new EditText(getActivity());
                final CharSequence[] items = {"Delete"};
                final ArrayList selectedItems = new ArrayList();



                CheckBox rb = new CheckBox(getActivity());
                rb.setId(R.id.radio_button);
                rb.setText("delete playlist \"" + mainActivity.activePlaylist + "\"");
                rb.setTextSize(15.0f);

                final LinearLayout ll = new LinearLayout(getActivity());
                ll.setOrientation(LinearLayout.VERTICAL);
                ll.addView(input);
                ll.addView(rb);

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                        builder.setTitle("Edit playlist \"" + mainActivity.activePlaylist+"\"")
                        .setMessage("Input new title: ")
                        .setView(ll)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                EditText edt = (EditText) ll.getChildAt(0);
                                CheckBox chb = (CheckBox) ll.getChildAt(1);

                                if (chb.isChecked()) {
                                    // -- DELETING PLAYLIST UNDER --
                                    mainActivity.dbM.delSelectedPlaylist(mainActivity.IMainUpd, mainActivity.PlHelper, mainActivity.activePlaylist);
                                    mainActivity.isFragmentCExists = false;

//                mainActivity.stor.put(mainActivity.idOfCurrentActivePlaylist, mainActivity.size.x - 270, 0.0f);
//                mainActivity.stor.putVis(mainActivity.idOfCurrentActivePlaylist,"VISIBLE");
                                    mainActivity.stor.removePlaylistData(mainActivity.idOfCurrentActivePlaylist);
                                    mainActivity.editor.putString("iconPositions", mainActivity.gson.toJson(mainActivity.stor));
                                    mainActivity.editor.commit();
                                    myAdapter.change(2);
                                    viewPager.setCurrentItem(1);

//                mainActivity.removedIcons.add(mainActivity.idOfCurrentActivePlaylist);

                                    removeFragment();
//
////                Toast.makeText(getActivity(), "Ха-Ха", Toast.LENGTH_SHORT).show();
                                    // -- DELETING PLAYLIST ABOVE --

                                } else {
                                    if (!(edt.getText().toString().equals(""))) {
                                        String currentDateTime = (String) DateFormat.format("yyyy-MM-dd kk:mm:ss", new Date());
                                        mainActivity.dbM.updSelectedPlaylist(mainActivity.activePlaylist, "" + edt.getText(), currentDateTime);
//                                    Toast.makeText(getActivity(), "" + edt.getText(), Toast.LENGTH_SHORT).show();
                                        mainActivity.activePlaylist = "" + edt.getText();
//                                    mainActivity.selectedPlaylist = ""+edt.getText();
                                        getActivity().setTitle(mainActivity.activePlaylist);
                                    }
                                }
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();
                            }
                        })
                        .setIcon(R.drawable.edit32)
                        .show();


            }
        });
        mainActivity.isAfterFragmentC = true;
//        Toast.makeText(getActivity(), "createIcon", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mainActivity = (MainActivity) getActivity();

        viewPager = (ViewPager) getActivity().findViewById(R.id.pager);

//        createEditPlaylistButton();


        playlist = (DragSortListView) getActivity().findViewById(R.id.playlist);
        relativeLayoutPlaylist = (RelativeLayout) getActivity().findViewById(R.id.main_of_fragment_c);
        deleteSongBar = (LinearLayout) getActivity().findViewById(R.id.delete_song_bar);
        iconDelete = (ImageView) getActivity().findViewById(R.id.delete_song);
        mainActivity.isAfterFragmentC = true;
//        mainActivity.setTitle("Хей");
//        mainActivity.get

//        iconTest = (ImageView) getActivity().findViewById(R.id.iconTest);

        relativeLayoutPlaylist.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                View view = (View) event.getLocalState();
                if (event.getAction() == DragEvent.ACTION_DROP){
                    view.setVisibility(View.VISIBLE);
                    deleteSongBar.setVisibility(View.INVISIBLE);
                }
                return true;
            }
        });

        View.OnDragListener dropToDeleteSong = new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                View view = (View) event.getLocalState();

                if(event.getAction() == DragEvent.ACTION_DRAG_ENTERED){

                    v.startAnimation(mainActivity.rotateAnimation);

                }
                if(event.getAction() == DragEvent.ACTION_DROP){

                    if (mainActivity.PlHelper.Existing(mainActivity.selectedSongPos,mainActivity.CHOOSE_SONG)) {
                        mainActivity.dbM.delSelectedSong(mainActivity.IMainUpd, mainActivity.PlHelper.SeparateAtristTitle(view.getTag().toString()), mainActivity.activePlaylist);
                        getPlaylistSongs(mainActivity.activePlaylist);
                        Toast.makeText(getActivity(), "Song removed", Toast.LENGTH_SHORT).show();
                        mainActivity.selectedSongPos -= 1;
                    }

                    deleteSongBar.setVisibility(View.INVISIBLE);
                    view.setVisibility(View.VISIBLE);
                }
                if (event.getAction() == DragEvent.ACTION_DRAG_ENDED){
                    v.setVisibility(View.VISIBLE);
                }

                return true;
            }
        };

        iconDelete.setOnDragListener(dropToDeleteSong);
//        iconTest.setOnDragListener(dropToDeleteSong);

        playlist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                deleteSongBar.setVisibility(View.VISIBLE);
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                ViewGroup viewGroup = (ViewGroup) view;
                View linearLayout = ((ViewGroup) view).getChildAt(0);
                View maybeTextView = ((ViewGroup) linearLayout).getChildAt(1);
                TextView text = (TextView) maybeTextView;
                view.setTag(text.getText());
                view.startDrag(null, shadowBuilder, view, 0);
                view.setVisibility(View.INVISIBLE);
                mainActivity.selectedSongPos = position;
//                mainActivity.playlistSongs = mainActivity.UpdateSongInformation();
//                mainActivity.selectedGenreSong = (String) text.getText();
                return true;
            }
        });



        playlist.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                if (event.getAction() == DragEvent.ACTION_DRAG_ENDED){
                    View view = (View) event.getLocalState();
                    v.setVisibility(View.VISIBLE);
                }

                return false;
            }
        });



        playlist.setDropListener(onDrop);
        playlist.setChoiceMode(ListView.CHOICE_MODE_SINGLE);


//        mainActivity = (MainActivity) getActivity();

        currentPlaylist.clear();

        for (int i=0; i < tempList.size(); i++){
            currentPlaylist.add(tempList.get(i));
        }

        adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_radio, R.id.text, currentPlaylist);
        playlist.setAdapter(adapter);
}

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_c, container, false);

    }
}


















//        playlist.setOnDragListener(new View.OnDragListener() {
//            @Override
//            public boolean onDrag(View v, DragEvent event) {
//                View view = (View) event.getLocalState();
//                if (event.getAction() == DragEvent.ACTION_DRAG_ENDED){
//                    deleteSongBar.setVisibility(View.INVISIBLE);
//
//                    if(!(view instanceof KatianFloat)) {
//                        view.setVisibility(View.VISIBLE);
//                    }
//                }
//                if (event.getAction() == DragEvent.ACTION_DRAG_STARTED){
//                    deleteSongBar.setVisibility(View.VISIBLE);
//                    iconDelete.setVisibility(View.VISIBLE);
//                }
//
//                return false;
//            }
//        });