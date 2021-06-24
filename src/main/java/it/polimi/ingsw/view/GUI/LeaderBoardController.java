package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.model.cards.LeaderCard;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.ArrayList;

import static it.polimi.ingsw.view.GUI.GUI.game;
import static it.polimi.ingsw.view.GUI.GUI.playerNumber;

public class LeaderBoardController {
    @FXML
    ImageView ilh1, ilh2, ila1, ila2;

    @FXML
    void initialize(){
        /*
        ArrayList<LeaderCard> leaders = game.getPlayerTurn(playerNumber).getPlayer().getPersonalBoard().getLeaderBoard().getLeaderCards();
        leaderToImageName(leaders.get(0), ilh1);
        leaderToImageName(leaders.get(1), ilh2);

         */

    }
    private void leaderToImageName(LeaderCard leader, ImageView image){
        String temp = "images/Cards/LeaderCards/" + leader.getImage() + ".png";
        image.setImage(new Image(temp));
    }
    private void activateLeader(LeaderCard leader, ImageView imageToRemove){
        discardLeader(imageToRemove);
        if (ila1.getImage()==null){
            leaderToImageName(leader, ila1);
        }
        else
            leaderToImageName(leader, ila2);
    }

    private void discardLeader(ImageView leaderToDiscard){
        leaderToDiscard.setDisable(true);
        leaderToDiscard.setOpacity(0);
    }
}
