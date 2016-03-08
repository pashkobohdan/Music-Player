package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import objects.StopCount;
import java.net.URL;
import java.util.ResourceBundle;

public class ControllerCounterStop implements Initializable{
    @FXML private Button buttonOk,buttonCancel,buttonDelete;
    @FXML private ComboBox<Integer> comboBoxCount;
    @FXML private TextField textFieldCount;
    @FXML private Label labelCount;

    private StopCount stopCount;

    private boolean isCancel = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        for(int i = 0;i<100;i++){
            comboBoxCount.getItems().add(i);
        }

    }

    public void setResourceBundle(ResourceBundle resourceBundle){
        labelCount.setText(resourceBundle.getString("key.dialog.countStop.labelCount"));

        buttonOk.setText(resourceBundle.getString("key.button.ok"));
        buttonCancel.setText(resourceBundle.getString("key.button.cancel"));
        buttonDelete.setText(resourceBundle.getString("key.button.delete"));


        textFieldCount.setText(resourceBundle.getString("key,dialog.countStopDialog.textField.defaultText"));
    }


    public void close(ActionEvent actionEvent){
        (((Node)actionEvent.getSource()).getScene().getWindow()).hide();
    }

    public void refreshStopCountInfo(){
        try{
            stopCount.setCount(Integer.parseInt(textFieldCount.getText()));
            textFieldCount.setText("");
        }
        catch (NumberFormatException e){
            stopCount.setCount(comboBoxCount.getValue());
        }

    }
    public void deleteStopCount(){
        stopCount.setCount(StopCount.DEFAULT_COUNT);
        comboBoxCount.getSelectionModel().select(StopCount.DEFAULT_COUNT);
        textFieldCount.setText(StopCount.DEFAULT_COUNT+Controller.EMPTY_STRING);

        refreshStopCountInfo();
    }

    public void actionButtonClicked(ActionEvent actionEvent) {
        if(!(actionEvent.getSource() instanceof Button)){
            return;
        }

        switch (((Button)actionEvent.getSource()).getId()){
            case "buttonOk":
                refreshStopCountInfo();
                close(actionEvent);
                break;
            case "buttonCancel":
                isCancel = true;
                close(actionEvent);
                break;
            case "buttonDelete":
                deleteStopCount();
                close(actionEvent);
                break;
        }
    }

    public void setStopCount(StopCount stopCount) {
        this.stopCount = stopCount;
        isCancel = false;

        textFieldCount.setText(stopCount.getCount()+Controller.EMPTY_STRING);
        comboBoxCount.getSelectionModel().select(stopCount.getCount());
    }

    public boolean isCancel() {
        return isCancel;
    }
}
