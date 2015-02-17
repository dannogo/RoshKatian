package player.com.roshkatian;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by oleh on 1/9/15.
 */
public class FragmentA extends Fragment implements  ParseHelper.PARSECONTINUE  {

    ViewPager viewPager=null;
    boolean isFirstTimeRan = true;

    Context context;
    ParseHelper parseHelper;
    private ProgressDialog pd;


    List<String> genrePath = new ArrayList<String>();
    List<String> genreName = new ArrayList<String>();
//    List<String> songsArtist = new ArrayList<String>();
//    List<String> songsTitle = new ArrayList<String>();
//    List<String> songsLink = new ArrayList<String>();

    FragmentB.Communicator comm;

//    String selectedGenre;
    MainActivity mainActivity;

    KatianAdapter adapter;


    public interface changeFragmentCountCallback{
        void change(int delta);
    }

    public changeFragmentCountCallback myAdapter;

    ListView list;
//    String[] descriptions={"Описание 1", "Описание 2", "Описание 3", "Описание 4", "Описание 5", "Описание 6", "Описание 7", "Описание 8", "Описание 9", "Описание 10", "Описание 11", "Описание 12", "Описание 13", "Описание 14", "Описание 15", "Описание 16", "Описание 17"};


    int[] images = {
            R.drawable.r1,
            R.drawable.r18,
            R.drawable.r15,
            R.drawable.r3,
            R.drawable.r19,
            R.drawable.r17,
            R.drawable.r7,
            R.drawable.r8,
            R.drawable.r9,
            R.drawable.r10,
            R.drawable.r11,
            R.drawable.r12,
            R.drawable.r13,
            R.drawable.r14,
            R.drawable.r4,
            R.drawable.r16,
            R.drawable.r6,
            R.drawable.r2,
            R.drawable.r19,
            R.drawable.r5,
            R.drawable.r21,
            R.drawable.r22,
            R.drawable.r23,
            R.drawable.r24,
            R.drawable.r25
    };

    String[] genres;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getActivity();

//        if (isFirstTimeRan) {
            parseHelper = new ParseHelper(this);

            pd = ProgressDialog.show(context, "Working...", "request to server", true, false);
            // Запускаем парсинг сайта
            parseHelper.ParseGenres();
//        }
    }



    @Override
    public void onAttach(Activity activity) {

        super.onAttach(activity);
        comm = (FragmentB.Communicator) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_a, container, false);
        list = (ListView) rootView.findViewById(R.id.listView);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewPager = (ViewPager) getActivity().findViewById(R.id.pager);
        mainActivity = (MainActivity) getActivity();


    }

    @Override
    public void onResume() {
        super.onResume();

//        Привожу ArrayList к Array
        String[] genres = genreName.toArray(new String[genreName.size()]);

//        list.setAdapter(new KatianAdapter(getActivity(), genres, images, descriptions));
        list.setAdapter(new KatianAdapter(getActivity(), genres, images));

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                mainActivity.selectedGenre = genreName.get(position);
//                selectedGenre = ((TextView) view).getText().toString();

//                mainActivity.selectedGenreSong = genreName.get(position);

                for(int i=0; i<genreName.size();i++){
                    if (mainActivity.selectedGenre.equals(genreName.get(i))) {
                        pd = ProgressDialog.show(context, "Working...", "request to server", true, false);

                        isParse();

                        //Запускаем парсинг страниц
                        parseHelper.ParseSongs(genrePath.get(i));

                        // Открытие доступа и переход на второй фрагмент
                        if (!mainActivity.isFragmentCExists) {
                            myAdapter.change(2);
                        }
                        viewPager.setCurrentItem(1);

                        break;
                    }
                }
            }

        });



    }

    @Override
    public void continueParseSite() {
        pd.dismiss();
        genreName.clear();
        genrePath.clear();
        for(int i=0;i<parseHelper.pGenrePathName.size();i++) {
            genrePath.add(parseHelper.pGenrePathName.get(i).getGenrePath());
            if (parseHelper.pGenrePathName.get(i).getGenreName().equals("Главная страница")) {
                genreName.add("Новые");
            }else if(parseHelper.pGenrePathName.get(i).getGenreName().equals("Регги")){

            }else {
                genreName.add(parseHelper.pGenrePathName.get(i).getGenreName());
            }
        }
//      Привожу ArrayList к Array
        String[] genres = genreName.toArray(new String[genreName.size()]);

//        list.setAdapter(new KatianAdapter(getActivity(), genres, images, descriptions));
        list.setAdapter(new KatianAdapter(getActivity(), genres, images));

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                mainActivity.selectedGenre = genreName.get(position);
//                selectedGenre = ((TextView) view).getText().toString();
//                mainActivity.selectedGenreSong = genreName.get(position);

                for(int i=0; i<genreName.size();i++){
                    if (mainActivity.selectedGenre.equals(genreName.get(i))) {
                        pd = ProgressDialog.show(context, "Working...", "request to server", true, false);

                        isParse();

                        //Запускаем парсинг страниц
                        parseHelper.ParseSongs(genrePath.get(i));

                        // Открытие доступа и переход на второй фрагмент
                        myAdapter.change(2);
                        viewPager.setCurrentItem(2);

                        break;
                    }
                }
            }

        });



    }


    public void isParse(){
        // в этом месте проверка на парсинг песен
        if (mainActivity.temporaryStorage.containsKey(mainActivity.selectedGenre)){
            parseHelper.isParse = false;
        }else{
            parseHelper.isParse = true;

        }
    }


    @Override
    public void continueParsePages() {
        pd.dismiss();

        boolean isFirstTimeChosen = true;

        mainActivity.songsArtist.clear();
        mainActivity.songsTitle.clear();
        mainActivity.songsLink.clear();


        if (mainActivity.temporaryStorage.containsKey(mainActivity.selectedGenre)){

            isFirstTimeChosen = false;
            Toast.makeText(getActivity(), "" + mainActivity.temporaryStorage.get(mainActivity.selectedGenre).get(0).size(), Toast.LENGTH_SHORT).show();


            mainActivity.songsTitle = mainActivity.temporaryStorage.get(mainActivity.selectedGenre).get(0);
            mainActivity.songsArtist = mainActivity.temporaryStorage.get(mainActivity.selectedGenre).get(1);
            mainActivity.songsLink = mainActivity.temporaryStorage.get(mainActivity.selectedGenre).get(2);

        }

        if(isFirstTimeChosen){

            //Загружаем в него результат работы doInBackground
            for(int j=0;j<parseHelper.pArtistTitle.size();j++) {
                mainActivity.songsArtist.add(parseHelper.pArtistTitle.get(j).getArtist());
                mainActivity.songsTitle.add(parseHelper.pArtistTitle.get(j).getSongTitle());
                mainActivity.songsLink.add(parseHelper.pArtistTitle.get(j).getSongLink());
            }
        }
        mainActivity.refreshTemporaryStorage();


        comm.respondB();
    }


}


class MyViewHolder{
    ImageView myImage;
    TextView myTitle;
    TextView myDescription;

    MyViewHolder(View v) {
        myImage = (ImageView) v.findViewById(R.id.imageView);
        myTitle = (TextView) v.findViewById(R.id.textView);
//        myDescription = (TextView) v.findViewById(R.id.textView2);
    }
}


class KatianAdapter extends ArrayAdapter<String> {

    Context context;
    int images[];
    String[] titleArray;
    String[] descriptionArray;

//    KatianAdapter(Context c, String[] titles, int imgs[], String[] desc) {
    KatianAdapter(Context c, String[] titles, int imgs[]) {
        super(c, R.layout.single_row, R.id.textView, titles);
        this.context = c;
        this.images = imgs;
        this.titleArray = titles;
//        this.descriptionArray = desc;

    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        MyViewHolder holder = null;

        if(row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.single_row, parent, false);
            holder = new MyViewHolder(row);
            row.setTag(holder);
        }else{
            holder = (MyViewHolder) row.getTag();
        }

        holder.myImage.setImageResource(images[position]);
        holder.myTitle.setText(titleArray[position]);
//        holder.myDescription.setText(descriptionArray[position]);

        return row;
    }



}
