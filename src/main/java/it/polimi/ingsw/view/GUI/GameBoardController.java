package it.polimi.ingsw.view.GUI;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class GameBoardController {

    @FXML
    AnchorPane firstAnchorPane;

    @FXML
    AnchorPane secondAnchorPane;

    @FXML
    AnchorPane upperAnchorPane;

    @FXML
    AnchorPane personalBoardAnchorPane;

    AnchorPane newLoadedPaneOne;
    AnchorPane newLoadedPaneTwo;


    @FXML
    void initialize() throws IOException {
        Rectangle2D screenBounds = Screen.getPrimary().getBounds();
        newLoadedPaneOne = FXMLLoader.load(ClassLoader.getSystemResource("FXML/UpperBoard.fxml"));

        firstAnchorPane.getChildren().add(newLoadedPaneOne);

        newLoadedPaneTwo = FXMLLoader.load(ClassLoader.getSystemResource("FXML/PersonalBoard.fxml"));

        secondAnchorPane.getChildren().add(newLoadedPaneTwo);
    }
}
