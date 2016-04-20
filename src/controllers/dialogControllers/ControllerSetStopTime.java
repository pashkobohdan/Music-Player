package controllers.dialogControllers;

import controllers.Controller;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import objects.Song;
import objects.StopTimeInfo;

import java.net.URL;
import java.util.ResourceBundle;

public class ControllerSetStopTime extends DialogController implements Initializable {
    public static final int hoursInDay = 24;


    @FXML
    private Button buttonOk, buttonCancel, buttonDelete;
    @FXML
    private Label labelHours, labelMinute, labelSecond;
    @FXML
    private ComboBox<Integer> comboboxHours, comboboxMinute, comboboxSecond;

    private StopTimeInfo stopTimeInfo;

    private boolean isCancel = true;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        for (int i = Song.INDEX_ZERO; i < hoursInDay; i++) {
            comboboxHours.getItems().add(i);
        }
        for (int i = Song.INDEX_ZERO; i < Data.SECOND_IN_MINUTE; i++) {
            comboboxMinute.getItems().add(i);
        }
        for (int i = Song.INDEX_ZERO; i < Data.SECOND_IN_MINUTE; i++) {
            comboboxSecond.getItems().add(i);
        }
    }

    public void setStopTimeInfo(StopTimeInfo stopTimeInfo) {
        isCancel = false;
        this.stopTimeInfo = stopTimeInfo;
        comboboxHours.getSelectionModel().select(stopTimeInfo.getHours());
        comboboxMinute.getSelectionModel().select(stopTimeInfo.getMinute());
        comboboxSecond.getSelectionModel().select(stopTimeInfo.getSecond());
    }

    public void setResourceBundle(ResourceBundle resourceBundle) {
        labelHours.setText(resourceBundle.getString("key.dialog.stopTime.hours"));
        labelMinute.setText(resourceBundle.getString("key.dialog.stopTime.minute"));
        labelSecond.setText(resourceBundle.getString("key.dialog.stopTime.second"));

        buttonOk.setText(resourceBundle.getString("key.button.ok"));
        buttonCancel.setText(resourceBundle.getString("key.button.cancel"));
        buttonDelete.setText(resourceBundle.getString("key.button.delete"));
    }

    public void close(ActionEvent actionEvent) {
        (((Node) actionEvent.getSource()).getScene().getWindow()).hide();
    }

    public void refreshStopTimeInfo() {
        stopTimeInfo.setHours(comboboxHours.getValue());
        stopTimeInfo.setMinute(comboboxMinute.getValue());
        stopTimeInfo.setSecond(comboboxSecond.getValue());
    }

    public void deleteStopTime() {
        comboboxHours.getSelectionModel().select(StopTimeInfo.DEFAULT_VALUES);
        comboboxMinute.getSelectionModel().select(StopTimeInfo.DEFAULT_VALUES);
        comboboxSecond.getSelectionModel().select(StopTimeInfo.DEFAULT_VALUES);

        refreshStopTimeInfo();
    }

    public void actionButtonPressed(ActionEvent actionEvent) {
        if (!(actionEvent.getSource() instanceof Button)) {
            return;
        }

        switch (((Button) actionEvent.getSource()).getId()) {
            case "buttonOk":
                refreshStopTimeInfo();
                close(actionEvent);
                break;
            case "buttonCancel":
                isCancel = true;
                close(actionEvent);
                break;
            case "buttonDelete":
                deleteStopTime();
                close(actionEvent);
                break;
        }
    }

    public boolean isCancel() {
        return isCancel;
    }
}
