package it.polimi.ingsw.view.GUI.Controllers.Board;

import it.polimi.ingsw.view.GUI.Controllers.GenericController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class GameBoardController extends GenericController {
    @FXML
    public Rectangle rectangleBlock;
    @FXML
    public Label labelWaitForPlayers;
    @FXML
    public ProgressIndicator spinnyThing;
    @FXML
    AnchorPane leaderAnchorPane, personalBoardAnchorPane, buttonAnchorPane, marketAnchorPane;

    AnchorPane newLoadedPaneLeader, newLoadedPanePersonalBoard, newLoadedPaneButton, newLoadedAnchorPaneMarket;

    ArrayList<GenericController> controllerArrayList = new ArrayList<>();

    @FXML
    void initialize() throws IOException {
        guiView.registerStage(this);

        newLoadedPaneLeader =  FXMLLoader.load(ClassLoader.getSystemResource("FXML/LeaderBoard.fxml"));
        leaderAnchorPane.getChildren().add(newLoadedPaneLeader);

        newLoadedPanePersonalBoard = FXMLLoader.load(ClassLoader.getSystemResource("FXML/PersonalBoard.fxml"));
        personalBoardAnchorPane.getChildren().add(newLoadedPanePersonalBoard);

        newLoadedAnchorPaneMarket = FXMLLoader.load(ClassLoader.getSystemResource("FXML/Market.fxml"));
        marketAnchorPane.getChildren().add(newLoadedAnchorPaneMarket);

        newLoadedPaneButton = FXMLLoader.load(ClassLoader.getSystemResource("FXML/ButtonBoard.fxml"));
        buttonAnchorPane.getChildren().add(newLoadedPaneButton);
        rectangleBlock.setVisible(!guiView.isMyTurn);
        spinnyThing.setVisible(!guiView.isMyTurn);
        labelWaitForPlayers.setVisible(!guiView.isMyTurn);

        controllerArrayList.add(PersonalBoardController.getPersonalBoardController());
        controllerArrayList.add(MarketController.getMarketController());
        controllerArrayList.add(LeaderBoardController.getLeaderBoardController());
    }

    public void startTurn() {
        Platform.runLater(() -> {
            rectangleBlock.setVisible(false);
            spinnyThing.setVisible(false);
            labelWaitForPlayers.setVisible(false);
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
            spinnyThing.setVisible(true);
            labelWaitForPlayers.setVisible(true);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Your turn has ended");
            alert.setHeaderText("Your turn has ended");
            alert.setContentText("Wait for the other players to play");

            alert.showAndWait();
        });
    }

    public void handleDisconnect() {
        Platform.runLater(() -> {
            rectangleBlock.setVisible(true);
            spinnyThing.setVisible(false);
            labelWaitForPlayers.setVisible(false);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Closed connection with the server");
            alert.setHeaderText("The game has ended");
            alert.setContentText("Close the game");

            alert.showAndWait();
        });
    }

    public void handleGameEnd(boolean youWon) {
        Platform.runLater(() -> {
            rectangleBlock.setVisible(true);
            spinnyThing.setVisible(false);
            labelWaitForPlayers.setVisible(false);
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

    public void showError(String lastErrorMessage) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(lastErrorMessage);
            alert.setContentText("Retry");

            alert.showAndWait();
        });
    }

    public void notifyUpdate() {
        for (GenericController g : controllerArrayList){
            g.update();
        }
    }

    public static void goToGameBoard(ActionEvent actionEvent) throws IOException {
        Parent GameBoardViewParent = FXMLLoader.load(ClassLoader.getSystemResource("FXML/GameBoard.fxml"));

        Scene GameBoardScene = new Scene(GameBoardViewParent);

        Stage GameBoardStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

        GameBoardStage.setTitle("Game Board");
        GameBoardStage.setScene(GameBoardScene);
        GameBoardStage.show();
    }
}
