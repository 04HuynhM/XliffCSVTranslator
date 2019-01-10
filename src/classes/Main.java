package classes;

import com.jfoenix.controls.JFXTabPane;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.stage.Stage;

public class Main extends Application {

    @FXML
    JFXTabPane tabPane;
    @FXML
    Tab xliffToCsvTab;
    @FXML
    Tab csvToXliffTab;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("MainScene.fxml"));
        primaryStage.setTitle("Xliff Conversion Tool");
        primaryStage.setScene(new Scene(root, 427, 820));
        primaryStage.setResizable(false);
        primaryStage.sizeToScene();
        primaryStage.show();
    }
}
