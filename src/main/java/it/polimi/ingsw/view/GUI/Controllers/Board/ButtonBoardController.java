package it.polimi.ingsw.view.GUI.Controllers.Board;

import it.polimi.ingsw.view.GUI.Controllers.GenericController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

import static it.polimi.ingsw.network.NetworkMessages.SUCCESS;

public class ButtonBoardController extends GenericController {
    @FXML
    Button bMarket, bCardDealer, bProduce, bLeaders, bOtherPlayers, bEndTurn;

    @FXML
    void initialize(){

    }

    public void endTurn() throws IOException {
        guiView.endTurn();
        if(guiView.isSuccessReceived() && guiView.game.getNumPlayers()!=1){
            guiView.notifyTurnEnded();
        }
    }

    public void openCardDealer(ActionEvent actionEvent) throws IOException {
        Parent cardDealerViewParent = FXMLLoader.load(ClassLoader.getSystemResource("FXML/CardDealer.fxml"));

        Scene cardDealerScene = new Scene(cardDealerViewParent);

        Stage cardDealerStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

        cardDealerStage.setTitle("Game Board");
        cardDealerStage.setScene(cardDealerScene);
        cardDealerStage.show();
    }
}
