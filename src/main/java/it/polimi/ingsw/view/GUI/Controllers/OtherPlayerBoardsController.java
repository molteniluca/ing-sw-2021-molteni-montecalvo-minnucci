package it.polimi.ingsw.view.GUI.Controllers;

import it.polimi.ingsw.view.GUI.Controllers.Board.GameBoardController;
import it.polimi.ingsw.view.GUI.Controllers.Board.LeaderBoardController;
import it.polimi.ingsw.view.GUI.Controllers.Board.PersonalBoardController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class OtherPlayerBoardsController extends GenericController{
    private int currentPlayer=0;

    @FXML
    private AnchorPane leaderAnchorPane, personalBoardAnchorPane;

    @FXML
    private AnchorPane newLoadedPaneLeader, newLoadedPanePersonalBoard;

    @FXML
    void initialize() throws IOException {
        newLoadedPaneLeader = FXMLLoader.load(ClassLoader.getSystemResource("FXML/LeaderBoard.fxml"));
        leaderAnchorPane.getChildren().add(newLoadedPaneLeader);

        newLoadedPanePersonalBoard = FXMLLoader.load(ClassLoader.getSystemResource("FXML/PersonalBoard.fxml"));
        personalBoardAnchorPane.getChildren().add(newLoadedPanePersonalBoard);
        LeaderBoardController.getLeaderBoardController().buttonActivate.setVisible(false);
        LeaderBoardController.getLeaderBoardController().buttonDiscard.setVisible(false);
        update();
    }

    /**
     * Method invoked when player wants to see previous player game board
     */
    public void previousPlayer() {
        if(currentPlayer==0)
            currentPlayer=guiView.game.getNumPlayers()-1;
        else
            currentPlayer--;
        update();
    }

    /**
     * Method invoked when player wants to see next player game board
     */
    public void nextPlayer() {
        if(guiView.game.getNumPlayers()==currentPlayer+1)
            currentPlayer=0;
        else
            currentPlayer++;
        update();
    }

    /**
     * Method invoked when player wants to return to game board
     * @param actionEvent click on button
     * @throws IOException if file fxml does not exits
     */
    public void toHomePage(ActionEvent actionEvent) throws IOException {
        GameBoardController.goToGameBoard(actionEvent);
    }

    /**
     * Method that is invoked when client received an updated game and updates the entire otherPlayerBoard view
     */
    @Override
    public void update() {
        Platform.runLater(() -> {
            LeaderBoardController.getLeaderBoardController().update(currentPlayer);
            PersonalBoardController.getPersonalBoardController().update(currentPlayer);
        });
    }
}
