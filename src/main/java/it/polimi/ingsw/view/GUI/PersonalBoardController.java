package it.polimi.ingsw.view.GUI;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import static it.polimi.ingsw.view.GUI.GUI.game;
import static it.polimi.ingsw.view.GUI.GUI.playerNumber;

public class PersonalBoardController {

    private int currentFaithPosition, currentLorenzoFaithPosition;

    @FXML
    ImageView if0, if1, if2, if3, if4, if5, if6, if7, if8, if9, if10, if11, if12, if13, if14, if15, if16, if17, if18, if19, if20, if21, if22, if23, if24;
    @FXML
    ImageView ifl0, ifl1, ifl2, ifl3, ifl4, ifl5, ifl6, ifl7, ifl8, ifl9, ifl10, ifl11, ifl12, ifl13, ifl14, ifl15, ifl16, ifl17, ifl18, ifl19, ifl20, ifl21, ifl22, ifl23, ifl24;

    ImageView[] faithImagePosition, faithLorenzoImagePosition;

    @FXML
    void updateFaithTrack(){
        faithImagePosition[currentFaithPosition].setVisible(false);
        currentFaithPosition = game.getPlayerTurn(playerNumber).getPlayer().getPersonalBoard().getFaithTrack().getPosition();
        faithImagePosition[currentFaithPosition].setVisible(true);

        if(game.getNumPlayers()==1){
            faithLorenzoImagePosition[currentLorenzoFaithPosition].setVisible(false);
            currentLorenzoFaithPosition = game.getSelfPLayingTurn().getLorenzo().getFaithTrack().getPosition();
            faithLorenzoImagePosition[currentLorenzoFaithPosition].setVisible(true);
        }
    }

    @FXML
    void updateStrongBox(){}

    @FXML
    void updateWarehouse(){}

    @FXML
    void updateProductionCards(){}

    void updatePersonalBoard(){
        updateFaithTrack();
        updateStrongBox();//...
    }

    @FXML
    void initialize(){
        //updatePersonalBoard();
        faithImagePosition = new ImageView[]{if0, if1, if2, if3, if4, if5, if6, if7, if8, if9, if10, if11, if12, if13, if14, if15, if16, if17, if18, if19, if20, if21, if22, if23, if24};
        faithLorenzoImagePosition = new ImageView[]{ifl0, ifl1, ifl2, ifl3, ifl4, ifl5, ifl6, ifl7, ifl8, ifl9, ifl10, ifl11, ifl12, ifl13, ifl14, ifl15, ifl16, ifl17, ifl18, ifl19, ifl20, ifl21, ifl22, ifl23, ifl24};

    }

}
