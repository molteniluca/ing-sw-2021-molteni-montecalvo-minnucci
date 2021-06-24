package it.polimi.ingsw.view.GUI;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;

public class InitialLeaderSelectionController {

    @FXML
    Label lChooseResource;

    @FXML
    GridPane gChooseResource;


    @FXML
    void initialize(){
        //if player can choose resources:
        lChooseResource.setOpacity(1);
        gChooseResource.setOpacity(1);
    }

    public void selectLeaderCard(MouseEvent mouseEvent) {

    }

    public void confirmLeaders(ActionEvent actionEvent) throws IOException {
        //check two (no less no more) leader cards selected

        //send leaders to server

        //open gameBoard
        Parent gameBoardViewParent = FXMLLoader.load(ClassLoader.getSystemResource("FXML/GameBoard.fxml"));

        Scene gameBoardScene = new Scene(gameBoardViewParent);

        Stage gameBoardStage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();

        gameBoardStage.setTitle("Game Board");
        gameBoardStage.setScene(gameBoardScene);
        gameBoardStage.show();
    }

    public void chooseResource(MouseEvent mouseEvent) {

    }
}
