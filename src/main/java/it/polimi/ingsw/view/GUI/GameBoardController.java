package it.polimi.ingsw.view.GUI;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class GameBoardController {

    @FXML
    AnchorPane firstAnchorPane;

    @FXML
    AnchorPane secondAnchorPane;

    AnchorPane newLoadedPaneOne;
    Pane newLoadedPaneTwo;

    @FXML
    void initialize(){
        try {
            newLoadedPaneOne = FXMLLoader.load(ClassLoader.getSystemResource("FXML/UpperBoard.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        firstAnchorPane.getChildren().add(newLoadedPaneOne);

        try {
            newLoadedPaneTwo = FXMLLoader.load(ClassLoader.getSystemResource("FXML/PersonalBoard.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        secondAnchorPane.getChildren().add(newLoadedPaneTwo);
    }
}
