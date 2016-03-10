package controllers;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import objects.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Controller implements Initializable {
    public static final int MAX_LENGTH_LABEL = 40;
    public static final int MIN_CHANGE_DURATION = 50;
    public static final int MIN_CHANGE_SLIDER_DURATION_VALUE = 3;
    public static final int DEFAULT_VOLUME = 50;
    public static final int DOUBLE_CLICK = 2;
    public static final int SECOND_IN_MINUTE = 60;
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

    private ResourceBundle resourceBundle;
    private TreeItem<String> treeItem;
    private ObservableList<String> playlistNames;
    private Playlist currentPlaylist;
    private Runnable endOfSongRunnable;
    private Duration duration;
    private Duration currentDuration;
    private double durationDouble;
    private double currentDurationDouble;
    private double oldValueDouble, newValueDouble;
    private double volume = DEFAULT_VOLUME;
    private int index;

    private boolean isShuffle = false;
    private boolean isMute = false;
    private boolean isRePlay = false;

    @FXML
    private TreeView<String> treeView;
    @FXML
    private Button buttonSettings, buttonGoToCurrentSong,
            buttonRewind, buttonStop, buttonPlay, buttonPause, buttonForward, buttonReplaySong, buttonSetStopTime,
            buttonShuffle, buttonReplay, buttonMute, buttonBack,
            buttonNext, buttonAddPlaylist, buttonPlaylistList,
            buttonOpenFolder, buttonOpenFiles, buttonSearch;
    @FXML
    private Label labelSongName, labelCountLeft, labelSongDuration, labelSongNowTime, labelNamePlaylist, labelLeftTime;
    @FXML
    private TextField textSearch;
    @FXML
    private Slider sliderStatus, sliderVolume;
    @FXML
    private ImageView songImage;

    private Stage mainStage;

    private DirectoryChooser directoryChooser;
    private File chooserPath;

    private FXMLLoader fxmlLoaderPlaylistDialog = new FXMLLoader();
    private Stage newPlaylistDialogStage;
    private Parent parentPlaylistDialog;
    private ControllerNewPlaylist newPlaylistDialogController;

    private PlaylistInfo playlistInfo;
    private File pictureFile;
    private boolean isFindEXPANTION;

    // fields for show stopTime dialog ( choose time of close application)
    private FXMLLoader fxmlLoaderStopTime = new FXMLLoader();       // loader of stopTime-dialog .fxml-file
    private Stage stopTimeStage;                                    // stage of stopTime-dialog window
    private Parent parentStopTime;                                  // parent of stopTime-dialog window
    private ControllerSetStopTime stopTimeController;               // controller of stopTime-dialog window
    private StopTimeInfo stopTimeInfo = new StopTimeInfo();         // info-class (stop time and other info)
    private MyThread myThread;                                     // thread for countdown of back time

    // fields for show stopCount dialog ( choose time of close application)
    private FXMLLoader fxmlLoaderStopCount = new FXMLLoader();      // loader (see up)
    private Stage stopCountStage;                                   // analogy (see up)
    private Parent parentStopCount;                                 // analogy (see up)
    private ControllerCounterStop stopCountController;              // analogy (see up)
    private StopCount stopCount = new StopCount();                  // analogy (see up)

    // fields for show playlistList dialog ( show all playlist )
    private FXMLLoader fxmlLoaderPlaylistList = new FXMLLoader();   // loader (see up)
    private Stage playlistListStage;                                // analogy (see up)
    private Parent parentPlaylistList;                              // analogy (see up)
    private ControllerPlaylistList playlistListController;          // analogy (see up)

    // fields for show settings
    private FXMLLoader fxmlLoaderSettings = new FXMLLoader();       // loader (see up)
    private Stage settingsStage;                                    // analogy (see up)
    private Parent parentSettings;                                  // analogy (see up)
    private ControllerSettings controllerSettings;                  // analogy (see up)

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        resourceBundle = resources;               // save resource-bundle to other window (for languages)

        labelLeftTime.setFont(new Font(15));    // set fonts and colors of text in labels
        labelSongName.setTextFill(Color.BLACK); //
        labelSongName.setFont(new Font(13));    //

        readPlaylistList();                     // read list of all playlist (names) for using down
        openFirstPlaylist();                    // open first playlist (of al)

        initButtonsToolTip();                   // design initialize

        initSlidersListeners();                 // listeners
        initTreeMouseClickedListener();         //

        initLoadNewPlaylistWindow();            // windows (dialog and etc..)
        initStopTimeWindow();                   //
        initStopCountWindow();                  //
        initPlaylistListWindow();               //
        initSettingsWindow();                   //


        textSearch.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                getCurrentPlaylist().setTreeViewFromSearchString(textSearch.getText());
            }
        });
    }

    public void setMainStage(Stage stage) {
        mainStage = stage;


        mainStage.setTitle(resourceBundle.getString("key.program.name"));
        mainStage.setMinHeight(550);
        mainStage.setMinWidth(500);


        getMainStage().setOnCloseRequest((event) -> {
            if (myThread != null) {
                myThread.stopNow();
            }
        });
    }

    private void initLoadNewPlaylistWindow() {
        try {
            fxmlLoaderPlaylistDialog.setLocation(getClass().getResource("../fxml/windowNewPlaylist.fxml"));
            parentPlaylistDialog = fxmlLoaderPlaylistDialog.load();
            newPlaylistDialogController = fxmlLoaderPlaylistDialog.getController();
            newPlaylistDialogController.setResourceBundle(resourceBundle);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initStopTimeWindow() {
        try {
            fxmlLoaderStopTime.setLocation(getClass().getResource(STOP_TIME_DIALOG_PATH));
            parentStopTime = fxmlLoaderStopTime.load();
            stopTimeController = fxmlLoaderStopTime.getController();
            stopTimeController.setResourceBundle(resourceBundle);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initStopCountWindow() {
        try {
            fxmlLoaderStopCount.setLocation(getClass().getResource(STOP_COUNTER_DIALOG_PATH));
            parentStopCount = fxmlLoaderStopCount.load();
            stopCountController = fxmlLoaderStopCount.getController();
            stopCountController.setResourceBundle(resourceBundle);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initPlaylistListWindow() {
        try {
            fxmlLoaderPlaylistList.setLocation(getClass().getResource(PLAYLIST_LIST_DIALOG_PATH));
            parentPlaylistList = fxmlLoaderPlaylistList.load();
            playlistListController = fxmlLoaderPlaylistList.getController();
            playlistListController.setResourceBundle(resourceBundle);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initSettingsWindow() {
        try {
            fxmlLoaderSettings.setLocation(getClass().getResource(SETTINGS_DIALOG_PATH));
            parentSettings = fxmlLoaderSettings.load();
            controllerSettings = fxmlLoaderSettings.getController();
            controllerSettings.setResourceBundle(resourceBundle);
            controllerSettings.setController(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initButtonsToolTip() {
        buttonSettings.setTooltip(new Tooltip(resourceBundle.getString("key.button.buttonSettings.ToolTip")));
        buttonGoToCurrentSong.setTooltip(new Tooltip(resourceBundle.getString("key.button.buttonGoToCurrentSong.ToolTip")));
        buttonRewind.setTooltip(new Tooltip(resourceBundle.getString("key.button.buttonRewind.ToolTip")));
        buttonStop.setTooltip(new Tooltip(resourceBundle.getString("key.button.buttonStop.ToolTip")));
        buttonPlay.setTooltip(new Tooltip(resourceBundle.getString("key.button.buttonPlay.ToolTip")));
        buttonPause.setTooltip(new Tooltip(resourceBundle.getString("key.button.buttonPause.ToolTip")));
        buttonForward.setTooltip(new Tooltip(resourceBundle.getString("key.button.buttonForward.ToolTip")));
        buttonReplaySong.setTooltip(new Tooltip(resourceBundle.getString("key.button.buttonReplaySong.ToolTip")));
        buttonSetStopTime.setTooltip(new Tooltip(resourceBundle.getString("key.button.buttonSetStopTime.ToolTip")));
        buttonShuffle.setTooltip(new Tooltip(resourceBundle.getString("key.button.buttonShuffle.ToolTip")));
        buttonReplay.setTooltip(new Tooltip(resourceBundle.getString("key.button.buttonReplay.ToolTip")));
        buttonMute.setTooltip(new Tooltip(resourceBundle.getString("key.button.buttonMute.ToolTip")));
        buttonBack.setTooltip(new Tooltip(resourceBundle.getString("key.button.buttonBack.ToolTip")));
        buttonNext.setTooltip(new Tooltip(resourceBundle.getString("key.button.buttonNext.ToolTip")));
        buttonAddPlaylist.setTooltip(new Tooltip(resourceBundle.getString("key.button.buttonAddPlaylist.ToolTip")));
        buttonPlaylistList.setTooltip(new Tooltip(resourceBundle.getString("key.button.buttonPlaylistList.ToolTip")));
        buttonOpenFolder.setTooltip(new Tooltip(resourceBundle.getString("key.button.buttonOpenFolder.ToolTip")));
        buttonOpenFiles.setTooltip(new Tooltip(resourceBundle.getString("key.button.buttonOpenFiles.ToolTip")));
        buttonSearch.setTooltip(new Tooltip(resourceBundle.getString("key.button.buttonSearch.ToolTip")));
    }

    private void initSlidersListeners() {
        sliderStatus.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (getCurrentPlaylist() != null &&
                    getCurrentPlaylist().getCurrentSong() != null &&
                    getCurrentPlaylist().getCurrentSong().getMediaPlayer() != null) {
                oldValueDouble = oldValue.doubleValue();
                newValueDouble = newValue.doubleValue();
                if (Math.abs(oldValueDouble - newValueDouble) > MIN_CHANGE_SLIDER_DURATION_VALUE) {
                    getCurrentPlaylist().getCurrentSong().getMediaPlayer().seek(
                            new Duration(newValueDouble * getCurrentPlaylist().getCurrentSong().
                                    getMediaPlayer().getTotalDuration().toMillis() / MAX_SLIDER_VALUE)
                    );
                }
            }
        });

        sliderVolume.valueProperty().addListener((observable, oldValue, newValue) -> {
            oldValueDouble = oldValue.doubleValue();
            newValueDouble = newValue.doubleValue();
            volume = newValueDouble;
            if (getCurrentPlaylist().getCurrentSong().getMediaPlayer() != null) {
                getCurrentPlaylist().getCurrentSong().getMediaPlayer().setVolume(volume / MAX_SLIDER_VALUE);
            }
            if (newValueDouble != MIN_VOLUME) {
                if (isMute) {
                    isMute = false;
                    refreshMuteButton();
                }
            } else {
                isMute = true;
                volume = MIN_VOLUME;
                refreshMuteButton();
            }
        });
    }

    private void initTreeMouseClickedListener() {
        treeView.setOnMouseClicked((event) -> {
            if (event.getClickCount() == DOUBLE_CLICK && treeView.getRoot() != null) {
                treeItem = treeView.getSelectionModel().getSelectedItem();
                if (treeItem != treeView.getRoot() &&
                        getCurrentPlaylist().setSearchSong(treeItem.getValue(), treeItem.getParent().getValue())) {
                    getCurrentPlaylist().playCurrentSong();
                    reCheckSong();
                }
            }
        });
    }

    private void readPlaylistList() {
        playlistNames = FXCollections.observableArrayList();

        if (new File(PLAYLISTS_PATH).listFiles().length < 1) {
            try {
                new File(PLAYLISTS_FILES + "default playlist" + PLAYLIST_EXPANTION).createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
        for (File file : new File(PLAYLISTS_PATH).listFiles()) {
            if (file.isFile()) {
                playlistNames.add(Song.stripExtension(file.getName()));
            }
        }
    }

    private void openFirstPlaylist() {
        if (playlistNames.size() > Song.INDEX_ZERO) {
            openPlaylist(playlistNames.get(Song.INDEX_ZERO));
        }
    }

    private void nextPlaylist() {
        index = playlistNames.indexOf(getCurrentPlaylist().getPlaylistName());
        System.out.println(index);
        if (index == playlistNames.size() - 1) {
            index = 0;
        } else {
            index++;
        }
        openPlaylist(playlistNames.get(index));
    }

    private void prevPlaylist() {
        index = playlistNames.indexOf(getCurrentPlaylist().getPlaylistName());
        if (index == 0) {
            index = playlistNames.size() - 1;
        } else {
            index--;
        }
    }

    private void openPlaylist(String name) {
        if (getCurrentPlaylist() != null) {
            getCurrentPlaylist().stopCurrentSong();
        }
        currentPlaylist = new Playlist(name);
        getCurrentPlaylist().setTreeView(treeView);
        getCurrentPlaylist().readPlaylist(PLAYLISTS_FILES + getCurrentPlaylist().getPlaylistName() + PLAYLIST_EXPANTION);
        getCurrentPlaylist().setFirstSong();

        endOfSongRunnable = () -> {
            if (isRePlay) {
                getCurrentPlaylist().rePlaySong();
            } else {
                if (isShuffle) {
                    getCurrentPlaylist().randomSong();
                } else {
                    getCurrentPlaylist().nextSong();
                }
            }

            if (stopCount.getCount() > 0) {
                stopCount.setCount(stopCount.getCount() - 1);
                refreshCountLabel();

                if (stopCount.getCount() == 0) {
                    getCurrentPlaylist().stopCurrentSong();
                }
            }

            reCheckSong();
        };

        reCheckSong();
        labelNamePlaylist.setText(getCurrentPlaylist().getPlaylistName());
    }

    private void reCheckSong() {
        refreshPictures();
        try {
            if (getCurrentPlaylist().getCurrentSong() == null) {
                return;
            }
            if (getCurrentPlaylist().getCurrentSong().getName().length() <= MAX_LENGTH_LABEL) {
                labelSongName.setText(getCurrentPlaylist().getCurrentSong().getName());
            } else {
                labelSongName.setText(getCurrentPlaylist().getCurrentSong().getName().substring(Song.INDEX_ZERO, MAX_LENGTH_LABEL));
            }

            chooseItemTreeView();

            labelSongDuration.setText(DEFAULT_TIME);

            if (getCurrentPlaylist().getCurrentSong().getMediaPlayer() == null) {
                return;
            }

            getCurrentPlaylist().getCurrentSong().getMediaPlayer().setOnReady(() -> {
                if (getCurrentPlaylist().getCurrentSong().getMediaPlayer() != null && labelSongDuration.getText().equals(DEFAULT_TIME)) {
                    duration = getCurrentPlaylist().getCurrentSong().getMediaPlayer().getTotalDuration();
                    currentDurationDouble = duration.toSeconds();
                    setLabelDuration(currentDurationDouble);
                }
            });

            getCurrentPlaylist().getCurrentSong().getMediaPlayer().currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                if (getCurrentPlaylist().getCurrentSong() != null && getCurrentPlaylist().getCurrentSong().getMediaPlayer() != null) {
                    oldValueDouble = oldValue.toMillis();
                    newValueDouble = newValue.toMillis();

                    duration = getCurrentPlaylist().getCurrentSong().getMediaPlayer().getTotalDuration();

                    if (labelSongDuration.getText().equals(DEFAULT_TIME)) {
                        setLabelDuration(duration.toSeconds());
                    }

                    if (Math.abs(oldValueDouble - newValueDouble) > MIN_CHANGE_DURATION) {
                        currentDuration = getCurrentPlaylist().getCurrentSong().getMediaPlayer().getCurrentTime();
                        durationDouble = duration.toSeconds();
                        currentDurationDouble = currentDuration.toSeconds();

                        sliderStatus.setValue(MAX_SLIDER_VALUE * currentDurationDouble / durationDouble);
                        labelSongNowTime.setText(String.format(TIME_FORMAT,
                                (int) (currentDurationDouble / SECOND_IN_MINUTE),
                                (int) (currentDurationDouble % SECOND_IN_MINUTE))
                        );
                    }
                }
            });

            getCurrentPlaylist().getCurrentSong().getMediaPlayer().setVolume(volume / MAX_SLIDER_VALUE);
            reSetEndListener();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setLabelDuration(double toralDuration) {
        labelSongDuration.setText(String.format(TIME_FORMAT,
                (int) (toralDuration / SECOND_IN_MINUTE),
                (int) (toralDuration % SECOND_IN_MINUTE)));
    }

    private void reSetEndListener() {
        if (getCurrentPlaylist().getCurrentSong().getMediaPlayer() != null) {
            getCurrentPlaylist().getCurrentSong().getMediaPlayer().setOnEndOfMedia(endOfSongRunnable);
        }
    }

    private void chooseItemTreeView() {
        treeView.getSelectionModel().select(getCurrentPlaylist().getCurrentSong().getNumber());
    }

    private void refreshMuteButton() {
        if (isMute) {
            buttonMute.setStyle(CUSTOM_BUTTON_PRESSED_STYLE);
        } else {
            buttonMute.setStyle(CUSTOM_BUTTON_DEFAULT_STYLE);
        }
    }

    private void refreshPictures() {
        try {
            if (getCurrentPlaylist().getCurrentSong() == null) {
                return;
            }

            isFindEXPANTION = false;
            pictureFile = new File(EMPTY_STRING);
            for (File file : new File(getCurrentPlaylist().getCurrentSong().getPathAbsoluteName()).
                    listFiles((dir, name) ->
                            name.endsWith(PNG_FILE_EXPANTION) ||
                                    name.endsWith(JPG_FILE_EXPANTION) ||
                                    name.endsWith(JPEG_FILE_EXPANTION))) {
                isFindEXPANTION = true;
                pictureFile = file;
                if (getCurrentPlaylist().getCurrentSong().getName().contains(Song.stripExtension(file.getName()))) {
                    songImage.setImage(new Image(file.toURI().toString()));
                    return;
                }
            }

            if (isFindEXPANTION) {
                songImage.setImage(new Image(pictureFile.toURI().toString()));
            } else {
                songImage.setImage(new Image(DEFAULT_SONG_IMAGE.toURI().toString()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readPlaylistFromPath(File file) {
        getCurrentPlaylist().stopCurrentSong();
        getCurrentPlaylist().readFromPath(file);
        getCurrentPlaylist().setFirstSong();
        reCheckSong();
        getCurrentPlaylist().writePlaylist();
    }

    private void createNewPlaylist() {
        playlistInfo = new PlaylistInfo(EMPTY_STRING);
        newPlaylistDialogController.setPlaylistInfo(playlistInfo);
        if (newPlaylistDialogStage == null) {
            newPlaylistDialogStage = new Stage();
            newPlaylistDialogStage.setTitle(resourceBundle.getString("key.dialog.newPlaylist.Title"));
            newPlaylistDialogStage.setMinWidth(300);
            newPlaylistDialogStage.setMinHeight(90);
            newPlaylistDialogStage.setResizable(false);
            newPlaylistDialogStage.setScene(new Scene(parentPlaylistDialog));
            newPlaylistDialogStage.initModality(Modality.WINDOW_MODAL);
            newPlaylistDialogStage.initOwner(getMainStage());
        }

        newPlaylistDialogStage.showAndWait();
        if (!playlistInfo.getPlaylistName().equals(EMPTY_STRING)) {
            try {
                new File(PLAYLISTS_FILES + playlistInfo.getPlaylistName() + PLAYLIST_EXPANTION).createNewFile();
                playlistNames.add(playlistInfo.getPlaylistName());
                if (playlistNames.size() < 2) {
                    currentPlaylist = new Playlist(playlistInfo.getPlaylistName());
                    labelNamePlaylist.setText(currentPlaylist.getPlaylistName());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void createStopTime() {
        stopTimeController.setStopTimeInfo(stopTimeInfo);
        if (stopTimeStage == null) {
            stopTimeStage = new Stage();
            stopTimeStage.setTitle(resourceBundle.getString("key.dialog.setStopTime.Title"));
            stopTimeStage.setMinWidth(300);
            stopTimeStage.setMinHeight(150);
            stopTimeStage.setResizable(false);
            stopTimeStage.setScene(new Scene(parentStopTime));
            stopTimeStage.initModality(Modality.WINDOW_MODAL);
            stopTimeStage.initOwner(getMainStage());
        }

        stopTimeStage.showAndWait();

        if (!stopTimeController.isCancel() && myThread != null) {
            myThread.stopNow();
        }

        if (!stopTimeController.isCancel() && stopTimeInfo.isChanged()) {
            System.out.println(stopTimeInfo);
            myThread = new MyThread(stopTimeInfo.getAllInSecond());
        }
    }

    private void createStopCount() {
        stopCountController.setStopCount(stopCount);

        if (stopCountStage == null) {
            stopCountStage = new Stage();
            stopCountStage.setTitle(resourceBundle.getString("key.dialog.stopCount.Title"));
            stopCountStage.setMinWidth(300);
            stopCountStage.setMinHeight(150);
            stopCountStage.setResizable(false);
            stopCountStage.setScene(new Scene(parentStopCount));
            stopCountStage.initModality(Modality.WINDOW_MODAL);
            stopCountStage.initOwner(getMainStage());
        }

        stopCountStage.showAndWait();

        if (!stopCountController.isCancel()) {
            labelCountLeft.setText(EMPTY_STRING);
        }

        if (!stopCountController.isCancel() && stopCount.isChanged()) {
            refreshCountLabel();
        }
    }

    private void createPlaylistList() {
        playlistListController.setNames(playlistNames);

        if (playlistListStage == null) {
            playlistListStage = new Stage();
            playlistListStage.setTitle(resourceBundle.getString("key.dialog.PlaylistList.Title"));
            playlistListStage.setMinWidth(400);
            playlistListStage.setMinHeight(250);
            playlistListStage.setResizable(false);
            playlistListStage.setScene(new Scene(parentPlaylistList));
            playlistListStage.initModality(Modality.WINDOW_MODAL);
            playlistListStage.initOwner(getMainStage());
        }

        playlistListStage.showAndWait();

        if (playlistNames.size() == 1) {
            openFirstPlaylist();
        }
    }

    private void createSetting() {
        if (settingsStage == null) {
            settingsStage = new Stage();
            settingsStage.setTitle(resourceBundle.getString("key.dialog.settings.Title"));
            settingsStage.setMinWidth(600);
            settingsStage.setMinHeight(400);
            settingsStage.setResizable(false);
            settingsStage.setScene(new Scene(parentSettings));
            settingsStage.initModality(Modality.WINDOW_MODAL);
            settingsStage.initOwner(getMainStage());
        }

        settingsStage.show();

    }

    private void refreshCountLabel() {
        labelCountLeft.setText(stopCount.getCount() + EMPTY_STRING);
    }

    public void actionButtonPressed(ActionEvent actionEvent) {

        if (!(actionEvent.getSource() instanceof Button)) {
            return;
        }

        switch (((Button) actionEvent.getSource()).getId()) {
            case "buttonSettings":
                createSetting();
                break;
            case "buttonGoToCurrentSong":
                chooseItemTreeView();
                treeView.scrollTo(getCurrentPlaylist().getCurrentSong().getNumber());
                break;
            case "buttonRewind":
                getCurrentPlaylist().prevSong();
                reCheckSong();
                break;
            case "buttonStop":
                getCurrentPlaylist().stopCurrentSong();
                //reCheckSong();
                break;
            case "buttonPlay":
                getCurrentPlaylist().playCurrentSong();
                reCheckSong();
                break;
            case "buttonPause":
                getCurrentPlaylist().pauseCurrentSong();
                //reCheckSong();
                break;
            case "buttonForward":
                getCurrentPlaylist().nextSong();
                reCheckSong();
                break;
            case "buttonReplaySong":
                createStopCount();
                break;
            case "buttonSetStopTime":
                createStopTime();
                break;
            case "buttonShuffle":
                isShuffle = !isShuffle;
                if (isShuffle) {
                    buttonShuffle.setStyle(CUSTOM_BUTTON_PRESSED_STYLE);
                } else {
                    buttonShuffle.setStyle(CUSTOM_BUTTON_DEFAULT_STYLE);
                }
                break;
            case "buttonReplay":
                isRePlay = !isRePlay;
                if (isRePlay) {
                    buttonReplay.setStyle(CUSTOM_BUTTON_PRESSED_STYLE);
                } else {
                    buttonReplay.setStyle(CUSTOM_BUTTON_DEFAULT_STYLE);
                }
                break;
            case "buttonMute":
                isMute = !isMute;
                refreshMuteButton();
                if (isMute) {
                    if (getCurrentPlaylist().getCurrentSong().getMediaPlayer() != null) {
                        getCurrentPlaylist().getCurrentSong().getMediaPlayer().setVolume(MIN_VOLUME);
                    }
                    volume = MIN_VOLUME;
                    sliderVolume.setValue(MIN_VOLUME);
                } else {
                    if (getCurrentPlaylist().getCurrentSong().getMediaPlayer() != null) {
                        getCurrentPlaylist().getCurrentSong().getMediaPlayer().setVolume(DEFAULT_VOLUME / MAX_SLIDER_VALUE);
                    }
                    volume = DEFAULT_VOLUME;
                    sliderVolume.setValue(DEFAULT_VOLUME);
                }
                break;
            case "buttonBack":
                prevPlaylist();
                break;
            case "buttonNext":
                nextPlaylist();
                break;
            case "buttonAddPlaylist":
                createNewPlaylist();
                break;
            case "buttonPlaylistList":
                createPlaylistList();
                break;
            case "buttonOpenFolder":
                directoryChooser = new DirectoryChooser();
                directoryChooser.setTitle(resourceBundle.getString("key.setDirectoryChooserTitle"));
                chooserPath = directoryChooser.showDialog(getMainStage());
                if (chooserPath != null) {
                    readPlaylistFromPath(chooserPath);
                }
                break;
            case "buttonOpenFiles":
                directoryChooser = new DirectoryChooser();
                directoryChooser.setTitle(resourceBundle.getString("key.dialog.FileChooser.Title"));
                chooserPath = directoryChooser.showDialog(getMainStage());
                if (chooserPath != null) {
                    if (treeView.getRoot() == null) {
                        readPlaylistFromPath(chooserPath);
                    } else {
                        getCurrentPlaylist().readAndAddFromPath(chooserPath);
                        getCurrentPlaylist().writePlaylist();
                    }
                }
                break;
            case "buttonSearch":

                break;
        }

    }

    public Stage getMainStage() {
        return mainStage;
    }

    public Playlist getCurrentPlaylist() {
        return currentPlaylist;
    }

    public class MyThread implements Runnable {
        private AtomicBoolean isStop = new AtomicBoolean(false);
        private AtomicInteger second;

        public MyThread(int second) {
            this.second = new AtomicInteger(second);
            if (second > 0) {
                new Thread(this).start();
            }
        }

        public void stopNow() {
            isStop.set(true);
            Platform.runLater(() -> labelLeftTime.setText(EMPTY_STRING));
        }

        public void run() {
            while (true) {
                try {
                    if (isStop.get()) {
                        break;
                    } else {
                        Platform.runLater(() -> labelLeftTime.setText(String.format(TIME_FORMAT_WITH_HOURS,
                                (second.get() / (SECOND_IN_MINUTE * SECOND_IN_MINUTE)),
                                ((second.get() % (SECOND_IN_MINUTE * SECOND_IN_MINUTE)) / SECOND_IN_MINUTE),
                                (second.get() % SECOND_IN_MINUTE))));

                        Thread.sleep(500);

                        Platform.runLater(() -> labelLeftTime.setText(EMPTY_STRING));

                        Thread.sleep(500);


                        second.decrementAndGet();
                        if (second.get() < 1) {
                            break;
                        }
                    }
                } catch (Exception e) {

                }
            }

            if (second.get() < 1) {
                Platform.exit();
                System.exit(0);
            }
        }
    }

}