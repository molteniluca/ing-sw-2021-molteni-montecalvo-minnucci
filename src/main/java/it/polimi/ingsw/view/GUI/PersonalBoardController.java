package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.model.board.personal.storage.WarehouseDepots;
import it.polimi.ingsw.model.cards.DevelopmentCard;
import it.polimi.ingsw.model.resources.ResourceTypes;
import it.polimi.ingsw.model.resources.Resources;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import javafx.scene.input.MouseEvent;

import java.util.ArrayList;

import static it.polimi.ingsw.view.GUI.GUI.game;
import static it.polimi.ingsw.view.GUI.GUI.playerNumber;

public class PersonalBoardController {

    private int currentFaithPosition, currentLorenzoFaithPosition, numberOfProductionCard;

    @FXML
    ImageView if0, if1, if2, if3, if4, if5, if6, if7, if8, if9, if10, if11, if12, if13, if14, if15, if16, if17, if18, if19, if20, if21, if22, if23, if24;
    @FXML
    ImageView ifl0, ifl1, ifl2, ifl3, ifl4, ifl5, ifl6, ifl7, ifl8, ifl9, ifl10, ifl11, ifl12, ifl13, ifl14, ifl15, ifl16, ifl17, ifl18, ifl19, ifl20, ifl21, ifl22, ifl23, ifl24;

    ImageView[] faithImagePosition, faithLorenzoImagePosition;

    @FXML
    GridPane strongBoxGrid;

    @FXML
    Label goldLabel, servantLabel, shieldLabel, stoneLabel;

    @FXML
    ImageView lev1, lev2_1, lev2_2, lev3_1, lev3_2, lev3_3;

    ImageView[] level2Image, level3Image;

    @FXML
    ImageView slot1_1, slot1_2, slot1_3, slot2_1, slot2_2, slot2_3, slot3_1, slot3_2, slot3_3;

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
    void updateStrongBox(){
        Resources resStrongBox = game.getPlayerTurn(playerNumber).getPlayer().getPersonalBoard().getDeposit().getStrongBox().getResources();
        goldLabel.setText(String.valueOf(resStrongBox.getResourceNumber(ResourceTypes.GOLD)));
        servantLabel.setText(String.valueOf(resStrongBox.getResourceNumber(ResourceTypes.SERVANT)));
        shieldLabel.setText(String.valueOf(resStrongBox.getResourceNumber(ResourceTypes.SHIELD)));
        stoneLabel.setText(String.valueOf(resStrongBox.getResourceNumber(ResourceTypes.STONE)));
    }

    @FXML
    void updateWarehouse(){
        WarehouseDepots warehouseDepots = game.getPlayerTurn(playerNumber).getPlayer().getPersonalBoard().getDeposit().getWarehouseDepots();
        for(int i = 0; i<= warehouseDepots.getNumberLevels(); i++){
            ResourceTypes resourceTypes = warehouseDepots.getResourceTypeLevel(i);
            String imageName = "images/resources/" + resourceTypes.toString().toLowerCase() + ".png";
            changeLevelResources(imageName, warehouseDepots.getResourcesNumber(i), i);
        }

    }

    private void changeLevelResources(String imageName, int numberOfResources, int level){
        for(int j = 0; j<numberOfResources; j++){
            switch (level){
                case 0:
                    lev1.setImage(new Image(imageName));
                    break;
                case 1:
                    level2Image[j].setImage(new Image(imageName));
                    j++;
                    break;
                case 2:
                    level3Image[j].setImage(new Image(imageName));
                    j++;
                    break;
            }
        }
    }


    @FXML
    void updateProductionCards(){
        ArrayList<DevelopmentCard> developmentCard = game.getPlayerTurn(playerNumber).getPlayer().getPersonalBoard().getCardBoard().getDevelopmentCards();
        //if(developmentCard.size() != numberOfProductionCard){



       // }
    }

    void updatePersonalBoard(){
        updateFaithTrack();
        updateStrongBox();
        updateWarehouse();
        updateProductionCards();
    }



    @FXML
    void initialize(){
        faithImagePosition = new ImageView[]{if0, if1, if2, if3, if4, if5, if6, if7, if8, if9, if10, if11, if12, if13, if14, if15, if16, if17, if18, if19, if20, if21, if22, if23, if24};
        faithLorenzoImagePosition = new ImageView[]{ifl0, ifl1, ifl2, ifl3, ifl4, ifl5, ifl6, ifl7, ifl8, ifl9, ifl10, ifl11, ifl12, ifl13, ifl14, ifl15, ifl16, ifl17, ifl18, ifl19, ifl20, ifl21, ifl22, ifl23, ifl24};
        level2Image = new ImageView[]{lev2_1, lev2_2};
        level3Image = new ImageView[]{lev3_1, lev3_2, lev3_3};


        //updatePersonalBoard();
    }

    public void showStrongBox(MouseEvent mouseEvent) {
        strongBoxGrid.setOpacity(1);
    }

    public void hideStrongBox(MouseEvent mouseEvent) {
        strongBoxGrid.setOpacity(0);
    }
}
