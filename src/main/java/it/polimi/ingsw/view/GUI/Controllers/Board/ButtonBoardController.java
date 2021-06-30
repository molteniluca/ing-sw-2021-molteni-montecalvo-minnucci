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
import javafx.stage.Stage;

import java.io.IOException;
import java.util.EnumSet;

import static it.polimi.ingsw.model.resources.ResourceTypes.*;
import static it.polimi.ingsw.model.resources.ResourceTypes.SERVANT;

public class ButtonBoardController extends GenericController {
    private static ButtonBoardController buttonBoardController;
    private boolean producing = false;
    private boolean productionEnded = false;

    @FXML
    public ComboBox productionComboBox;
    @FXML
    public ComboBox comboRes1, comboRes2, comboRes3;
    @FXML
    public Button bEndProduction;
    @FXML
    Button bCardDealer, bProduce, bOtherPlayers, bEndTurn;
    @FXML
    ImageView tokenImage;

    @FXML
    void initialize(){
        buttonBoardController = this;
        if(guiView.game.getNumPlayers()==1)
            bOtherPlayers.setVisible(false);
    }

    public void endTurn() throws IOException {
        guiView.endTurn();
        boolean response = guiView.isSuccessReceived();
        if(response && guiView.game.getNumPlayers()!=1){
            guiView.notifyTurnEnded();
        }
        productionEnded =false;
    }

    public void openCardDealer(ActionEvent actionEvent) throws IOException {
        Parent cardDealerViewParent = FXMLLoader.load(ClassLoader.getSystemResource("FXML/CardDealer.fxml"));

        Scene cardDealerScene = new Scene(cardDealerViewParent);

        Stage cardDealerStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

        cardDealerStage.setTitle("Game Board");
        cardDealerStage.setScene(cardDealerScene);
        cardDealerStage.show();
    }

    public void goToOtherPlayers(ActionEvent actionEvent) throws IOException {
        Parent cardDealerViewParent = FXMLLoader.load(ClassLoader.getSystemResource("FXML/OtherPlayerBoards.fxml"));

        Scene cardDealerScene = new Scene(cardDealerViewParent);

        Stage cardDealerStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

        cardDealerStage.setTitle("Other Players");
        cardDealerStage.setScene(cardDealerScene);
        cardDealerStage.show();
    }

    public static ButtonBoardController getButtonBoardController() {
        return buttonBoardController;
    }

    @Override
    public void update() {
        Platform.runLater(() -> {
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
                        productionComboBox.getItems().addAll("Default Production");
                    }

                    for (int i = 0; i < p.getProd2().length; i++) {
                        if (p.getProd2()[i])
                            productionComboBox.getItems().addAll("Card " + (i + 1));
                    }

                    for (int i = 0; i < p.getAvailableProductions().size(); i++) {
                        productionComboBox.getItems().addAll("Leader " + (i + 1));
                    }
                } else {
                    productionComboBox.getItems().addAll("Default Production");
                    for (int i = 0; i < 3; i++) {
                        productionComboBox.getItems().addAll("Card " + (i + 1));
                    }
                    for (int i = 0; i < 2; i++) {
                        productionComboBox.getItems().addAll("Leader " + (i + 1));
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

    public void produce(ActionEvent actionEvent) {
        PersonalBoard p = guiView.game.getPlayerTurn(guiView.playerNumber).getPlayer().getPersonalBoard();

        String selected = (String)productionComboBox.getValue();
        try {
            if(selected.equals("Default Production")){
                guiView.productionProd1((ResourceTypes) comboRes1.getValue(),(ResourceTypes) comboRes2.getValue(), (ResourceTypes) comboRes3.getValue());
                guiView.isSuccessReceived();
                producing=true;
            }else{
                if(selected.contains("Leader")){
                    if(selected.contains("1")){
                        guiView.productionProd3(p.getAvailableProductions().get(0).getProductionCost(), (ResourceTypes) comboRes1.getValue());
                    }else if(selected.contains("2")) {
                        guiView.productionProd3(p.getAvailableProductions().get(0).getProductionCost(), (ResourceTypes) comboRes1.getValue());
                    }
                    guiView.isSuccessReceived();
                    producing=true;
                } else{
                    if(selected.contains("1")){
                        guiView.productionProd2(0);
                    }else if(selected.contains("2")){
                        guiView.productionProd2(1);
                    }else if(selected.contains("3")){
                        guiView.productionProd2(2);
                    }
                    guiView.isSuccessReceived();
                    producing=true;
                }
            }
        } catch (IOException e) {
            guiView.notifyDisconnection();
        }
    }

    public void selectedCombo(ActionEvent actionEvent) {
        updateCombos();
    }

    private void updateCombos() {
        String selected = (String)productionComboBox.getValue();
        if(selected!=null) {
            if (selected.equals("Default Production")) {
                comboRes1.setVisible(true);
                comboRes2.setVisible(true);
                comboRes3.setVisible(true);
                comboRes1.getItems().clear();
                comboRes2.getItems().clear();
                comboRes3.getItems().clear();
                for (ResourceTypes r : EnumSet.of(GOLD, STONE, SHIELD, SERVANT)) {
                    comboRes1.getItems().addAll(r);
                    comboRes2.getItems().addAll(r);
                    comboRes3.getItems().addAll(r);
                }
                comboRes1.getSelectionModel().selectFirst();
                comboRes2.getSelectionModel().selectFirst();
                comboRes3.getSelectionModel().selectFirst();
            } else {
                if (selected.contains("Leader")) {
                    comboRes1.setVisible(true);
                    comboRes2.setVisible(false);
                    comboRes3.setVisible(false);
                } else {
                    comboRes1.setVisible(false);
                    comboRes2.setVisible(false);
                    comboRes3.setVisible(false);
                }
            }
        }
    }

    public void endProduction(ActionEvent actionEvent) {
        try {
            guiView.endProduction();
            guiView.isSuccessReceived();
            producing=false;
            productionEnded=true;
            update();
        } catch (IOException e) {
            //TODO DISCONNECT
        }
    }
}
