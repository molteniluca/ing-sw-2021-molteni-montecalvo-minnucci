package it.polimi.ingsw.view.GUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class GUIApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {
        Rectangle2D screenBounds = Screen.getPrimary().getBounds();
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        URL url = ClassLoader.getSystemResource("FXML/homePage.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(url);
        Parent root = fxmlLoader.load();

        Scene scene = new Scene(root);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Masters Of Renaissance");
        primaryStage.setResizable(false);

        Image icon = new Image("images/icon.png");
        primaryStage.getIcons().add(icon);

        primaryStage.show();
    }
}
