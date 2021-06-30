package it.polimi.ingsw.view.GUI.Controllers.Board;

import it.polimi.ingsw.view.GUI.Controllers.GenericController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class GameBoardController extends GenericController {
    public static Stage stage;
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

    Alert alertDisconnected, alertTurnEnded, alertWin, alertError;

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
        controllerArrayList.add(ButtonBoardController.getButtonBoardController());
        notifyUpdate();
    }

    public void startTurn() {
        Platform.runLater(() -> {
            rectangleBlock.setVisible(false);
            spinnyThing.setVisible(false);
            labelWaitForPlayers.setVisible(false);
            if(guiView.game.getNumPlayers()==1) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Your turn has started");
                alert.setHeaderText("Your turn has started");
                alert.setContentText("Have fun!");

                alert.showAndWait();
            }
        });
    }

    public void endTurn() {
        Platform.runLater(() -> {
            rectangleBlock.setVisible(true);
            spinnyThing.setVisible(true);
            labelWaitForPlayers.setVisible(true);
            /*if(alertWin==null) {
                alertTurnEnded = new Alert(Alert.AlertType.INFORMATION);
                alertTurnEnded.setTitle("Your turn has ended");
                alertTurnEnded.setHeaderText("Your turn has ended");
                alertTurnEnded.setContentText("Wait for the other players to play");

                alertTurnEnded.showAndWait();
            }*/
        });
    }

    public void handleDisconnect() {
        Platform.runLater(() -> {
            rectangleBlock.setVisible(true);
            spinnyThing.setVisible(false);
            labelWaitForPlayers.setVisible(false);
            if(alertWin==null){
                alertDisconnected = new Alert(Alert.AlertType.ERROR);
                alertDisconnected.setTitle("Closed connection with the server");
                alertDisconnected.setHeaderText("There is a problem communicating with the server or one of the players");
                alertDisconnected.setContentText("Close the game");

                alertDisconnected.showAndWait();
                System.exit(0);
            }
        });
    }

    public void handleGameEnd(boolean youWon) {
        Platform.runLater(() -> {
            rectangleBlock.setVisible(true);
            spinnyThing.setVisible(false);
            labelWaitForPlayers.setVisible(false);
            if(alertError!=null){
                alertError.close();
            }
            if(alertTurnEnded!=null){
                alertTurnEnded.close();
            }
            if(alertDisconnected!=null){
                alertDisconnected.close();
            }
            alertWin = new Alert(Alert.AlertType.CONFIRMATION);
            alertWin.setTitle("Game ended");
            if(youWon)
                alertWin.setHeaderText("Congratulation, you won");
            else
                alertWin.setHeaderText("You lost, better luck next time!");
            alertWin.setContentText("Do you wanna play again?");

            ButtonType button = alertWin.showAndWait().orElse(ButtonType.CANCEL);
            if(button == ButtonType.OK){
                Parent GameBoardViewParent = null;
                try {
                    GameBoardViewParent = FXMLLoader.load(ClassLoader.getSystemResource("FXML/AskCreateOrJoin.fxml"));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Scene GameBoardScene = new Scene(GameBoardViewParent);

                Stage GameBoardStage = stage;

                GameBoardStage.setTitle("Master Of Renaissance");
                GameBoardStage.setScene(GameBoardScene);
                GameBoardStage.show();
            }else{
                System.exit(0);
            }
        });
    }

    public void showError(String lastErrorMessage) {
        Platform.runLater(() -> {
            alertError = new Alert(Alert.AlertType.ERROR);
            alertError.setTitle("Error");
            alertError.setHeaderText(lastErrorMessage);
            alertError.setContentText("Retry");

            alertError.showAndWait();
        });
    }

    /**
     * Calls the update method on every controller
     */
    public void notifyUpdate() {
        for (GenericController g : controllerArrayList){
            g.update();
        }
    }

    public static void goToGameBoard(ActionEvent actionEvent) throws IOException {
        Parent GameBoardViewParent = FXMLLoader.load(ClassLoader.getSystemResource("FXML/GameBoard.fxml"));

        Scene GameBoardScene = new Scene(GameBoardViewParent);

        Stage GameBoardStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage=GameBoardStage;

        GameBoardStage.setTitle("Game Board");
        GameBoardStage.setScene(GameBoardScene);
        GameBoardStage.show();
    }
}
