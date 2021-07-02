package it.polimi.ingsw.view.GUI.Controllers;

import it.polimi.ingsw.model.cards.DevelopmentCard;
import it.polimi.ingsw.model.resources.ResourceTypes;
import it.polimi.ingsw.model.resources.Resources;
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
    private Label goldLabel, servantLabel, shieldLabel, stoneLabel;

    @FXML
    private ImageView ipc1_1, ipc1_2, ipc1_3, ipc1_4, ipc2_1, ipc2_2, ipc2_3, ipc2_4, ipc3_1, ipc3_2, ipc3_3, ipc3_4;
    private ImageView[][] ipc;
    @FXML // fx:id="comboBox"
    private ComboBox<Integer> comboBox;

    @FXML
    private ImageView slot1, slot2, slot3;

    @FXML
    void initialize() {
        ipc = new ImageView[][]{{ipc1_1, ipc1_2, ipc1_3, ipc1_4},{ ipc2_1, ipc2_2, ipc2_3, ipc2_4},{ ipc3_1, ipc3_2, ipc3_3, ipc3_4}};
        ImageView[] slots = new ImageView[]{slot1, slot2, slot3};
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
        Resources res = guiView.game.getPlayerTurn(guiView.playerNumber).getPlayer().getPersonalBoard().getDeposit().getTotalResources();
        goldLabel.setText(Integer.toString(res.getResourceNumber(ResourceTypes.GOLD)));
        servantLabel.setText(Integer.toString(res.getResourceNumber(ResourceTypes.SERVANT)));
        shieldLabel.setText(Integer.toString(res.getResourceNumber(ResourceTypes.SHIELD)));
        stoneLabel.setText(Integer.toString(res.getResourceNumber(ResourceTypes.STONE)));
        
        DevelopmentCard[] developmentCards = guiView.game.getPlayerTurn(guiView.playerNumber).getPlayer().getPersonalBoard().getCardBoard().getUpperDevelopmentCards();
        for (int i = 0; i < 3; i++) {
            if (developmentCards[i]!=null)
                developmentCardToImageName(developmentCards[i], slots[i]);
        }
    }

    /**
     * Method that set column and row indexes for gridPane,
     * with the aim to help on the user selection of gridPane cell
     */
    private void setColumnRowIndexes(){
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                GridPane.setColumnIndex(ipc[i][j], j);
                GridPane.setRowIndex(ipc[i][j], i);
            }
        }
    }

    /**
     * Method invoked when player selects one production card
     * @param event click on one production card
     */
    public void buyProductionCard(MouseEvent event) {
        column = GridPane.getColumnIndex((Node) event.getSource());
        row = GridPane.getRowIndex((Node) event.getSource());
        isProdCardSelected[row][column] = selectProductionCard(ipc[row][column], isProdCardSelected[row][column]);
    }

    /**
     * Method that keeps track of selected leader card numbers
     * and makes images clicked bigger
     * @param ipc image of production card
     * @param isProdCardSelected boolean parameter true if production card is selected (before action)
     * @return boolean true if production card is selected (after action)
     */
    private boolean selectProductionCard(ImageView ipc, boolean isProdCardSelected){
        if(!isProdCardSelected) {
            ipc.setFitWidth(180.0);
            ipc.setFitHeight(247.5);

            isProdCardSelected = true;
            numberOfSelectedCards++;
        }
        else{
            ipc.setFitWidth(160.0);
            ipc.setFitHeight(226.875);

            isProdCardSelected = false;
            numberOfSelectedCards--;
        }
        return isProdCardSelected;
    }

    /**
     * Method invoked
     * @param actionEvent
     * @throws IOException
     */
    public void confirmCard(ActionEvent actionEvent) throws IOException {
        if (numberOfSelectedCards == 1){
            if(comboBox.getValue()!=null){
                try {
                    guiView.marketBuyCard(row,column,comboBox.getValue()-1);
                } catch (IOException e) {
                    guiView.notifyDisconnection();
                }
                if (guiView.isSuccessReceived()) {
                    GameBoardController.goToGameBoard(actionEvent);
                }
            }else
                showError("Select a place in the card board");
        }
        else if (numberOfSelectedCards > 1){
            showError("You can buy only one card per turn");
        }else{
            showError("You need to select a card!");
        }
    }

    /**
     * Method that shows error
     * @param error error from server
     */
    private void showError(String error) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(error);
            alert.setContentText("Retry");

            alert.showAndWait();
        });
    }

    /**
     * Method that converts DevelopmentCard to ImageView
     * @param developmentCard development card to convert
     * @param image image related to development card
     */
    private void developmentCardToImageName(DevelopmentCard developmentCard, ImageView image){
        String temp = "images/Cards/DevelopmentCards/" + developmentCard.getImage() + "-1.png";
        image.setImage(new Image(temp));
    }

    /**
     * Method invoked when player wants to come back to game board
     * @param actionEvent click on button
     * @throws IOException if fxml file is not reachable
     */
    public void returnToGameBoard(ActionEvent actionEvent) throws IOException {
        openGameBoard(actionEvent);
    }

    /**
     * Method invoked to return to game board
     * @param actionEvent click on button
     * @throws IOException if fxml is not reachable
     */
    public void openGameBoard(ActionEvent actionEvent) throws IOException {
        GameBoardController.goToGameBoard(actionEvent);
    }
}
