package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import objects.LocaleInfo;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class ControllerSettings implements Initializable{
    @FXML private Label labelLanguage;
    @FXML private Button buttonApply;
    @FXML private ComboBox<String> comboboxLanguages;

    private List<String> languages;
    private Controller controller;

    private Alert alert;
    private ButtonType buttonRestartNow;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        Locale locale;
        languages =  LocaleInfo.languagesList();
        for(String language : languages){
            locale = new Locale(language);
            comboboxLanguages.getItems().add(locale.getDisplayLanguage(locale));
        }

        locale = new Locale(LocaleInfo.readMainLanguage());
        comboboxLanguages.getSelectionModel().select(locale.getDisplayLanguage(locale));
    }
    public void setResourceBundle(ResourceBundle resourceBundle) {
        labelLanguage.setText(resourceBundle.getString("key.dialog.settings.labelLanguages.Text"));
        buttonApply.setText(resourceBundle.getString("key.button.apply"));

        buttonRestartNow = new ButtonType(resourceBundle.getString("key.button.restartNow"));
        alert = new Alert(Alert.AlertType.INFORMATION, resourceBundle.getString("key.dialog.restartNow.text"),
                ButtonType.OK,
                buttonRestartNow);
        alert.setTitle(resourceBundle.getString("key.dialog.restartNow.title"));
        alert.setHeaderText(resourceBundle.getString("key.dialog.restartNow.header"));

    }

    public void actionButtonClicked(ActionEvent actionEvent) {
        if(!(actionEvent.getSource() instanceof Button)){
            return;
        }

        switch (((Button)actionEvent.getSource()).getId()){
            case "buttonApply" :
                buttonApply();
                break;
        }
    }
    private void buttonApply() {

        alert.showAndWait();

        LocaleInfo.writeMainLanguage(languages.get(comboboxLanguages.getSelectionModel().getSelectedIndex()));


        if (alert.getResult() == buttonRestartNow) {

            try {
                controller.getCurrentPlaylist().stopCurrentSong();
                controller.getMainStage().close();

                Thread.sleep(50);

                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("../fxml/main.fxml"));
                fxmlLoader.setResources(ResourceBundle.getBundle("bundles.Locale", new Locale(languages.get(comboboxLanguages.getSelectionModel().getSelectedIndex()))));

                Parent root = fxmlLoader.load();

                Stage stage = new Stage();

                Controller controller = fxmlLoader.getController();
                controller.setMainStage(stage);

                stage.setScene(new Scene(root));
                stage.show();
            }
            catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }
}
