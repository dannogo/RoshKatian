package player.com.roshkatian;

import android.os.AsyncTask;

import org.htmlcleaner.TagNode;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Дом on 15.01.2015.
 */
public class ParseHelper {
    boolean isParse=true;
    String MAIN_PAGE = "http://300kbit.ru/";
    List<GenrePathName> pGenrePathName;
    List<ArtistTitle> pArtistTitle;
    private ParseSite parseSite;
    private ParsePages parsePages;
    PARSECONTINUE parseContinue;
    public interface PARSECONTINUE{
        public void continueParseSite();

        public void continueParsePages();
    }

    public ParseHelper(PARSECONTINUE parseContinue){
        pGenrePathName = new ArrayList<GenrePathName>();
        pArtistTitle = new ArrayList<ArtistTitle>();
        this.parseContinue = parseContinue;
    }

    public void setGenrePathName ( List<GenrePathName> g){
        pGenrePathName = g;
    }
    public  List<ArtistTitle> getArtistTitle (){
        return pArtistTitle;
    }
    public void setArtistTitle ( List<ArtistTitle> a){
        pArtistTitle = a;
    }
    public  List<GenrePathName> getGenrePathName (){
        return pGenrePathName;
    }

    public void ParseGenres() {
        parseSite = new ParseSite();

        parseSite.execute(MAIN_PAGE);
    }


    public void ParseSongs(String page) {
        parsePages = new ParsePages();
        parsePages.execute(page);
    }
        //----------parsing----------
//класс для return данных по исполнителям
    public class ArtistTitle {
        private String artist;
        private String songTitle;
        private String songLink;
        public ArtistTitle() {}
        public String getArtist() {
            return artist;
        }

        public void setArtist(String artist) {
            this.artist = artist;
        }

        public String getSongTitle() {
            return songTitle;
        }

        public void setSongTitle(String songTitle) {
            this.songTitle = songTitle;
        }

        public String getSongLink() {
            return songLink;
        }

        public void setSongLink(String songLink) {
                this.songLink = songLink;
        }

        public void SeparateAtristTitle(String s){

            for(int i=0; i<s.length();i++)
            {
                if (s.charAt(i) == '-'){
                    setArtist(s.substring(0, i - 1));
                    setSongTitle(s.substring(i + 2));
                }
            }

        }
    }
    //класс для return данных по по жанрам
    public class GenrePathName {
        private String genrePath;
        private String genreName;

        public GenrePathName() {
        }

        public String getGenrePath() {
            return genrePath;
        }

        public void setGenrePath(String genrePath) {
            this.genrePath = genrePath;
        }

        public String getGenreName() {
            return genreName;
        }

        public void setGenreName(String genreName) {
            this.genreName = genreName;

        }
    }

    private class ParseSite extends AsyncTask<String, Void, List<GenrePathName>> {
        //Фоновая операция

        @Override
        protected List<GenrePathName> doInBackground(String... arg) {
            List<GenrePathName> genrePathName = new ArrayList<GenrePathName>();
            List<String> genrePath = new ArrayList<String>();
            List<String> genreName = new ArrayList<String>();
            genrePathName.clear();
            genrePath.clear();
            genreName.clear();
            try {
                HtmlHelper hh = new HtmlHelper(new URL(arg[0]));
                List<TagNode> listGenrelinks = hh.getLinksByUl();
                for (Iterator<TagNode> iterator = listGenrelinks.iterator(); iterator.hasNext(); ) {
                    TagNode divElement = (TagNode) iterator.next();
                    genrePath.add(MAIN_PAGE + divElement.getAttributeByName("href"));
                    genreName.add(divElement.getText().toString());
                }
                for (int i = 0; i < genrePath.size(); i++) {
                    genrePathName.add(new GenrePathName());
                    genrePathName.get(i).setGenrePath(genrePath.get(i));
                    genrePathName.get(i).setGenreName(genreName.get(i));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return genrePathName;
        }

        //Событие по окончанию парсинга сайта
        @Override
        protected void onPostExecute(List<GenrePathName> output) {
            setGenrePathName(output);

                parseContinue.continueParseSite();

        }
    }

        private class ParsePages extends AsyncTask<String, Void, List<ArtistTitle>> {
            //Фоновая операция
            @Override
            protected List<ArtistTitle> doInBackground(String... arg) {
                List<ArtistTitle> artistTitle = new ArrayList<ArtistTitle>();
                List<String> songsName = new ArrayList<String>();
                List<String> songsLink = new ArrayList<String>();
                songsName.clear();
                artistTitle.clear();
                try
                {

                HtmlHelper hh = new HtmlHelper(new URL(arg[0]));
                List<TagNode> links = hh.getLinksByClass("name-song");

                for (Iterator<TagNode> iterator = links.iterator(); iterator.hasNext();)
                {
                    TagNode divElement = (TagNode) iterator.next();
                    songsName.add(divElement.getText().toString());
                    songsLink.add(divElement.getAttributeByName("href"));
                }
                    for(int i=0;i<songsName.size();i++) {
                        artistTitle.add(new ArtistTitle());
                        artistTitle.get(i).SeparateAtristTitle(songsName.get(i));
                        artistTitle.get(i).setSongLink(songsLink.get(i));
                    }

                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
                return artistTitle;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (!isParse){
                    parsePages.cancel(true);
                    parseContinue.continueParsePages();

                }
            }

            //
//        protected void onPreExecute(List<ArtistTitle> output)  {
//
//        }
        //Событие по окончанию парсинга страницы
        protected void onPostExecute(List<ArtistTitle> output)
        {
            setArtistTitle(output);

            parseContinue.continueParsePages();
        }
    }
}
