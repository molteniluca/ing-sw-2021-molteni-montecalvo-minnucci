package it.polimi.ingsw.view.GUI.Controllers;

import it.polimi.ingsw.model.cards.DevelopmentCard;
import it.polimi.ingsw.view.GUI.Controllers.Board.GameBoardController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.util.Stack;

public class CardDealerController extends GenericController{

    boolean[][] isProdCardSelected = new boolean[3][4];
    int counter, numberOfSelectedCards;
    int row;
    int column;

    @FXML
    ImageView ipc1_1, ipc1_2, ipc1_3, ipc1_4, ipc2_1, ipc2_2, ipc2_3, ipc2_4, ipc3_1, ipc3_2, ipc3_3, ipc3_4;

    ImageView[][] ipc;

    @FXML
    Button bGameBoard, bConfirm;

    @FXML // fx:id="comboBox"
    private ComboBox<Integer> comboBox;

    @FXML
    Label lWrongSelection;

    @FXML
    void initialize() {
        ipc = new ImageView[][]{{ipc1_1, ipc1_2, ipc1_3, ipc1_4},{ ipc2_1, ipc2_2, ipc2_3, ipc2_4},{ ipc3_1, ipc3_2, ipc3_3, ipc3_4}};
        counter = 0;

        Stack<DevelopmentCard>[][] cardMatrix = guiView.game.getPlayerTurn(guiView.playerNumber).getPlayer().getPersonalBoard().getGeneralBoard().getCardDealer().getCardMatrix();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                if(!cardMatrix[i][j].empty())
                    developmentCardToImageName(cardMatrix[i][j].peek(), ipc[i][j]);
            }
        }
        setColumnRowIndexes();
        comboBox.getItems().addAll(1,2,3);
    }

    private void setColumnRowIndexes(){
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                GridPane.setColumnIndex(ipc[i][j], j);
                GridPane.setRowIndex(ipc[i][j], i);
            }
        }
    }

    private void developmentCardToImageName(DevelopmentCard developmentCard, ImageView image){
        String temp = "images/Cards/DevelopmentCards/" + developmentCard.getImage() + "-1.png";
        image.setImage(new Image(temp));
    }

    public void returnToGameBoard(ActionEvent actionEvent) throws IOException {
        openGameBoard(actionEvent);
    }

    public void openGameBoard(ActionEvent actionEvent) throws IOException {
        GameBoardController.goToGameBoard(actionEvent);
    }

    public void buyProductionCard(MouseEvent event) {
        column = GridPane.getColumnIndex((Node) event.getSource());
        row = GridPane.getRowIndex((Node) event.getSource());
        isProdCardSelected[row][column] = selectProductionCard(ipc[row][column], isProdCardSelected[row][column]);
    }

    private boolean selectProductionCard(ImageView ipc, boolean isProdCardSelected){
        if(!isProdCardSelected) {
            //change layout to selected layout TODO

            isProdCardSelected = true;
            numberOfSelectedCards++;
        }
        else{
            //change layout to unselected/normal layout TODO

            isProdCardSelected = false;
            numberOfSelectedCards--;
        }
        return isProdCardSelected;
    }

    public void confirmCard(ActionEvent actionEvent) throws IOException {
        if (numberOfSelectedCards == 1){
            if(comboBox.getValue()!=null){
                guiView.marketBuyCard(row,column,comboBox.getValue()-1);
                if (guiView.isSuccessReceived()) {
                    GameBoardController.goToGameBoard(actionEvent);
                }
            }else
                showError("Select a place in the card board");
        }
        else if (numberOfSelectedCards > 1){
            showError("You can buy only one card per turn");
        }
    }

    public void showError(String error) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(error);
            alert.setContentText("Retry");

            alert.showAndWait();
        });
    }
}
