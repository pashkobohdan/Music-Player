package sample;

import controllers.Controller;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import objects.LocaleInfo;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class Main extends Application {
    private FXMLLoader fxmlLoader;
    private String mainLanguage;
    private Parent parentRoot;
    private Controller controller;
    private Scene scene;

    @Override
    public void start(Stage primaryStage) throws Exception {

        fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("../fxml/main.fxml"));

        mainLanguage = LocaleInfo.readMainLanguage();
        if (mainLanguage != null) {
            fxmlLoader.setResources(ResourceBundle.getBundle("bundles.Locale", new Locale(mainLanguage)));
        } else {
            fxmlLoader.setResources(ResourceBundle.getBundle("bundles.Locale", Locale.getDefault()));
        }

        parentRoot = fxmlLoader.load();

        controller = fxmlLoader.getController();
        controller.setMainStage(primaryStage);

        scene = new Scene(parentRoot, 500, 600);
        primaryStage.setScene(scene);

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
