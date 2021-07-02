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
    private static Stage stage;
    @FXML
    private Rectangle rectangleBlock;
    @FXML
    private Label labelWaitForPlayers;
    @FXML
    private ProgressIndicator spinningThing;
    @FXML
    private AnchorPane leaderAnchorPane, personalBoardAnchorPane, buttonAnchorPane, marketAnchorPane;

    private final ArrayList<GenericController> controllerArrayList = new ArrayList<>();

    private Alert alertDisconnected, alertWin, alertError;


    @FXML
    public void initialize() throws IOException {
        guiView.registerStage(this);

        AnchorPane newLoadedPaneLeader = FXMLLoader.load(ClassLoader.getSystemResource("FXML/LeaderBoard.fxml"));
        leaderAnchorPane.getChildren().add(newLoadedPaneLeader);

        AnchorPane newLoadedPanePersonalBoard = FXMLLoader.load(ClassLoader.getSystemResource("FXML/PersonalBoard.fxml"));
        personalBoardAnchorPane.getChildren().add(newLoadedPanePersonalBoard);

        AnchorPane newLoadedAnchorPaneMarket = FXMLLoader.load(ClassLoader.getSystemResource("FXML/Market.fxml"));
        marketAnchorPane.getChildren().add(newLoadedAnchorPaneMarket);

        AnchorPane newLoadedPaneButton = FXMLLoader.load(ClassLoader.getSystemResource("FXML/ButtonBoard.fxml"));
        buttonAnchorPane.getChildren().add(newLoadedPaneButton);
        rectangleBlock.setVisible(!guiView.isMyTurn);
        spinningThing.setVisible(!guiView.isMyTurn);
        labelWaitForPlayers.setVisible(!guiView.isMyTurn);

        controllerArrayList.add(PersonalBoardController.getPersonalBoardController());
        controllerArrayList.add(MarketController.getMarketController());
        controllerArrayList.add(LeaderBoardController.getLeaderBoardController());
        controllerArrayList.add(ButtonBoardController.getButtonBoardController());
        notifyUpdate();
    }

    /**
     * Method that enable player to start its turn giving him an alert
     */
    public void startTurn() {
        Platform.runLater(() -> {
            rectangleBlock.setVisible(false);
            spinningThing.setVisible(false);
            labelWaitForPlayers.setVisible(false);
            if(guiView.game.getNumPlayers()!=1) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Your turn has started");
                alert.setHeaderText("Your turn has started");
                alert.setContentText("Have fun!");

                alert.showAndWait();
            }
        });
    }

    /**
     * Method that ends player turn
     */
    public void endTurn() {
        Platform.runLater(() -> {
            rectangleBlock.setVisible(true);
            spinningThing.setVisible(true);
            labelWaitForPlayers.setVisible(true);
        });
    }

    /**
     * Method that disable actions and warn player
     * about a problem of connection with the server
     */
    public void handleDisconnect() {
        Platform.runLater(() -> {
            rectangleBlock.setVisible(true);
            spinningThing.setVisible(false);
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

    /**
     * Method that shows alert which notifies players about
     * the end of the game and tells if player has won or lost
     * @param youWon boolean parameter true if player has won
     */
    public void handleGameEnd(boolean youWon) {
        Platform.runLater(() -> {
            rectangleBlock.setVisible(true);
            spinningThing.setVisible(false);
            labelWaitForPlayers.setVisible(false);
            if(alertError!=null){
                alertError.close();
            }
            if(alertDisconnected!=null){
                alertDisconnected.close();
            }
            alertWin = new Alert(Alert.AlertType.INFORMATION);
            alertWin.setTitle("Game ended");
            if(youWon)
                alertWin.setHeaderText("Congratulation, you won with "+guiView.game.getPlayerTurn(guiView.playerNumber).getVictoryPoints()+" victory points");
            else
                alertWin.setHeaderText("You lost, better luck next time! You had: " + guiView.game.getPlayerTurn(guiView.playerNumber).getVictoryPoints()+" victory points");
            alertWin.setContentText("Exit the game");

            alertWin.showAndWait();
            System.exit(0);
        });
    }

    /**
     * Method that shows an alert with message of error
     * @param lastErrorMessage the error message
     */
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

    /**
     * Method that opens GameBoard
     * @param actionEvent click of buttons that open GameBoard
     * @throws IOException if fxml GameBoard file cannot be opened
     */
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
