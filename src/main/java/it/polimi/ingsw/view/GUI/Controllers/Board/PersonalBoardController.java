package it.polimi.ingsw.view.GUI.Controllers.Board;

import it.polimi.ingsw.model.board.personal.storage.WarehouseDepots;
import it.polimi.ingsw.model.cards.DevelopmentCard;
import it.polimi.ingsw.model.resources.ResourceTypes;
import it.polimi.ingsw.model.resources.Resources;
import it.polimi.ingsw.view.GUI.Controllers.GenericController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;

public class PersonalBoardController extends GenericController {
    private int currentFaithPosition, currentLorenzoFaithPosition;
    private int[] slotPosition = new int[3];
    private static PersonalBoardController personalBoardController;

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

    ImageView[][] slots;

    @FXML
    void updateFaithTrack(int player){
        faithImagePosition[currentFaithPosition].setVisible(false);
        currentFaithPosition = guiView.game.getPlayerTurn(player).getPlayer().getPersonalBoard().getFaithTrack().getPosition();
        faithImagePosition[currentFaithPosition].setVisible(true);

        if(guiView.game.getNumPlayers()==1){
            faithLorenzoImagePosition[currentLorenzoFaithPosition].setVisible(false);
            currentLorenzoFaithPosition = guiView.game.getSelfPLayingTurn().getLorenzo().getFaithTrack().getPosition();
            faithLorenzoImagePosition[currentLorenzoFaithPosition].setVisible(true);
        }
    }

    @FXML
    void updateStrongBox(int player){
        Resources resStrongBox = guiView.game.getPlayerTurn(player).getPlayer().getPersonalBoard().getDeposit().getStrongBox().getResources();
        goldLabel.setText(String.valueOf(resStrongBox.getResourceNumber(ResourceTypes.GOLD)));
        servantLabel.setText(String.valueOf(resStrongBox.getResourceNumber(ResourceTypes.SERVANT)));
        shieldLabel.setText(String.valueOf(resStrongBox.getResourceNumber(ResourceTypes.SHIELD)));
        stoneLabel.setText(String.valueOf(resStrongBox.getResourceNumber(ResourceTypes.STONE)));
    }

    @FXML
    void updateWarehouse(int player){
        WarehouseDepots warehouseDepots = guiView.game.getPlayerTurn(player).getPlayer().getPersonalBoard().getDeposit().getWarehouseDepots();
        for(int i = 0; i< warehouseDepots.getNumberLevels(); i++){
            int resourceNumber = warehouseDepots.getResourcesNumber(i);
            ResourceTypes resourceTypes = warehouseDepots.getResourceTypeLevel(i);
            if (resourceNumber > 0) {
                String imageName = "images/resources/" + resourceTypes.toString().toLowerCase() + ".png";
                changeLevelResources(imageName, warehouseDepots.getResourcesNumber(i), i);
            }
            else
                changeLevelResources(null, warehouseDepots.getLevel(i).getMaxResourceNumber(), i);
        }

    }

    private void changeLevelResources(String imageName, int numberOfResources, int level){
        Image image;
        if(imageName == null)
            image = null;
        else
            image = new Image(imageName);

        for(int j = 0; j<numberOfResources; j++){
            switch (level){
                case 0:
                    lev1.setImage(image);
                    break;
                case 1:
                    level2Image[j].setImage(image);
                    break;
                case 2:
                    level3Image[j].setImage(image);
                    break;
            }
        }
    }

    @FXML
    void updateProductionCards(int player){
        ArrayList<DevelopmentCard>[] developmentCard = guiView.game.getPlayerTurn(player).getPlayer().getPersonalBoard().getCardBoard().getDevelopmentCardsMatrix();

        for(int k=0;k<3;k++){
            for (int i = 0; i < 3; i++) {
                if (developmentCard[i].size() > k) {
                    updateSlotImage(k,i, developmentCard[i].get(k));
                }
            }
        }
    }

    private void updateSlotImage(int level,int slot, DevelopmentCard developmentCard){
        if(slots[slot][level].getImage()==null) {
            String nameImage = developmentCard.getImage();
            slots[slot][level].setImage(new Image("images/Cards/DevelopmentCards/" +nameImage+"-1.png"));
        }
    }

    void updatePersonalBoard(int player){
        updateFaithTrack(player);
        updateStrongBox(player);
        updateWarehouse(player);
        updateProductionCards(player);
    }



    @FXML
    void initialize(){
        personalBoardController=this;
        faithImagePosition = new ImageView[]{if0, if1, if2, if3, if4, if5, if6, if7, if8, if9, if10, if11, if12, if13, if14, if15, if16, if17, if18, if19, if20, if21, if22, if23, if24};
        faithLorenzoImagePosition = new ImageView[]{ifl0, ifl1, ifl2, ifl3, ifl4, ifl5, ifl6, ifl7, ifl8, ifl9, ifl10, ifl11, ifl12, ifl13, ifl14, ifl15, ifl16, ifl17, ifl18, ifl19, ifl20, ifl21, ifl22, ifl23, ifl24};
        level2Image = new ImageView[]{lev2_1, lev2_2};
        level3Image = new ImageView[]{lev3_1, lev3_2, lev3_3};
        slots = new ImageView[][]{{slot1_1, slot1_2, slot1_3},{slot2_1, slot2_2, slot2_3},{slot3_1, slot3_2, slot3_3}};
    }

    public static PersonalBoardController getPersonalBoardController() {
        return personalBoardController;
    }

    public void showStrongBox() {
        strongBoxGrid.setOpacity(1);
    }

    public void hideStrongBox() {
        strongBoxGrid.setOpacity(0);
    }

    @Override
    public void update() {
        Platform.runLater(() -> updatePersonalBoard(guiView.playerNumber));
    }

    public void update(int player) {
        updatePersonalBoard(player);
    }
}
