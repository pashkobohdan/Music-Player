package controllers.dialogControllers;

import controllers.Controller;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import objects.PlaylistInfo;
import java.util.ResourceBundle;

public class ControllerNewPlaylist extends DialogController{
    @FXML
    private Button buttonOk, buttonCancel;
    @FXML
    private TextField playlistName;
    @FXML
    private Label labelSetName;

    private PlaylistInfo playlistInfo;


    public void setResourceBundle(ResourceBundle resourceBundle) {
        labelSetName.setText(resourceBundle.getString("key.dialog.newPlaylist.LabelText"));
        playlistName.setText(resourceBundle.getString("key.dialog.newPlaylist.defaultName"));
    }

    public void setPlaylistInfo(PlaylistInfo playlistInfo) {
        this.playlistInfo = playlistInfo;
    }

    public void buttonOkPressed(ActionEvent actionEvent) {
        playlistInfo.setPlaylistName(playlistName.getText());

        (((Node) actionEvent.getSource()).getScene().getWindow()).hide();
    }

    public void buttonCancelPressed(ActionEvent actionEvent) {
        playlistInfo.setPlaylistName(Controller.EMPTY_STRING);

        (((Node) actionEvent.getSource()).getScene().getWindow()).hide();
    }
}