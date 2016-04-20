package controllers;

import controllers.dialogControllers.*;
import javafx.application.Platform;
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
    private double volume = Data.DEFAULT_VOLUME;
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


        textSearch.textProperty().addListener((observable, oldValue, newValue) -> {
                    getCurrentPlaylist().setTreeViewFromSearchString(textSearch.getText());
                }
        );
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
            fxmlLoaderStopTime.setLocation(getClass().getResource(Data.STOP_TIME_DIALOG_PATH));
            parentStopTime = fxmlLoaderStopTime.load();
            stopTimeController = fxmlLoaderStopTime.getController();
            stopTimeController.setResourceBundle(resourceBundle);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initStopCountWindow() {
        try {
            fxmlLoaderStopCount.setLocation(getClass().getResource(Data.STOP_COUNTER_DIALOG_PATH));
            parentStopCount = fxmlLoaderStopCount.load();
            stopCountController = fxmlLoaderStopCount.getController();
            stopCountController.setResourceBundle(resourceBundle);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initPlaylistListWindow() {
        try {
            fxmlLoaderPlaylistList.setLocation(getClass().getResource(Data.PLAYLIST_LIST_DIALOG_PATH));
            parentPlaylistList = fxmlLoaderPlaylistList.load();
            playlistListController = fxmlLoaderPlaylistList.getController();
            playlistListController.setResourceBundle(resourceBundle);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initSettingsWindow() {
        try {
            fxmlLoaderSettings.setLocation(getClass().getResource(Data.SETTINGS_DIALOG_PATH));
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
    }

    private void initSlidersListeners() {
        sliderStatus.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (getCurrentPlaylist() != null &&
                    getCurrentPlaylist().getCurrentSong() != null &&
                    getCurrentPlaylist().getCurrentSong().getMediaPlayer() != null) {
                oldValueDouble = oldValue.doubleValue();
                newValueDouble = newValue.doubleValue();
                if (Math.abs(oldValueDouble - newValueDouble) > Data.MIN_CHANGE_SLIDER_DURATION_VALUE) {
                    getCurrentPlaylist().getCurrentSong().getMediaPlayer().seek(
                            new Duration(newValueDouble * getCurrentPlaylist().getCurrentSong().
                                    getMediaPlayer().getTotalDuration().toMillis() / Data.MAX_SLIDER_VALUE)
                    );
                }
            }
        });

        sliderVolume.valueProperty().addListener((observable, oldValue, newValue) -> {
            oldValueDouble = oldValue.doubleValue();
            newValueDouble = newValue.doubleValue();
            volume = newValueDouble;
            if (getCurrentPlaylist().getCurrentSong().getMediaPlayer() != null) {
                getCurrentPlaylist().getCurrentSong().getMediaPlayer().setVolume(volume / Data.MAX_SLIDER_VALUE);
            }
            if (newValueDouble != Data.MIN_VOLUME) {
                if (isMute) {
                    isMute = false;
                    refreshMuteButton();
                }
            } else {
                isMute = true;
                volume = Data.MIN_VOLUME;
                refreshMuteButton();
            }
        });
    }

    private void initTreeMouseClickedListener() {
        treeView.setOnMouseClicked((event) -> {
            if (event.getClickCount() == Data.DOUBLE_CLICK && treeView.getRoot() != null) {
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

        if (new File(Data.PLAYLISTS_PATH) != null && new File(Data.PLAYLISTS_PATH).listFiles().length < 1) {
            try {
                new File(Data.PLAYLISTS_FILES + "default playlist" + Data.PLAYLIST_EXPANTION).createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
        for (File file : new File(Data.PLAYLISTS_PATH).listFiles()) {
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
        index = playlistNames.indexOf(currentPlaylist.getPlaylistName());
        if (index >= playlistNames.size() - 1) {
            index = Song.INDEX_ZERO;
        } else {
            index++;
        }
        openPlaylist(playlistNames.get(index));
    }

    private void prevPlaylist() {
        index = playlistNames.indexOf(currentPlaylist.getPlaylistName());
        if (index <= Song.INDEX_ZERO) {
            index = playlistNames.size() - 1;
        } else {
            index--;
        }
        openPlaylist(playlistNames.get(index));
    }

    private void openPlaylist(String name) {
        if (getCurrentPlaylist() != null) {
            getCurrentPlaylist().stopCurrentSong();
        }
        currentPlaylist = new Playlist(name);
        getCurrentPlaylist().setTreeView(treeView);
        getCurrentPlaylist().readPlaylist(Data.PLAYLISTS_FILES + getCurrentPlaylist().getPlaylistName() + Data.PLAYLIST_EXPANTION);
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

            if (stopCount.getCount() > Song.INDEX_ZERO) {
                stopCount.setCount(stopCount.getCount() - 1);
                refreshCountLabel();

                if (stopCount.getCount() == Song.INDEX_ZERO) {
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
            if (getCurrentPlaylist().getCurrentSong().getName().length() <= Data.MAX_LENGTH_LABEL) {
                labelSongName.setText(getCurrentPlaylist().getCurrentSong().getName());
            } else {
                labelSongName.setText(getCurrentPlaylist().getCurrentSong().getName().substring(Song.INDEX_ZERO, Data.MAX_LENGTH_LABEL));
            }

            chooseItemTreeView();

            labelSongDuration.setText(Data.DEFAULT_TIME);

            if (getCurrentPlaylist().getCurrentSong().getMediaPlayer() == null) {
                return;
            }

            getCurrentPlaylist().getCurrentSong().getMediaPlayer().setOnReady(() -> {
                if (getCurrentPlaylist().getCurrentSong().getMediaPlayer() != null && labelSongDuration.getText().equals(Data.DEFAULT_TIME)) {
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

                    if (labelSongDuration.getText().equals(Data.DEFAULT_TIME)) {
                        setLabelDuration(duration.toSeconds());
                    }

                    if (Math.abs(oldValueDouble - newValueDouble) > Data.MIN_CHANGE_DURATION) {
                        currentDuration = getCurrentPlaylist().getCurrentSong().getMediaPlayer().getCurrentTime();
                        durationDouble = duration.toSeconds();
                        currentDurationDouble = currentDuration.toSeconds();

                        sliderStatus.setValue(Data.MAX_SLIDER_VALUE * currentDurationDouble / durationDouble);
                        labelSongNowTime.setText(String.format(Data.TIME_FORMAT,
                                (int) (currentDurationDouble / Data.SECOND_IN_MINUTE),
                                (int) (currentDurationDouble % Data.SECOND_IN_MINUTE))
                        );
                    }
                }
            });

            getCurrentPlaylist().getCurrentSong().getMediaPlayer().setVolume(volume / Data.MAX_SLIDER_VALUE);
            reSetEndListener();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setLabelDuration(double toralDuration) {
        labelSongDuration.setText(String.format(Data.TIME_FORMAT,
                (int) (toralDuration / Data.SECOND_IN_MINUTE),
                (int) (toralDuration % Data.SECOND_IN_MINUTE)));
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
            buttonMute.setStyle(Data.CUSTOM_BUTTON_PRESSED_STYLE);
        } else {
            buttonMute.setStyle(Data.CUSTOM_BUTTON_DEFAULT_STYLE);
        }
    }

    private void refreshPictures() {
        try {
            if (getCurrentPlaylist().getCurrentSong() == null) {
                return;
            }

            isFindEXPANTION = false;
            pictureFile = new File(Data.EMPTY_STRING);
            for (File file : new File(getCurrentPlaylist().getCurrentSong().getPathAbsoluteName()).
                    listFiles((dir, name) ->
                            name.endsWith(Data.PNG_FILE_EXPANTION) ||
                                    name.endsWith(Data.JPG_FILE_EXPANTION) ||
                                    name.endsWith(Data.JPEG_FILE_EXPANTION))) {
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
                songImage.setImage(new Image(Data.DEFAULT_SONG_IMAGE.toURI().toString()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readPlaylistFromPath(File file) {
        currentPlaylist.stopCurrentSong();
        currentPlaylist.readFromPath(file);
        currentPlaylist.setFirstSong();
        reCheckSong();
        currentPlaylist.writePlaylist();
    }

    private void createNewPlaylist() {
        playlistInfo = new PlaylistInfo(Data.EMPTY_STRING);
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
        if (!playlistInfo.getPlaylistName().equals(Data.EMPTY_STRING)) {
            try {
                new File(Data.PLAYLISTS_FILES + playlistInfo.getPlaylistName() + Data.PLAYLIST_EXPANTION).createNewFile();
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
            labelCountLeft.setText(Data.EMPTY_STRING);
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
        labelCountLeft.setText(stopCount.getCount() + Data.EMPTY_STRING);
    }

    public Stage getMainStage() {
        return mainStage;
    }

    public Playlist getCurrentPlaylist() {
        return currentPlaylist;
    }

    /**
     * @param actionEvent button pressed event
     */
    public void buttonSettingsPressed(ActionEvent actionEvent) {
        createSetting();
    }

    public void buttonGoToCurrentSongPressed(ActionEvent actionEvent) {
        chooseItemTreeView();
        treeView.scrollTo(getCurrentPlaylist().getCurrentSong().getNumber());
    }

    public void buttonRewindPressed(ActionEvent actionEvent) {
        getCurrentPlaylist().prevSong();
        reCheckSong();
    }

    public void buttonStopPressed(ActionEvent actionEvent) {
        getCurrentPlaylist().stopCurrentSong();
    }

    public void buttonPlayPressed(ActionEvent actionEvent) {
        getCurrentPlaylist().playCurrentSong();
        reCheckSong();
    }

    public void buttonPausePressed(ActionEvent actionEvent) {
        getCurrentPlaylist().pauseCurrentSong();
    }

    public void buttonForwardPressed(ActionEvent actionEvent) {
        getCurrentPlaylist().nextSong();
        reCheckSong();
    }

    public void buttonSetStopTimePressed(ActionEvent actionEvent) {
        createStopTime();
    }

    public void buttonReplaySongPressed(ActionEvent actionEvent) {
        createStopCount();
    }

    public void buttonShufflePressed(ActionEvent actionEvent) {
        isShuffle = !isShuffle;
        if (isShuffle) {
            buttonShuffle.setStyle(Data.CUSTOM_BUTTON_PRESSED_STYLE);
        } else {
            buttonShuffle.setStyle(Data.CUSTOM_BUTTON_DEFAULT_STYLE);
        }
    }

    public void buttonReplayPressed(ActionEvent actionEvent) {
        isRePlay = !isRePlay;
        if (isRePlay) {
            buttonReplay.setStyle(Data.CUSTOM_BUTTON_PRESSED_STYLE);
        } else {
            buttonReplay.setStyle(Data.CUSTOM_BUTTON_DEFAULT_STYLE);
        }
    }

    public void buttonMutePressed(ActionEvent actionEvent) {
        isMute = !isMute;
        refreshMuteButton();
        if (isMute) {
            if (getCurrentPlaylist().getCurrentSong().getMediaPlayer() != null) {
                getCurrentPlaylist().getCurrentSong().getMediaPlayer().setVolume(Data.MIN_VOLUME);
            }
            volume = Data.MIN_VOLUME;
            sliderVolume.setValue(Data.MIN_VOLUME);
        } else {
            if (getCurrentPlaylist().getCurrentSong().getMediaPlayer() != null) {
                getCurrentPlaylist().getCurrentSong().getMediaPlayer().setVolume(Data.DEFAULT_VOLUME / Data.MAX_SLIDER_VALUE);
            }
            volume = Data.DEFAULT_VOLUME;
            sliderVolume.setValue(Data.DEFAULT_VOLUME);
        }
    }

    public void buttonBackPressed(ActionEvent actionEvent) {
        prevPlaylist();
    }

    public void buttonNextPressed(ActionEvent actionEvent) {
        nextPlaylist();
    }

    public void buttonAddPlaylistPressed(ActionEvent actionEvent) {
        createNewPlaylist();
    }

    public void buttonPlaylistListPressed(ActionEvent actionEvent) {
        createPlaylistList();
    }

    public void buttonOpenFolderPressed(ActionEvent actionEvent) {
        directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle(resourceBundle.getString("key.setDirectoryChooserTitle"));
        chooserPath = directoryChooser.showDialog(getMainStage());
        if (chooserPath != null) {
            readPlaylistFromPath(chooserPath);
        }
    }

    public void buttonOpenFilesPressed(ActionEvent actionEvent) {
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
    }

    /**
     * Class for close window after some time
     */
    public class MyThread implements Runnable {
        private AtomicBoolean isStop = new AtomicBoolean(false);
        private AtomicInteger second;

        public MyThread(int second) {
            this.second = new AtomicInteger(second);
            if (second > Song.INDEX_ZERO) {
                new Thread(this).start();
            }
        }

        public void stopNow() {
            isStop.set(true);
            Platform.runLater(() -> labelLeftTime.setText(Data.EMPTY_STRING));
        }

        public void run() {
            while (true) {
                try {
                    if (isStop.get()) {
                        break;
                    } else {
                        Platform.runLater(() -> labelLeftTime.setText(String.format(Data.TIME_FORMAT_WITH_HOURS,
                                (second.get() / (Data.SECOND_IN_MINUTE * Data.SECOND_IN_MINUTE)),
                                ((second.get() % (Data.SECOND_IN_MINUTE * Data.SECOND_IN_MINUTE)) / Data.SECOND_IN_MINUTE),
                                (second.get() % Data.SECOND_IN_MINUTE))));

                        Thread.sleep(Data.sleepHalfSecond);

                        Platform.runLater(() -> labelLeftTime.setText(Data.EMPTY_STRING));

                        Thread.sleep(Data.sleepHalfSecond);


                        second.decrementAndGet();
                        if (second.get() < 1) {
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (second.get() < 1) {
                Platform.exit();
                System.exit(0);
            }
        }
    }

}