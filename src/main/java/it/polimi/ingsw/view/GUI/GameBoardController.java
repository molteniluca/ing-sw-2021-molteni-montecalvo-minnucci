package it.polimi.ingsw.view.GUI;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class GameBoardController {

    @FXML
    AnchorPane leaderAnchorPane, personalBoardAnchorPane, buttonAnchorPane, marketAnchorPane;

    AnchorPane newLoadedPaneLeader, newLoadedPanePersonalBoard, newLoadedPaneButton, newLoadedPaneMarket;


    @FXML
    void initialize() throws IOException {
        newLoadedPaneLeader = FXMLLoader.load(ClassLoader.getSystemResource("FXML/UpperBoard.fxml"));
        leaderAnchorPane.getChildren().add(newLoadedPaneLeader);

        newLoadedPanePersonalBoard = FXMLLoader.load(ClassLoader.getSystemResource("FXML/PersonalBoard.fxml"));
        personalBoardAnchorPane.getChildren().add(newLoadedPanePersonalBoard);

        //TODO ask Francesco how to start market section.
//        newLoadedPaneMarket = FXMLLoader.load(ClassLoader.getSystemResource("FXML/Market.fxml"));
//        marketAnchorPane.getChildren().add(newLoadedPaneMarket);

        newLoadedPaneButton = FXMLLoader.load(ClassLoader.getSystemResource("FXML/ButtonBoard.fxml"));
        buttonAnchorPane.getChildren().add(newLoadedPaneButton);

    }
}
