package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.model.cards.DevelopmentCard;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Stack;

import static it.polimi.ingsw.view.GUI.GUI.game;
import static it.polimi.ingsw.view.GUI.GUI.playerNumber;

public class CardDealerController {

    boolean isProdCardSelected[] = new boolean[12];
    int counter;

    @FXML
    ImageView ipc1_1, ipc1_2, ipc1_3, ipc1_4, ipc2_1, ipc2_2, ipc2_3, ipc2_4, ipc3_1, ipc3_2, ipc3_3, ipc3_4;

    ImageView[] ipc;

    @FXML
    Button bGameBoard;

    @FXML

    void initialize() {
        ipc = new ImageView[]{ipc1_1, ipc1_2, ipc1_3, ipc1_4, ipc2_1, ipc2_2, ipc2_3, ipc2_4, ipc3_1, ipc3_2, ipc3_3, ipc3_4};
        counter = 0;
        /*
        Stack<DevelopmentCard>[][] cardMatrix = game.getPlayerTurn(playerNumber).getPlayer().getPersonalBoard().getGeneralBoard().getCardDealer().getCardMatrix();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                developmentCardToImageName(cardMatrix[i][j].peek(), ipc[counter]);
                counter++;
            }
        }

         */
    }



    private void developmentCardToImageName(DevelopmentCard developmentCard, ImageView image){
        String temp = "images/Cards/LeaderCards/" + developmentCard.getImage() + ".png";
        image.setImage(new Image(temp));
    }

    public void returnToGameBoard(ActionEvent actionEvent) throws IOException {
        openGameBoard(actionEvent);
    }

    protected static void openGameBoard(ActionEvent actionEvent) throws IOException {
        Parent GameBoardViewParent = FXMLLoader.load(ClassLoader.getSystemResource("FXML/GameBoard.fxml"));

        Scene GameBoardScene = new Scene(GameBoardViewParent);

        Stage GameBoardStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

        GameBoardStage.setTitle("Game Board");
        GameBoardStage.setScene(GameBoardScene);
        GameBoardStage.show();
    }

    public void buyProductionCard1_1(MouseEvent mouseEvent) {
        isProdCardSelected[0] = selectProductionCard(ipc[0], isProdCardSelected[0]);
    }

    public void buyProductionCard1_2(MouseEvent mouseEvent) {
        isProdCardSelected[1] = selectProductionCard(ipc[1], isProdCardSelected[1]);
    }

    public void buyProductionCard1_3(MouseEvent mouseEvent) {
        isProdCardSelected[2] = selectProductionCard(ipc[2], isProdCardSelected[2]);
    }

    public void buyProductionCard1_4(MouseEvent mouseEvent) {
        isProdCardSelected[3] = selectProductionCard(ipc[3], isProdCardSelected[3]);
    }

    public void buyProductionCard2_1(MouseEvent mouseEvent) {
        isProdCardSelected[4] = selectProductionCard(ipc[4], isProdCardSelected[4]);
    }

    public void buyProductionCard2_2(MouseEvent mouseEvent) {
        isProdCardSelected[5] = selectProductionCard(ipc[5], isProdCardSelected[5]);
    }

    public void buyProductionCard2_3(MouseEvent mouseEvent) {
        isProdCardSelected[6] = selectProductionCard(ipc[6], isProdCardSelected[6]);
    }

    public void buyProductionCard2_4(MouseEvent mouseEvent) {
        isProdCardSelected[7] = selectProductionCard(ipc[7], isProdCardSelected[7]);
    }

    public void buyProductionCard3_1(MouseEvent mouseEvent) {
        isProdCardSelected[8] = selectProductionCard(ipc[8], isProdCardSelected[8]);
    }

    public void buyProductionCard3_2(MouseEvent mouseEvent) {
        isProdCardSelected[9] = selectProductionCard(ipc[9], isProdCardSelected[9]);
    }

    public void buyProductionCard3_3(MouseEvent mouseEvent) {
        isProdCardSelected[10] = selectProductionCard(ipc[10], isProdCardSelected[10]);
    }

    public void buyProductionCard3_4(MouseEvent mouseEvent) {
        isProdCardSelected[11] = selectProductionCard(ipc[11], isProdCardSelected[11]);
    }

    private boolean selectProductionCard(ImageView ipc, boolean isProdCardSelected){
        if(!isProdCardSelected) {
            //change layout to selected layout


            isProdCardSelected = true;
        }
        else{
            //change layout to unselected/normal layout


            isProdCardSelected = false;
        }
        return isProdCardSelected;
    }
}
