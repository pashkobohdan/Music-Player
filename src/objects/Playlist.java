package objects;

import MyLibrary.ReadWrite;
import controllers.Controller;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Playlist {
    public static final String ROOT_PATH_NAME = "Music";
    public static final String PROJECT_PATH = System.getProperty("user.dir");
    public static final String PLAYLIST_PATH = PROJECT_PATH + "\\src\\playlists";
    public static final String PLAYLIST_FILES = PLAYLIST_PATH + "\\";
    public static final String PLAYLIST_EXPANTION = ".mppl";
    public static final String LINE_SEPARATOR = "\n";

    private ArrayList<Song> songArrayList;
    private Song currentSong;
    private TreeView<String> treeView;
    private String playlistName;

    private TreeItem<String> normalRootItem;
    private TreeItem<String> rootItem;
    private TreeItem<String> itemPath;
    private TreeItem<String> itemSong;

    private Song workSong;
    private int number;
    private String nameOpenPath ;


    public Playlist(String playlistName){
        this.playlistName = playlistName;
    }

    public void setFirstSong(){
        if(songArrayList.size()> Song.INDEX_ZERO) {
            currentSong = getSongArrayList().get(Song.INDEX_ZERO);
        }
    }
    public void playCurrentSong(){
        if(currentSong!=null) {
            if(currentSong.isPlay()){
                currentSong.stop();
            }
            currentSong.play();
        }
    }
    public void pauseCurrentSong(){
        if(currentSong!=null) {
            currentSong.pause();
        }
    }
    public void stopCurrentSong(){
        if(currentSong!=null && currentSong.getMediaPlayer()!=null) {
            currentSong.stop();
        }
    }
    public void nextSong(){
        boolean isPlay = currentSong==null?false:currentSong.isPlay();
        currentSong.stop();

        currentSong.clearSong();

        int index = getSongArrayList().indexOf(currentSong);
        if(index == getSongArrayList().size() - 1){
            index = Song.INDEX_ZERO;
        }
        else{
            index++;
        }
        currentSong = getSongArrayList().get(index);
        if(isPlay){
            currentSong.play();
        }
    }
    public void prevSong(){
        boolean isPlay = currentSong==null?false:currentSong.isPlay();
        currentSong.stop();

        currentSong.clearSong();

        int index= getSongArrayList().indexOf(currentSong);
        if(index == Song.INDEX_ZERO){
            index = getSongArrayList().size()-1;
        }
        else{
            index--;
        }
        currentSong = getSongArrayList().get(index);
        if(isPlay){
            currentSong.play();
        }
    }
    public void randomSong(){
        boolean isPlay = currentSong==null?false:currentSong.isPlay();
        currentSong.stop();
        currentSong = getSongArrayList().get(new Random(System.nanoTime()).nextInt(getSongArrayList().size()));
        if(isPlay){
            currentSong.play();
        }
    }
    public void rePlaySong(){
        boolean isPlay = currentSong==null ? false : currentSong.isPlay();
        currentSong.stop();
        currentSong = getSongArrayList().get(getSongArrayList().indexOf(currentSong));
        currentSong.loadSong();
        if(isPlay){
            currentSong.play();
        }
    }

    public boolean setSearchSong(String name, String path){
        for(Song argSong : getSongArrayList()){
            if(argSong.getName().equals(name) && argSong.getPathName().equals(path)){
                if(currentSong.getMediaPlayer()!=null) {
                    currentSong.stop();
                }
                currentSong=argSong;
                playCurrentSong();
                return true;
            }
        }
        return false;
    }     // установка щелкнутой песни

    public void readPlaylist(String fileName){
        songArrayList = new ArrayList<>();
        try {
            String[] files = ReadWrite.readToArray(fileName, LINE_SEPARATOR);

            treeView.setRoot(null);

            if(files == null){
                return;
            }

            int numberItem = 1;

            rootItem = new TreeItem<>(ROOT_PATH_NAME);
            rootItem.setExpanded(true);
            itemPath =new TreeItem<>(ROOT_PATH_NAME);

            Song nowSong;
            String path = Controller.EMPTY_STRING;
            for (String arg : files){
                if(isDirectory(arg)){
                    if(!path.equals(arg)) {
                        path = arg;
                        itemPath=new TreeItem<>(path);
                        itemPath.setExpanded(true);
                        rootItem.getChildren().add(itemPath);
                        numberItem++;
                    }
                }
                else{
                    nowSong=new Song(arg);
                    nowSong.setNumber(numberItem++);
                    songArrayList.add(nowSong);
                    itemSong=new TreeItem<>(nowSong.getName());
                    itemPath.getChildren().add(itemSong);
                }
            }

            normalRootItem = rootItem;
            treeView.setRoot(rootItem);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }               // чтение с файла плейлиста
    private boolean isDirectory(String fileName){
        if(fileName.contains(Controller.MP3_FILE_EXPANTION) ||
                fileName.contains(Controller.PNG_FILE_EXPANTION) || 
                fileName.contains(Controller.JPG_FILE_EXPANTION) || 
                fileName.contains(Controller.JPEG_FILE_EXPANTION)){
            return false;
        }
        return true;
    }
    public void writePlaylist(){
        if(songArrayList ==null){
            return;
        }

        try (FileOutputStream fileOutputStream = new FileOutputStream(PLAYLIST_FILES+ playlistName + PLAYLIST_EXPANTION)){
            String path = songArrayList.get(Song.INDEX_ZERO).getPathName();
            fileOutputStream.write((path+LINE_SEPARATOR).getBytes());

            for(Song song : songArrayList){
                if(song.getFullname().contains(Controller.MP3_FILE_EXPANTION)){
                    if (!path.equals(song.getPathName())) {
                        fileOutputStream.write((song.getPathName() + LINE_SEPARATOR).getBytes());
                        path = song.getPathName();
                    }
                    fileOutputStream.write((song.getFullname() + LINE_SEPARATOR).getBytes());
                }
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    private void addPath(String path){
        if(!new File(path).getName().equals(nameOpenPath)) {
            itemPath = new TreeItem<>(new File(path).getName());
            itemPath.setExpanded(true);
            rootItem.getChildren().add(itemPath);
            number++;
        }
        for(File file : new File(path).listFiles()){
            if(file.isFile() && file.getAbsolutePath().contains(Controller.MP3_FILE_EXPANTION)){
                workSong=new Song(file);
                workSong.setNumber(number++);
                songArrayList.add(workSong);
                itemSong=new TreeItem<>(workSong.getName());
                if(itemPath == null){
                    rootItem.getChildren().add(itemSong);
                }
                else{
                    itemPath.getChildren().add(itemSong);
                }
            }
        }
        for(File file : new File(path).listFiles()){
            if(file.isDirectory()){
                addPath(file.getAbsolutePath());
            }
        }
    }                       // чтение файлов с заданой папки
    public void readFromPath(File file){
        nameOpenPath = file.getName();
        rootItem = new TreeItem<>(file.getName());
        rootItem.setExpanded(true);
        itemPath = null;

        songArrayList = new ArrayList<>();
        number = 1;
        addPath(file.getAbsolutePath());

        normalRootItem = rootItem;
        treeView.setRoot(rootItem);
    }
    public void readAndAddFromPath(File file){
        nameOpenPath = songArrayList.get(songArrayList.size() - 1).getPathName();
        rootItem = treeView.getRoot();
        rootItem.setExpanded(true);
        itemPath = null;

        number = songArrayList.get(songArrayList.size() - 1).getNumber() + 1;

        addPath(file.getAbsolutePath());

        normalRootItem = rootItem;
        treeView.setRoot(rootItem);
    }

    public void setTreeViewFromSearchString(String searchString){
        if(searchString.equals(Controller.EMPTY_STRING)){
            treeView.setRoot(normalRootItem);
            return;
        }

        searchString = searchString.toLowerCase();

        rootItem = new TreeItem<>(ROOT_PATH_NAME);
        rootItem.setExpanded(true);
        itemPath = null;

        String path = "";
        int index = 1;

        String normalName, normalPathName ;

        for (Song song : songArrayList){
            normalName = songNameToNormalName(song.getName());
            normalPathName = songNameToNormalName(song.getPathName());

            if(compareString(normalName, searchString) || compareString(normalPathName, searchString)){
                if(!song.getPathName().equals(path)) {
                    path = song.getPathName();
                    itemPath = new TreeItem<>(path);
                    itemPath.setExpanded(true);
                    rootItem.getChildren().add(itemPath);
                    index++ ;
                }
                song.setNumber(index++);
                itemPath.getChildren().add(new TreeItem<>(song.getName()));
            }
        }

        if(rootItem.getChildren().size() > Song.INDEX_ZERO) {
            treeView.setRoot(rootItem);
        }
        else {
            treeView.setRoot(null);
        }
    }
    private String songNameToNormalName(String source){
        String result = source.
                toLowerCase().
                replaceAll("\\+"," ").
                replaceAll("_"," ").
                replaceAll("-"," ").
                replaceAll("\\("," (").
                replaceAll("\\)",") ");
        while (result.contains("  ")){
            result = result.replaceAll("  "," ");
        }
        return result;
    }
    private boolean compareString(String base, String search){
        for(String arg : search.split(" ")){
            if(!base.contains(arg)){
                return false;
            }
        }
        return true;
    }

    public void setTreeView(TreeView treeView){
        this.treeView=treeView;
    }
    public TreeView<String> getTreeView() {
        return treeView;
    }
    public ArrayList<Song> getSongArrayList() {
        return songArrayList;
    }
    public Song getCurrentSong() {
        return currentSong;
    }
    public void setCurrentSong(Song currentSong) {
        this.currentSong = currentSong;
    }
    public String getPlaylistName() {
        return playlistName;
    }
    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

}
