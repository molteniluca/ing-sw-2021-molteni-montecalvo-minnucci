package it.polimi.ingsw.view.GUI.Controllers;

import it.polimi.ingsw.model.cards.DevelopmentCard;
import it.polimi.ingsw.view.GUI.GUIView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Stack;

public class CardDealerController extends GenericController{

    boolean[][] isProdCardSelected = new boolean[3][4];
    boolean confirm;
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
    GridPane gCardDealer;

    @FXML
    void initialize() {
        ipc = new ImageView[][]{{ipc1_1, ipc1_2, ipc1_3, ipc1_4},{ ipc2_1, ipc2_2, ipc2_3, ipc2_4},{ ipc3_1, ipc3_2, ipc3_3, ipc3_4}};
        counter = 0;

        Stack<DevelopmentCard>[][] cardMatrix = guiView.game.getPlayerTurn(guiView.playerNumber).getPlayer().getPersonalBoard().getGeneralBoard().getCardDealer().getCardMatrix();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
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
        if (numberOfSelectedCards == 0 || confirm) {
            goToGameBoard(actionEvent);
        }
        else {
            //write "you can only take 1 card"
            lWrongSelection.setText("You need to press confirm or select no one card");
        }
    }

    protected static void goToGameBoard(ActionEvent actionEvent) throws IOException {
        Parent GameBoardViewParent = FXMLLoader.load(ClassLoader.getSystemResource("FXML/GameBoard.fxml"));

        Scene GameBoardScene = new Scene(GameBoardViewParent);

        Stage GameBoardStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

        GameBoardStage.setTitle("Game Board");
        GameBoardStage.setScene(GameBoardScene);
        GameBoardStage.show();
    }

    public void buyProductionCard(MouseEvent event) {
        column = GridPane.getColumnIndex((Node) event.getSource());
        row = GridPane.getRowIndex((Node) event.getSource());
        isProdCardSelected[row][column] = selectProductionCard(ipc[row][column], isProdCardSelected[row][column]);
    }

    private boolean selectProductionCard(ImageView ipc, boolean isProdCardSelected){
        if(!isProdCardSelected) {
            //change layout to selected layout


            isProdCardSelected = true;
            numberOfSelectedCards++;
        }
        else{
            //change layout to unselected/normal layout


            isProdCardSelected = false;
            numberOfSelectedCards--;
        }
        return isProdCardSelected;
    }

    public void confirmCard(ActionEvent actionEvent) throws IOException {
        if (numberOfSelectedCards == 1){
            guiView.marketBuyCard(row,column,comboBox.getValue()-1);
            //if server sends success (enough res to buy card) or no one card is selected
            if (guiView.isSuccessReceived()) //FIXME when server messages work
                confirm = true;
            else {
                lWrongSelection.setText(guiView.waitAndGetResponse().toString());
            }
        }
        else if (numberOfSelectedCards > 1){
            lWrongSelection.setText("You can buy only one card per turn");
        }
    }
}
