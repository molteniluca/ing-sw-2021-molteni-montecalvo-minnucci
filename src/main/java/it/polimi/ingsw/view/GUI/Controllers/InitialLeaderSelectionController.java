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
    private boolean isLeader1Selected, isLeader2Selected, isLeader3Selected, isLeader4Selected;
    private boolean isResourcesSelected;
    private ResourceTypes temp, resourceTypes = null;

    @FXML
    private Label lChooseResource;

    @FXML
    private GridPane gChooseResource, gChooseResource2;

    @FXML
    private ImageView leader1, leader2, leader3, leader4;

    @FXML
    private ImageView iGold, iServant, iShield, iStone; //i = image

    @FXML
    private ImageView iGold2, iServant2, iShield2, iStone2;


    @FXML
    void initialize(){
        ImageView[] leadersImage = new ImageView[]{leader1, leader2, leader3, leader4};
        ArrayList<LeaderCard> leaderCards = guiView.game.getPlayerTurn(guiView.playerNumber).getPlayer().getPersonalBoard().getLeaderBoard().getLeaderCardsInHand();
        for (LeaderCard leaders: leaderCards) {
            leaderToImageName(leaders, leadersImage[i]);
            i++;
        }

        //different players has different initial requests
        switch(guiView.playerNumber){
            case 0:
                lChooseResource.setOpacity(0);
                gChooseResource.setOpacity(0);
                gChooseResource.setDisable(true);
                break;

            case 1:

            case 2:
                lChooseResource.setText("You can have one resource");
                break;

            case 3:
                lChooseResource.setText("You can have two resources");
                gChooseResource2.setOpacity(1);
                break;
        }
    }

    /**
     * Method invoked when player select his first resource,
     * different scenarios after selection, depending on player number.
     * @param mouseEvent click on first resource
     */
    public void chooseFirstResource(MouseEvent mouseEvent) {

        imageResBiggerAndToResourceType(mouseEvent);

        switch(guiView.playerNumber){
            case 0:

            case 1:

            case 2:
                gChooseResource.setDisable(true);
                isResourcesSelected = true;
                break;

            //only the fourth player has to choose the second resource
            case 3:
                gChooseResource.setDisable(true);
                gChooseResource2.setDisable(false);

                temp = resourceTypes;
                lChooseResource.setText("Choose another resource");
                break;
        }
    }

    /**
     * Method invoked when fourth player select his second resource
     * @param mouseEvent click on the second resource
     */
    public void chooseSecondResource(MouseEvent mouseEvent) {
        imageResBiggerAndToResourceType(mouseEvent);

        isResourcesSelected = true;
        gChooseResource2.setDisable(true);
    }

    /**
     * Method that keeps track of the selected leader
     * cards and counts them thanks to sub-method
     * @param mouseEvent click on leader card
     */
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

    /**
     * Boolean method that makes leader images bigger
     * and counts how many leaders are selected
     * @param leader image of leader selected
     * @param isLeaderSelected boolean attribute which tells
     *                         you if leader is clicked or not
     * @return boolean value which tells you if leader is selected or not
     */
    private boolean selectLeader(ImageView leader, boolean isLeaderSelected){
        if(!isLeaderSelected) {
            leader.setFitHeight(247.5);
            leader.setFitWidth(180);
            isLeaderSelected = true;
            numberOfSelectedLeader++; //total number of leader selected
        }
        else{
            leader.setFitHeight(220);
            leader.setFitWidth(160);
            isLeaderSelected = false;
            numberOfSelectedLeader--; //total number of leader selected
        }
        return isLeaderSelected;
    }

    /**
     * Method that checks if player has selected all of his items,
     * sends the information to the server and open GameBoard
     * @param actionEvent click on confirm button
     */
    public void confirmResourcesLeaders(ActionEvent actionEvent) {
        if(numberOfSelectedLeader==2 && (isResourcesSelected || guiView.playerNumber==0)) {

            Integer[] numberOfLeaderToSend = new Integer[2];
            boolean[] isLeaderSelected = {isLeader1Selected, isLeader2Selected, isLeader3Selected, isLeader4Selected};
            int k = 0;
            //set array of int related to leaders to send
            for (int j = 0; j < 4; j++) {
                if (isLeaderSelected[j]){
                    numberOfLeaderToSend[k] = j;
                    k++;
                }
            }

            //send information to server
            try {
                if (guiView.playerNumber == 3) {
                    guiView.setInitialResources(temp, resourceTypes);
                    guiView.isSuccessReceived();
                }
                else if (resourceTypes != null) {
                    guiView.setInitialResources(resourceTypes);
                    guiView.isSuccessReceived();
                }

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

    /**
     * Method that converts LeaderCard to ImageView
     * @param leader LeaderCard to convert
     * @param image image to set
     */
    private void leaderToImageName(LeaderCard leader, ImageView image){
        String temp = "images/Cards/LeaderCards/" + leader.getImage() + "-1.png";
        image.setImage(new Image(temp));
    }

    /**
     * Method that makes res images bigger and
     * assigns correct ResourceTypes depending
     * on which image was chosen
     * @param mouseEvent click on image chosen
     */
    private void imageResBiggerAndToResourceType(MouseEvent mouseEvent){
        ImageView imageView = (ImageView) mouseEvent.getSource();
        imageView.setFitWidth(50);
        imageView.setFitHeight(50);
        if (imageView == iGold || imageView == iGold2)
            resourceTypes = ResourceTypes.GOLD;
        else if (imageView == iServant || imageView == iServant2)
            resourceTypes = ResourceTypes.SERVANT;
        else if (imageView == iShield || imageView == iShield2)
            resourceTypes = ResourceTypes.SHIELD;
        else if (imageView == iStone || imageView == iStone2)
            resourceTypes = ResourceTypes.STONE;
    }

    /**
     * Method invoked from GUIView when server notify a disconnection
     */
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
