package objects;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.io.File;
import java.util.regex.Pattern;

public class Song {
    public static final String DOT_SYMBOL = ".";
    public static final double DEFAULT_VOLUME = 0.5;
    public static final int INDEX_ZERO = 0;

    private boolean isPlay = false;
    private int number;
    private String name;
    private String fullName;
    private String pathName;
    private String pathAbsoluteName;
    private Media media;
    private MediaPlayer mediaPlayer;
    private File file;

    private String[] pathOfName;

    public Song(String fullName) {
        try {
            this.fullName = fullName;
            file = new File(fullName);
            name = stripExtension(file.getName());

            pathOfName = fullName.split(Pattern.quote(File.separator));
            setPathName(pathOfName[pathOfName.length - 2]);

            pathAbsoluteName = fullName.substring(INDEX_ZERO, fullName.lastIndexOf(File.separator));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Song(File file) {
        try {
            this.file = file;
            fullName = file.getAbsolutePath();
            name = stripExtension(file.getName());

            pathOfName = file.getAbsolutePath().split(Pattern.quote(File.separator));
            setPathName(pathOfName[pathOfName.length - 2]);

            pathAbsoluteName = fullName.substring(INDEX_ZERO, fullName.lastIndexOf(File.separator));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void play() {
        if (getMediaPlayer() == null) {
            loadSong();
        }
        isPlay = true;
        mediaPlayer.play();
    }

    public void pause() {
        if (getMediaPlayer() == null) {
            loadSong();
        }
        isPlay = false;
        mediaPlayer.pause();
    }

    public void stop() {
        if (getMediaPlayer() == null) {
            loadSong();
        }
        isPlay = false;
        mediaPlayer.stop();
    }

    public void loadSong() {
        media = new Media(file.toURI().toString());
        mediaPlayer = new MediaPlayer(getMedia());
        mediaPlayer.setVolume(DEFAULT_VOLUME);
    }

    public void clearSong() {
        media = null;
        mediaPlayer = null;
    }

    public static String stripExtension(String str) {
        if (str == null) return null;
        int pos = str.lastIndexOf(DOT_SYMBOL);
        if (pos == -1) return str;
        return str.substring(INDEX_ZERO, pos);
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getFullname() {
        return fullName;
    }

    public void setFullname(String fullname) {
        this.fullName = fullname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPathName() {
        return pathName;
    }

    public Media getMedia() {
        return media;
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public boolean isPlay() {
        return isPlay;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getPathAbsoluteName() {
        return pathAbsoluteName;
    }

    public void setPathName(String pathName) {
        this.pathName = pathName;
    }
}
