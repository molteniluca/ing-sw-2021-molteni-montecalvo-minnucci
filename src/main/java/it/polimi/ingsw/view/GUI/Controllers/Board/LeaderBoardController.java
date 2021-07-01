package it.polimi.ingsw.view.GUI.Controllers.Board;

import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.view.GUI.Controllers.GenericController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
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
        Platform.runLater(()->{
            update(guiView.playerNumber);
        });
    }

    public void discardLeader(ActionEvent actionEvent) {
        if(leaderSelected == -1)
            Platform.runLater(() -> {
                Alert alertDisconnected = new Alert(Alert.AlertType.ERROR);
                alertDisconnected.setTitle("Error!");
                alertDisconnected.setHeaderText("You need to select a leader card");
                alertDisconnected.setContentText("Retry");
                alertDisconnected.showAndWait();
            });
        else
            try {
                guiView.discardLeader(leaderSelected);
                guiView.isSuccessReceived();
            } catch (IOException e) {
                guiView.notifyDisconnection();
            }
    }

    private void selectImage(ImageView im, boolean isActive) {
        if(isActive) {
            im.setFitWidth(140.0);
            im.setFitHeight(210);
        }
        else{
            im.setFitWidth(130.0);
            im.setFitHeight(195.0);
        }
    }

    public void activateLeader(ActionEvent actionEvent) {
        if(leaderSelected == -1)
            Platform.runLater(() -> {
                Alert alertDisconnected = new Alert(Alert.AlertType.ERROR);
                alertDisconnected.setTitle("Error!");
                alertDisconnected.setHeaderText("You need to select a leader card");
                alertDisconnected.setContentText("Retry");
                alertDisconnected.showAndWait();
            });
        else
            try {
                guiView.activateLeader(leaderSelected);
                guiView.isSuccessReceived();
            } catch (IOException e) {
                guiView.notifyDisconnection();
            }
    }

    public void leaderClick(MouseEvent mouseEvent) {
        if(mouseEvent.getPickResult().getIntersectedNode().getId().equals("ilh1")){
            if(leaderSelected==0)
                leaderSelected=-1;
            else
                leaderSelected=0;
        }else {
            if(leaderSelected==1)
                leaderSelected=-1;
            else
                leaderSelected=1;
        }

        switch (leaderSelected){
            case -1:
                selectImage(ilh1,false);
                selectImage(ilh2,false);
                break;
            case 0:
                selectImage(ilh2,false);
                selectImage(ilh1,true);
                break;
            case 1:
                selectImage(ilh2,true);
                selectImage(ilh1,false);
        }
    }

    private void setClickable(int player){
        if(player == guiView.playerNumber)
            rLeader.setVisible(!guiView.game.getPlayerTurn(guiView.playerNumber).isLeaderAction() && !
                    guiView.game.getPlayerTurn(guiView.playerNumber).isWaitingForAction() ||
                    guiView.game.getPlayerTurn(guiView.playerNumber).isHandlingSwap() ||
                    guiView.game.getPlayerTurn(guiView.playerNumber).isProducing());
        else
            rLeader.setVisible(false);
    }

    public void update(int player) {
        setClickable(player);
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
