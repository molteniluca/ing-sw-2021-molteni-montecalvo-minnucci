package it.polimi.ingsw.view.GUI.Controllers.Board;

import it.polimi.ingsw.model.board.personal.PersonalBoard;
import it.polimi.ingsw.model.resources.ResourceTypes;
import it.polimi.ingsw.view.GUI.Controllers.GenericController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.EnumSet;

import static it.polimi.ingsw.model.resources.ResourceTypes.*;

public class ButtonBoardController extends GenericController {
    private static ButtonBoardController buttonBoardController;
    private boolean producing = false;
    private boolean productionEnded = false;

    @FXML
    private ComboBox<String> productionComboBox;
    @FXML
    private ComboBox<ResourceTypes> comboRes1, comboRes2, comboRes3;
    @FXML
    private Button bEndProduction, bProduce, bOtherPlayers, bEndTurn;
    @FXML
    private ImageView tokenImage;
    @FXML
    public Rectangle rBlockProduceAndBuyProduction, rForProduce, rButtonBoard;

    @FXML
    private void initialize(){
        buttonBoardController = this;
        if(guiView.game.getNumPlayers()==1)
            bOtherPlayers.setVisible(false);
    }

    /**
     * Method invoked when player ends his turn
     */
    public void endTurn() {
        try {
            guiView.endTurn();
            boolean response = guiView.isSuccessReceived();
            if(response && guiView.game.getNumPlayers()!=1){
                guiView.notifyTurnEnded();
            }
            productionEnded =false;
        } catch (IOException e) {
            guiView.notifyDisconnection();
        }
    }

    /**
     * Method invoked when player wants to buy a production card
     * @param actionEvent click on button to open card dealer
     * @throws IOException if it cannot open fxml file
     */
    public void openCardDealer(ActionEvent actionEvent) throws IOException {
        Parent cardDealerViewParent = FXMLLoader.load(ClassLoader.getSystemResource("FXML/CardDealer.fxml"));

        Scene cardDealerScene = new Scene(cardDealerViewParent);

        Stage cardDealerStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

        cardDealerStage.setTitle("Game Board");
        cardDealerStage.setScene(cardDealerScene);
        cardDealerStage.show();
    }

    /**
     * Method that allow player to see other game board
     * @param actionEvent click on button
     * @throws IOException if it cannot open fxml file
     */
    public void goToOtherPlayers(ActionEvent actionEvent) throws IOException {
        Parent cardDealerViewParent = FXMLLoader.load(ClassLoader.getSystemResource("FXML/OtherPlayerBoards.fxml"));

        Scene cardDealerScene = new Scene(cardDealerViewParent);

        Stage cardDealerStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

        cardDealerStage.setTitle("Other Players");
        cardDealerStage.setScene(cardDealerScene);
        cardDealerStage.show();
    }

    /**
     * Getter of buttonBoardController to manage it from GameBoardController
     * @return the buttonBoardController
     */
    public static ButtonBoardController getButtonBoardController() {
        return buttonBoardController;
    }

    /**
     * Method invoked when player clicks on produce button
     */
    public void produce() {
        PersonalBoard p = guiView.game.getPlayerTurn(guiView.playerNumber).getPlayer().getPersonalBoard();

        String selected = productionComboBox.getValue();
        try {
            if(selected.equals("Default Production")){
                guiView.productionProd1(comboRes1.getValue(), comboRes2.getValue(), comboRes3.getValue());
            }else{
                if(selected.contains("Leader")){
                    if(selected.contains("1")){
                        guiView.productionProd3(p.getAvailableProductions().get(0).getProductionCost(), comboRes1.getValue());
                    }else if(selected.contains("2")) {
                        guiView.productionProd3(p.getAvailableProductions().get(0).getProductionCost(), comboRes1.getValue());
                    }
                } else{
                    if(selected.contains("1")){
                        guiView.productionProd2(0);
                    }else if(selected.contains("2")){
                        guiView.productionProd2(1);
                    }else if(selected.contains("3")){
                        guiView.productionProd2(2);
                    }
                }

            }
            if(guiView.isSuccessReceived())
                producing=true;
            else if(!producing){
                guiView.endProduction();
                guiView.isSuccessReceived();
            }
        } catch (IOException e) {
            guiView.notifyDisconnection();
        }
    }

    /**
     * Method invoked when player selects one combobox
     */
    public void selectedCombo() {
        updateCombos();
    }

    /**
     * Method that updates comboBoxes based on player choice
     */
    private void updateCombos() {
        String selected = productionComboBox.getValue();
        if(selected!=null) {
            if (selected.equals("Default Production")) {
                comboRes1.setVisible(true);
                comboRes2.setVisible(true);
                comboRes3.setVisible(true);
                comboRes1.getItems().clear();
                comboRes2.getItems().clear();
                comboRes3.getItems().clear();
                for (ResourceTypes r : EnumSet.of(GOLD, STONE, SHIELD, SERVANT)) {
                    comboRes1.getItems().add(r);
                    comboRes2.getItems().add(r);
                    comboRes3.getItems().add(r);
                }
                comboRes1.getSelectionModel().selectFirst();
                comboRes2.getSelectionModel().selectFirst();
                comboRes3.getSelectionModel().selectFirst();
            } else {
                if (selected.contains("Leader")) {
                    comboRes1.setVisible(true);
                } else {
                    comboRes1.setVisible(false);
                }
                comboRes2.setVisible(false);
                comboRes3.setVisible(false);
            }
        }
    }

    /**
     * Method invoked when player decides to end his productions
     */
    public void endProduction() {
        try {
            guiView.endProduction();
            guiView.isSuccessReceived();
            producing=false;
            productionEnded=true;
            update();
        } catch (IOException e) {
            guiView.notifyDisconnection();
        }
    }

    /**
     * Method that allows or not to click in buttonBoard areas thanks to little-gray rectangles
     */
    private void setClickable(){
        bEndTurn.setDisable(!guiView.game.getPlayerTurn(guiView.playerNumber).isAlreadyDone());
        rBlockProduceAndBuyProduction.setVisible(! guiView.game.getPlayerTurn(guiView.playerNumber).isWaitingForAction());
        rButtonBoard.setVisible(guiView.game.getPlayerTurn(guiView.playerNumber).isHandlingSwap());
        rForProduce.setVisible(guiView.game.getPlayerTurn(guiView.playerNumber).isProducing());
    }

    /**
     * Method that is invoked when client received an updated game and update the entire buttonBoard view
     */
    @Override
    public void update() {
        Platform.runLater(() -> {
            setClickable();
            if(guiView.game.getNumPlayers() == 1) {
                if (guiView.game.getSelfPLayingTurn().getLorenzo().getLastAction() != null)
                    tokenImage.setImage(new Image(guiView.game.getSelfPLayingTurn().getLorenzo().getLastAction().getTokenImage()));
            }

            if(!productionEnded) {
                bProduce.setVisible(true);
                productionComboBox.setVisible(true);
                bEndProduction.setVisible(producing);

                PersonalBoard p = guiView.game.getPlayerTurn(guiView.playerNumber).getPlayer().getPersonalBoard();
                productionComboBox.getItems().clear();

                if (p.getProd2() != null) {
                    if (p.isProd1()) {
                        productionComboBox.getItems().add("Default Production");
                    }

                    for (int i = 0; i < p.getProd2().length; i++) {
                        if (p.getProd2()[i])
                            productionComboBox.getItems().add("Card " + (i + 1));
                    }

                    for (int i = 0; i < p.getAvailableProductions().size(); i++) {
                        productionComboBox.getItems().add("Leader " + (i + 1));
                    }
                } else {
                    productionComboBox.getItems().add("Default Production");
                    for (int i = 0; i < 3; i++) {
                        productionComboBox.getItems().add("Card " + (i + 1));
                    }
                    for (int i = 0; i < 2; i++) {
                        productionComboBox.getItems().add("Leader " + (i + 1));
                    }
                }
                productionComboBox.getSelectionModel().selectFirst();

                if (productionComboBox.getValue() != null) {
                    updateCombos();
                } else {
                    bProduce.setVisible(false);
                    comboRes1.setVisible(false);
                    comboRes2.setVisible(false);
                    comboRes3.setVisible(false);
                    productionComboBox.setVisible(false);
                }
            }else{
                bProduce.setVisible(false);
                comboRes1.setVisible(false);
                comboRes2.setVisible(false);
                comboRes3.setVisible(false);
                productionComboBox.setVisible(false);
                bEndProduction.setVisible(false);
            }
        });
    }
}
