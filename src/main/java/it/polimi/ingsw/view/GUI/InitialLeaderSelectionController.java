package it.polimi.ingsw.view.GUI;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;

import static it.polimi.ingsw.view.GUI.GUI.game;

public class InitialLeaderSelectionController {

    int numberOfSelectedLeader;

    @FXML
    Label lChooseResource, lWrongNumberOfLeaders;

    @FXML
    GridPane gChooseResource;

    @FXML
    ImageView leader1, leader2, leader3, leader4;

    private boolean isLeader1Selected, isLeader2Selected, isLeader3Selected, isLeader4Selected;


    @FXML
    void initialize(){
        //TODO if player is 1:
        /*
        lChooseResource.setOpacity(0);
        gChooseResource.setOpacity(0);
        gChooseResource.setDisable(true); //check if res could be pressed if 1 player plays.

         */
    }

    public void selectLeaderCard1(MouseEvent mouseEvent) {
        isLeader1Selected = selectLeader(leader1, isLeader1Selected);
    }

    public void selectLeaderCard2(MouseEvent mouseEvent) {
        isLeader2Selected = selectLeader(leader2, isLeader2Selected);
    }

    public void selectLeaderCard3(MouseEvent mouseEvent) {
        isLeader3Selected = selectLeader(leader3, isLeader3Selected);
    }

    public void selectLeaderCard4(MouseEvent mouseEvent) {
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
        //TODO if num player is 2 or 3, choose one resource and send it to server
        gChooseResource.setDisable(true);

        //TODO if num player is 4, choose another resource
        lChooseResource.setText("Choose another resource");
    }
}
