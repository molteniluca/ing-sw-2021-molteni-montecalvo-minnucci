package it.polimi.ingsw.view.GUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class GUIApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {
        URL url = ClassLoader.getSystemResource("MainBoard.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(url);
        Parent root = fxmlLoader.load();

        Scene scene = new Scene(root, 800, 600);

        primaryStage.setTitle("Masters Of Renaissance");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
