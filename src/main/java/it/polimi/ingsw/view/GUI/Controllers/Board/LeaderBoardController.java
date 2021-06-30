package it.polimi.ingsw.view.GUI.Controllers.Board;

import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.view.GUI.Controllers.GenericController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;

import java.io.IOException;
import java.util.ArrayList;

public class LeaderBoardController extends GenericController {
    private static LeaderBoardController leaderBoardController;
    private int leaderSelected=-1;

    @FXML
    public Button buttonDiscard, buttonActivate;

    @FXML
    private ImageView ilh1, ilh2, ila1, ila2; //i = image, l = leader, h = hand, a = activated

    @FXML
    public Rectangle rLeader;

    public static LeaderBoardController getLeaderBoardController() {
        return leaderBoardController;
    }

    @FXML
    void initialize(){
        leaderBoardController=this;
    }

    private void leaderToImageName(LeaderCard leader, ImageView image){
        if(leader!=null)
            image.setImage(new Image("images/Cards/LeaderCards/" + leader.getImage() + "-1.png"));
        else
            image.setImage(null);
    }

    @Override
    public void update() {
        update(guiView.playerNumber);
    }

    public void discardLeader(ActionEvent actionEvent) throws IOException {
        guiView.discardLeader(leaderSelected);
        guiView.isSuccessReceived();
    }

    public void activateLeader(ActionEvent actionEvent) throws IOException {
        guiView.activateLeader(leaderSelected);
        guiView.isSuccessReceived();
    }

    public void leaderClick(MouseEvent mouseEvent) {
        if(mouseEvent.getPickResult().getIntersectedNode().getId().equals("ilh1")){
            leaderSelected=0;
        }else {
            leaderSelected=1;
        }
    }

    public void setClickable(boolean set){
        rLeader.setVisible(!set);
    }

    public void update(int player) {
        ImageView[] leaderInHandView=new ImageView[]{ilh1,ilh2};
        ImageView[] leaderActiveView=new ImageView[]{ila1,ila2};

        if(player==guiView.playerNumber){
            ArrayList<LeaderCard> leaderCardsInHand = guiView.game.getPlayerTurn(player).getPlayer().getPersonalBoard().getLeaderBoard().getLeaderCardsInHand();
            for(int i=0;i<2;i++){
                if(i<leaderCardsInHand.size())
                    leaderToImageName(leaderCardsInHand.get(i), leaderInHandView[i]);
                else
                    leaderToImageName(null,leaderInHandView[i]);
            }
        }else{
            for(int i=0;i<2;i++){
                leaderToImageName(null,leaderInHandView[i]);
            }
        }

        ArrayList<LeaderCard> leaderActive = guiView.game.getPlayerTurn(player).getPlayer().getPersonalBoard().getLeaderBoard().getLeaderCards();
        for(int i=0;i<2;i++){
            if(i<leaderActive.size())
                leaderToImageName(leaderActive.get(i), leaderActiveView[i]);
            else
                leaderToImageName(null,leaderActiveView[i]);
        }
    }
}
