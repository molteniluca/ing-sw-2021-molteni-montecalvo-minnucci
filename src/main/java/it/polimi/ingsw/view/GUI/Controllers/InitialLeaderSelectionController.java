package it.polimi.ingsw.view.GUI.Controllers;

import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.view.GUI.GUIView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;


import java.io.IOException;
import java.util.ArrayList;

public class InitialLeaderSelectionController extends GenericController{
    int numberOfSelectedLeader, i;
    private boolean isLeader1Selected, isLeader2Selected, isLeader3Selected, isLeader4Selected, isSecondResourceChosen;

    @FXML
    Label lChooseResource, lWrongNumberOfLeaders;

    @FXML
    GridPane gChooseResource;

    @FXML
    ImageView leader1, leader2, leader3, leader4;

    ImageView[] leadersImage;


    @FXML
    void initialize(){
        leadersImage = new ImageView[]{leader1, leader2, leader3, leader4};
        ArrayList<LeaderCard> leaderCards = guiView.game.getPlayerTurn(guiView.playerNumber).getPlayer().getPersonalBoard().getLeaderBoard().getLeaderCardsInHand();
        for (LeaderCard leaders: leaderCards) {
            leaderToImageName(leaders, leadersImage[i]);
            i++;
        }

        switch(guiView.playerNumber){
            case 0:
                lChooseResource.setOpacity(0);
                gChooseResource.setOpacity(0);
                gChooseResource.setDisable(true);
                break;

            case 1:
                lChooseResource.setText("You can have one resource");
                break;

            case 2:
                lChooseResource.setText("You can have one resource and one faith point");
                break;

            case 3:
                lChooseResource.setText("You can have two resources and one faith point");
                break;
        }
    }

    private void leaderToImageName(LeaderCard leader, ImageView image){
        String temp = "images/Cards/LeaderCards/" + leader.getImage() + "-1.png";
        image.setImage(new Image(temp));
    }

    public void selectLeaderCard1() {
        isLeader1Selected = selectLeader(leader1, isLeader1Selected);
    }

    public void selectLeaderCard2() {
        isLeader2Selected = selectLeader(leader2, isLeader2Selected);
    }

    public void selectLeaderCard3() {
        isLeader3Selected = selectLeader(leader3, isLeader3Selected);
    }

    public void selectLeaderCard4() {
        isLeader4Selected = selectLeader(leader4, isLeader4Selected);
    }

    private boolean selectLeader(ImageView leader, boolean isLeaderSelected){
        if(!isLeaderSelected) {
            leader.setFitHeight(247.5);
            leader.setFitWidth(180);
            isLeaderSelected = true;
            numberOfSelectedLeader++;
        }
        else{
            leader.setFitHeight(220);
            leader.setFitWidth(160);
            isLeaderSelected = false;
            numberOfSelectedLeader--;
        }
        return isLeaderSelected;
    }

    public void confirmLeaders(ActionEvent actionEvent) throws IOException {
        if(numberOfSelectedLeader==2) {
            //TODO send leaders to server

            //open gameBoard
            CardDealerController.goToGameBoard(actionEvent);
        }
        else {
            lWrongNumberOfLeaders.setOpacity(1);
        }
    }

    public void chooseResource(MouseEvent mouseEvent) {
        int numResSelected = -1, temp;
        ImageView imageView = (ImageView) mouseEvent.getSource();
        if (imageView.getImage().getUrl().equals("/images/resources/gold.png"))
            numResSelected = 1;
        else if (imageView.getImage().getUrl().equals("/images/resources/servant.png"))
            numResSelected = 2;
        else if (imageView.getImage().getUrl().equals("/images/resources/shield.png"))
            numResSelected = 3;
        else if (imageView.getImage().getUrl().equals("/images/resources/stone.png"))
            numResSelected = 4;

        //TODO send with networkHandler.sendObject(numberToResourceType(numResSelected)); to the server in switch

        switch(guiView.playerNumber){
            case 1:

            case 2:
                gChooseResource.setDisable(true);

                /*
                try{
                    networkHandler.sendObject(numberToResourceType(numResSelected));
                }catch (IOException e)
                {
                    e.printStackTrace();
                }

                 */

                break;
                /*
                try{
                    networkHandler.sendObject(numberToResourceType(numResSelected));
                }catch (IOException e)
                {
                    e.printStackTrace();
                }

                 */

            case 3:
                if (!isSecondResourceChosen){
                    temp = numResSelected;
                    isSecondResourceChosen = true;
                    lChooseResource.setText("Choose another resource");
                }
                else {
                /*
                try{
                    networkHandler.sendObject(numberToResourceType(temp));
                    networkHandler.sendObject(numberToResourceType(numResSelected));
                }catch (IOException e)
                {
                    e.printStackTrace();
                }


                 */
                    gChooseResource.setDisable(true);
                }

                break;
        }
    }
}
