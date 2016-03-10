package controllers;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;

public class ControllerPlaylistList {
    @FXML
    private Button buttonAdd, buttonDelete;
    @FXML
    private ListView<String> listNames;
    @FXML
    private TextField textFiledName;

    private String defaultPlaylistName;

    public void setResourceBundle(ResourceBundle resourceBundle) {
        buttonAdd.setText(resourceBundle.getString("key.button.add"));
        buttonDelete.setText(resourceBundle.getString("key.button.delete"));

        buttonAdd.setTooltip(new Tooltip(resourceBundle.getString("key.dialog.playlistList.buttonAdd.TolTip")));
        buttonDelete.setTooltip(new Tooltip(resourceBundle.getString("key.dialog.playlistList.buttonDelete.ToolTip")));

        textFiledName.setText(resourceBundle.getString("key.dialog.playlistList.textFieldName.text"));
        defaultPlaylistName = textFiledName.getText();
    }

    public void setNames(ObservableList<String> observableList) {
        textFiledName.setText(defaultPlaylistName);

        listNames.setEditable(true);
        listNames.setCellFactory(TextFieldListCell.forListView());
        listNames.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        listNames.setItems(observableList);
        listNames.getSelectionModel().selectFirst();

        textFiledName.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {
                    createFile();
                }
            }
        });
    }

    public void createFile() {
        if (textFiledName.getText().length() < 1 || textFiledName.getText().equals(defaultPlaylistName)) {
            return;
        }
        try {
            new File(Controller.PLAYLISTS_FILES + textFiledName.getText() + Controller.PLAYLIST_EXPANTION).createNewFile();

            listNames.getItems().add(textFiledName.getText());
            listNames.getSelectionModel().selectLast();
            textFiledName.setText(Controller.EMPTY_STRING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteFile() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete \"" + listNames.getSelectionModel().getSelectedItem() + "\" playlist ?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            if (new File(Controller.PLAYLISTS_FILES + listNames.getSelectionModel().getSelectedItem() + Controller.PLAYLIST_EXPANTION).delete()) {
                listNames.getItems().remove(listNames.getSelectionModel().getSelectedItem());
            }
        }
    }

    public void actionButtonClicked(ActionEvent actionEvent) {
        if (!(actionEvent.getSource() instanceof Button)) {
            return;
        }

        switch (((Button) actionEvent.getSource()).getId()) {
            case "buttonAdd":
                createFile();
                break;

            case "buttonDelete":
                deleteFile();
                break;
        }
    }
}
