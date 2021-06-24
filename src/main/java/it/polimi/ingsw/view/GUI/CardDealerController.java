package it.polimi.ingsw.view.GUI;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;

public class CardDealerController {

    @FXML
    ImageView ipc1_1, ipc1_2, ipc1_3, ipc1_4, ipc2_1, ipc2_2, ipc2_3, ipc2_4, ipc3_1, ipc3_2, ipc3_3, ipc3_4;

    @FXML
    Button bGameBoard;

    @FXML
    void initialize(){

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
}
