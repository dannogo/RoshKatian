package player.com.roshkatian;

/**
 * Created by Дом on 24.01.2015.
 */
public class SongInformation {
    private String artist;
    private String songTitle;
    private Integer songIsLoad;
    private String songLink;

    public SongInformation() {
    }

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

    public Integer getSongIsLoad() {
        return songIsLoad;
    }

    public void setSongIsLoad(Integer songIsLoad) {
        this.songIsLoad = songIsLoad;
    }

    public String getSongLink() {
        return songLink;
    }

    public void setSongLink(String songLink) {
        this.songLink = songLink;
    }

    public String UniteAtristTitle() {
        String s = getArtist()+" - "+getSongTitle();
        return s;
    }

}
