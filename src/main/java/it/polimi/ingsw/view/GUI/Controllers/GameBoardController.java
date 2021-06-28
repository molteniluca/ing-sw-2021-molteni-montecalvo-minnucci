package it.polimi.ingsw.view.GUI.Controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class GameBoardController extends GenericController{
    @FXML
    public Rectangle rectangleBlock;
    @FXML
    AnchorPane leaderAnchorPane, personalBoardAnchorPane, buttonAnchorPane, marketAnchorPane;

    AnchorPane newLoadedPaneLeader, newLoadedPanePersonalBoard, newLoadedPaneButton, newLoadedAnchorPaneMarket;


    @FXML
    void initialize() throws IOException {
        guiView.registerStage(this);
        newLoadedPaneLeader = FXMLLoader.load(ClassLoader.getSystemResource("FXML/LeaderBoard.fxml"));
        leaderAnchorPane.getChildren().add(newLoadedPaneLeader);

        newLoadedPanePersonalBoard = FXMLLoader.load(ClassLoader.getSystemResource("FXML/PersonalBoard.fxml"));
        personalBoardAnchorPane.getChildren().add(newLoadedPanePersonalBoard);


        newLoadedAnchorPaneMarket = FXMLLoader.load(ClassLoader.getSystemResource("FXML/Market.fxml"));
        marketAnchorPane.getChildren().add(newLoadedAnchorPaneMarket);

        newLoadedPaneButton = FXMLLoader.load(ClassLoader.getSystemResource("FXML/ButtonBoard.fxml"));
        buttonAnchorPane.getChildren().add(newLoadedPaneButton);
        rectangleBlock.setVisible(!guiView.isMyTurn);
    }

    public void startTurn() {
        Platform.runLater(() -> {
            rectangleBlock.setVisible(false);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Your turn has started");
            alert.setHeaderText("Your turn has started");
            alert.setContentText("Have fun!");

            alert.showAndWait();
        });
    }

    public void endTurn() {
        Platform.runLater(() -> {
            rectangleBlock.setVisible(true);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Your turn has ended");
            alert.setHeaderText("Your turn has ended");
            alert.setContentText("Wait for the other players to play");

            alert.showAndWait();
        });
    }

    public void handleDisconnect() {
        Platform.runLater(() -> {
            rectangleBlock.setVisible(false);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Closed connection with the server");
            alert.setHeaderText("The game has ended");
            alert.setContentText("Close the game");

            alert.showAndWait();
        });
    }

    public void handleGameEnd(boolean youWon) {
        Platform.runLater(() -> {
            rectangleBlock.setVisible(false);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Game ended");
            if(youWon)
                alert.setHeaderText("Congratulation, you won");
            else
                alert.setHeaderText("You lost, better luck next time!");
            alert.setContentText("Close the game");

            alert.showAndWait();
        });
    }
}
