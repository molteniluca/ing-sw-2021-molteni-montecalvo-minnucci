package it.polimi.ingsw.view.GUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class GUIApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {
        URL url = ClassLoader.getSystemResource("FXML/AskCreateOrJoin.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(url);
        Parent root = fxmlLoader.load();

        Scene scene = new Scene(root);

        Rectangle2D screenBounds = Screen.getPrimary().getBounds();
        System.out.println(Screen.getScreens().size());
        System.out.println(screenBounds);
        //System.exit(0);

        primaryStage.setTitle("Masters Of Renaissance");


        primaryStage.setResizable(true);
        //primaryStage.setFullScreen(true);

        primaryStage.setMaxWidth(screenBounds.getMaxX());
        primaryStage.setMaxHeight(screenBounds.getMaxY());


        //primaryStage.setX(screenBounds.getWidth());

        //primaryStage.setMaximized(true);
        //primaryStage.setFullScreen(true);
        //primaryStage.sizeToScene();
        //cambio del tasto e del testo  per uscire dallo schermo intero
        //primaryStage.setFullScreenExitHint("Press q to exit");
        //primaryStage.setFullScreenExitKeyCombination(KeyCombination.valueOf("q"));

        primaryStage.setScene(scene);

        primaryStage.show();


    }
}
