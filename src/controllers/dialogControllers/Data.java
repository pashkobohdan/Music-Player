package controllers.dialogControllers;

import java.io.File;

/**
 * Created by Bohdan Pashko on 07.04.2016.
 */
public class Data {
    public static final int MAX_LENGTH_LABEL = 40;
    public static final int MIN_CHANGE_DURATION = 50;
    public static final int MIN_CHANGE_SLIDER_DURATION_VALUE = 3;
    public static final int DEFAULT_VOLUME = 50;
    public static final int DOUBLE_CLICK = 2;
    public static final int SECOND_IN_MINUTE = 60;
    public static final int sleepHalfSecond = 500;
    public static final double MAX_SLIDER_VALUE = 100.0;
    public static final double MIN_VOLUME = 0.0;
    public static final String EMPTY_STRING = "";
    public static final String PROJECT_PATH = System.getProperty("user.dir");
    public static final String PICTURES_PATH = PROJECT_PATH + "/src/pictures";
    public static final String PICTURES_FILES = PICTURES_PATH + "/";
    public static final String PLAYLISTS_PATH = PROJECT_PATH + "/src/playlists";
    public static final String PLAYLISTS_FILES = PLAYLISTS_PATH + "/";
    public static final String PLAYLIST_EXPANTION = ".mppl";
    public static final String TIME_FORMAT = "%02d:%02d";
    public static final String DEFAULT_TIME = "00:00";
    public static final String TIME_FORMAT_WITH_HOURS = "%02d:%02d:%02d";
    public static final String CUSTOM_BUTTON_PRESSED_STYLE = "-fx-background-color: burlywood";
    public static final String CUSTOM_BUTTON_DEFAULT_STYLE = "-fx-background-color: wheat; ";
    public static final String PNG_FILE_EXPANTION = ".png";
    public static final String JPG_FILE_EXPANTION = ".jpg";
    public static final String JPEG_FILE_EXPANTION = ".jpeg";
    public static final String MP3_FILE_EXPANTION = ".mp3";
    public static final String STOP_TIME_DIALOG_PATH = "../fxml/stopTimeDialog.fxml";
    public static final String STOP_COUNTER_DIALOG_PATH = "../fxml/stopCounterDialog.fxml";
    public static final String PLAYLIST_LIST_DIALOG_PATH = "../fxml/playlistList.fxml";
    public static final String SETTINGS_DIALOG_PATH = "../fxml/settings.fxml";
    public static final File DEFAULT_SONG_IMAGE = new File(PICTURES_FILES + "defaultSongImage" + PNG_FILE_EXPANTION);
}
