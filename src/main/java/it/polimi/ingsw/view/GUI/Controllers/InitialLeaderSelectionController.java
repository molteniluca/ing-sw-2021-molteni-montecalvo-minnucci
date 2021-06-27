package it.polimi.ingsw.view.GUI.Controllers;

import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.model.resources.ResourceTypes;
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
    private ResourceTypes temp;

    @FXML
    Label lChooseResource, lWrongNumberOfLeaders;

    @FXML
    GridPane gChooseResource;

    @FXML
    ImageView leader1, leader2, leader3, leader4;

    @FXML
    ImageView iGold, iServant, iShield, iStone; //i = image

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
            Integer[] numberOfLeaderToSend = new Integer[2];
            boolean[] isLeaderSelected = {isLeader1Selected, isLeader2Selected, isLeader3Selected, isLeader4Selected};
            int k = 0;
            for (int j = 0; j < 4; j++) {
                if (isLeaderSelected[j]){
                    numberOfLeaderToSend[k] = j;
                    k++;
                }
            }
            guiView.chooseLeader(numberOfLeaderToSend);
            guiView.waitForUpdatedGame();

            //open gameBoard
            CardDealerController.goToGameBoard(actionEvent);
        }
        else {
            lWrongNumberOfLeaders.setOpacity(1);
        }
    }

    public void chooseResource(MouseEvent mouseEvent) {
        ResourceTypes resourceTypes = null;
        ImageView imageView = (ImageView) mouseEvent.getSource();
        if (imageView == iGold)
            resourceTypes = ResourceTypes.GOLD;
        else if (imageView == iServant)
            resourceTypes = ResourceTypes.SERVANT;
        else if (imageView == iShield)
            resourceTypes = ResourceTypes.SHIELD;
        else if (imageView == iStone)
            resourceTypes = ResourceTypes.STONE;

        switch(guiView.playerNumber){
            case 1:

            case 2:
                gChooseResource.setDisable(true);

                try{
                    guiView.setInitialResources(resourceTypes);
                }catch (IOException e)
                {
                    e.printStackTrace();
                }

                break;

            case 3:
                if (!isSecondResourceChosen){
                    temp = resourceTypes;
                    isSecondResourceChosen = true;
                    lChooseResource.setText("Choose another resource");
                }
                else {

                try{
                    guiView.setInitialResources(temp, resourceTypes);
                }catch (IOException e)
                {
                    e.printStackTrace();
                }

                    gChooseResource.setDisable(true);
                }

                break;
        }
    }
}
