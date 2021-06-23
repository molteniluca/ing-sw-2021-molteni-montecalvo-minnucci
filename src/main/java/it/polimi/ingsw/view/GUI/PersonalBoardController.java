package it.polimi.ingsw.view.GUI;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Screen;

import java.io.IOException;

import static it.polimi.ingsw.view.GUI.GUI.game;
import static it.polimi.ingsw.view.GUI.GUI.playerNumber;

public class PersonalBoardController {

    private int currentFaithPosition;

    @FXML
    AnchorPane personalBoardAnchorPane;

    @FXML
    ImageView if0, if1, if2, if3, if4, if5, if6, if7, if8, if9, if10, if11, if12, if13, if14, if15, if16, if17, if18, if19, if20, if21, if22, if23, if24;
    ImageView[] faithImagePosition = {if0, if1, if2, if3, if4, if5, if6, if7, if8, if9, if10, if11, if12, if13, if14, if15, if16, if17, if18, if19, if20, if21, if22, if23, if24};

    @FXML
    void updateFaithTrack(){
        faithImagePosition[currentFaithPosition].setVisible(false);
        currentFaithPosition = game.getPlayerTurn(playerNumber).getPlayer().getPersonalBoard().getFaithTrack().getPosition();
        faithImagePosition[currentFaithPosition].setVisible(true);
    }

    void updatePersonalBoard(){
        updateFaithTrack();
    }

    @FXML
    void initialize(){
        //updatePersonalBoard();
    }

}
