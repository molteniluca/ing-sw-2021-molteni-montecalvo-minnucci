package it.polimi.ingsw.view.GUI.Controllers;

import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.model.resources.ResourceTypes;
import it.polimi.ingsw.view.GUI.Controllers.Board.GameBoardController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
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
    private boolean isResourcesSelected;
    Boolean isLeaderSelected[];
    private ResourceTypes temp, resourceTypes = null;

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
        isLeaderSelected = new Boolean[]{isLeader1Selected, isLeader2Selected, isLeader3Selected, isLeader4Selected};
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

    public void chooseResource(MouseEvent mouseEvent) {

        ImageView imageView = (ImageView) mouseEvent.getSource();
        imageView.setFitWidth(50);
        imageView.setFitHeight(50);
        if (imageView == iGold)
            resourceTypes = ResourceTypes.GOLD;
        else if (imageView == iServant)
            resourceTypes = ResourceTypes.SERVANT;
        else if (imageView == iShield)
            resourceTypes = ResourceTypes.SHIELD;
        else if (imageView == iStone)
            resourceTypes = ResourceTypes.STONE;

        switch(guiView.playerNumber){
            case 0:

            case 1:

            case 2:
                gChooseResource.setDisable(true);
                isResourcesSelected = true;
                break;

            case 3:
                if (!isSecondResourceChosen){
                    imageView.setFitHeight(40);
                    imageView.setFitWidth(40);
                    temp = resourceTypes;
                    isSecondResourceChosen = true;
                    lChooseResource.setText("Choose another resource");
                }
                else {
                    isResourcesSelected = true;
                    gChooseResource.setDisable(true);
                }
                guiView.isSuccessReceived();
                break;
        }
    }

    public void selectLeaderCard(MouseEvent mouseEvent) {
        String id = mouseEvent.getPickResult().getIntersectedNode().getId();
        switch (id){
            case "leader1":
                isLeader1Selected = selectLeader(leader1, isLeader1Selected);
                break;
            case "leader2":
                isLeader2Selected = selectLeader(leader2, isLeader2Selected);
                break;
            case "leader3":
                isLeader3Selected = selectLeader(leader3, isLeader3Selected);
                break;
            case "leader4":
                isLeader4Selected = selectLeader(leader4, isLeader4Selected);
                break;
        }
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

    public void confirmResourcesLeaders(ActionEvent actionEvent) {
        if(numberOfSelectedLeader==2 && (isResourcesSelected || guiView.playerNumber==0)) {

            Integer[] numberOfLeaderToSend = new Integer[2];
            boolean[] isLeaderSelected = {isLeader1Selected, isLeader2Selected, isLeader3Selected, isLeader4Selected};
            int k = 0;
            for (int j = 0; j < 4; j++) {
                if (isLeaderSelected[j]){
                    numberOfLeaderToSend[k] = j;
                    k++;
                }
            }

            try {
                if (isSecondResourceChosen)
                    guiView.setInitialResources(temp, resourceTypes);
                else if (resourceTypes != null)
                    guiView.setInitialResources(resourceTypes);

                guiView.chooseLeaderAndWaitForStart(numberOfLeaderToSend);
                GameBoardController.goToGameBoard(actionEvent);
            } catch (IOException e) {
                handleDisconnect();
            }

        }
        else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("WARNING");
            alert.setHeaderText("Illegal action");
            alert.setContentText("Choose leaders and eventually resources correctly.");

            alert.showAndWait();
        }
    }

    private void leaderToImageName(LeaderCard leader, ImageView image){
        String temp = "images/Cards/LeaderCards/" + leader.getImage() + "-1.png";
        image.setImage(new Image(temp));
    }

    public void handleDisconnect() {
        Platform.runLater(() -> {
            Alert alertDisconnected = new Alert(Alert.AlertType.ERROR);
            alertDisconnected.setTitle("Closed connection with the server");
            alertDisconnected.setHeaderText("There is a problem communicating with the server or one of the players");
            alertDisconnected.setContentText("Close the game");

            alertDisconnected.showAndWait();
            System.exit(0);
        });
    }
}
