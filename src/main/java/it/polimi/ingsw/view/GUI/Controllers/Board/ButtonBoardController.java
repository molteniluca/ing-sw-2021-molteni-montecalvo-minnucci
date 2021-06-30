package it.polimi.ingsw.view.GUI.Controllers.Board;

import it.polimi.ingsw.view.GUI.Controllers.GenericController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;

public class ButtonBoardController extends GenericController {
    private static ButtonBoardController buttonBoardController;
    @FXML
    Button bCardDealer, bProduce, bOtherPlayers, bEndTurn;

    @FXML
    ImageView tokenImage;

    @FXML
    void initialize(){
        buttonBoardController = this;
        if(guiView.game.getNumPlayers()==1) {
            bOtherPlayers.setVisible(false);
        }
    }

    public void endTurn() throws IOException {
        guiView.endTurn();
        boolean response = guiView.isSuccessReceived();
        if(response && guiView.game.getNumPlayers()!=1){
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
            if(guiView.game.getSelfPLayingTurn().getLorenzo().getLastAction()!=null)
                tokenImage.setImage(new Image(guiView.game.getSelfPLayingTurn().getLorenzo().getLastAction().getTokenImage()));
                }
        );
    }
}
