package it.polimi.ingsw.view.GUI.Controllers;

import it.polimi.ingsw.view.GUI.Controllers.Board.GameBoardController;
import it.polimi.ingsw.view.GUI.Controllers.Board.LeaderBoardController;
import it.polimi.ingsw.view.GUI.Controllers.Board.MarketController;
import it.polimi.ingsw.view.GUI.Controllers.Board.PersonalBoardController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;

import java.io.IOException;

public class OtherPlayerBoardsController extends GenericController{
    private int currentPlayer=0;

    @FXML
    AnchorPane leaderAnchorPane, personalBoardAnchorPane;

    @FXML
    AnchorPane newLoadedPaneLeader, newLoadedPanePersonalBoard;

    public void previousPlayer(ActionEvent actionEvent) {
        if(guiView.game.getNumPlayers()==currentPlayer+1)
            currentPlayer=0;
            LeaderBoardController.getLeaderBoardController().update(currentPlayer);
            PersonalBoardController.getPersonalBoardController().update(currentPlayer);
    }

    public void nextPlayer(ActionEvent actionEvent) {
        if(currentPlayer==0)
            currentPlayer=guiView.game.getNumPlayers()-1;
        LeaderBoardController.getLeaderBoardController().update(currentPlayer);
        PersonalBoardController.getPersonalBoardController().update(currentPlayer);
    }

    public void toHomePage(ActionEvent actionEvent) throws IOException {
        GameBoardController.goToGameBoard(actionEvent);
    }


    @FXML
    void initialize() throws IOException {
        newLoadedPaneLeader = FXMLLoader.load(ClassLoader.getSystemResource("FXML/LeaderBoard.fxml"));
        leaderAnchorPane.getChildren().add(newLoadedPaneLeader);

        newLoadedPanePersonalBoard = FXMLLoader.load(ClassLoader.getSystemResource("FXML/PersonalBoard.fxml"));
        personalBoardAnchorPane.getChildren().add(newLoadedPanePersonalBoard);
    }
}
