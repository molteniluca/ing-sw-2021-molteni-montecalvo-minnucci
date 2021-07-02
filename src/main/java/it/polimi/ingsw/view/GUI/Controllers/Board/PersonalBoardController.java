package it.polimi.ingsw.view.GUI.Controllers.Board;

import it.polimi.ingsw.model.board.personal.storage.WarehouseDepots;
import it.polimi.ingsw.model.cards.DevelopmentCard;
import it.polimi.ingsw.model.resources.ResourceTypes;
import it.polimi.ingsw.model.resources.Resources;
import it.polimi.ingsw.view.GUI.Controllers.GenericController;
import javafx.animation.RotateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Point3D;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.ArrayList;

public class PersonalBoardController extends GenericController {
    private int currentFaithPosition, currentLorenzoFaithPosition;
    private final int[] slotPosition = new int[3];
    private static PersonalBoardController personalBoardController;

    @FXML
    public ImageView personalBoardImage;

    //initializes the three pope cards front and back
    private static final Image popeFavor1Front = new Image("images/FaithTrack/pope_favor1_front.png");
    private static final Image popeFavor2Front = new Image("images/FaithTrack/pope_favor2_front.png");
    private static final Image popeFavor3Front = new Image("images/FaithTrack/pope_favor3_front.png");

    private static final Image personalBoardImageLoaded = new Image("/images/PersonalBoard.jpg");

    private static final Image[] popeFavorFront = new Image[]{popeFavor1Front, popeFavor2Front, popeFavor3Front}; //array used to better handle the cards


    private static final Image popeFavor1Back = new Image("images/FaithTrack/pope_favor1_back.png");
    private static final Image popeFavor2Back = new Image("images/FaithTrack/pope_favor2_back.png");
    private static final Image popeFavor3Back = new Image("images/FaithTrack/pope_favor3_back.png");

    private static final Image[] popeFavorBack = new Image[]{popeFavor1Back, popeFavor2Back, popeFavor3Back};

    @FXML
    private GridPane availableResourcesProduction;
    @FXML
    private Label stoneLabelProduction, shieldLabelProduction, servantLabelProduction, goldLabelProduction;
    @FXML
    private Label playerName;
    @FXML //i = image, f = faith
    private ImageView if0, if1, if2, if3, if4, if5, if6, if7, if8, if9, if10, if11, if12, if13, if14, if15, if16, if17, if18, if19, if20, if21, if22, if23, if24;
    @FXML //i = image, f = faith, l = Lorenzo
    private ImageView ifl0, ifl1, ifl2, ifl3, ifl4, ifl5, ifl6, ifl7, ifl8, ifl9, ifl10, ifl11, ifl12, ifl13, ifl14, ifl15, ifl16, ifl17, ifl18, ifl19, ifl20, ifl21, ifl22, ifl23, ifl24;

    private ImageView[] faithImagePosition, faithLorenzoImagePosition;

    @FXML
    private GridPane strongBoxGrid;

    @FXML
    private Label goldLabel, servantLabel, shieldLabel, stoneLabel;

    @FXML
    private ImageView lev1, lev2_1, lev2_2, lev3_1, lev3_2, lev3_3, lev4_1, lev4_2, lev5_1, lev5_2;

    private ImageView[] level2Image, level3Image, level4Image, level5Image; //warehouse

    @FXML
    private ImageView slot1_1, slot1_2, slot1_3, slot2_1, slot2_2, slot2_3, slot3_1, slot3_2, slot3_3; //cardBoard slots

    private ImageView[][] slots;

    @FXML
    private ImageView popeFavor1, popeFavor2, popeFavor3; //the 3 popeCards in the faith track

    private ImageView[] popeFavor; //an array of the 3 popeCards

    @FXML
    void initialize(){
        personalBoardImage.setImage(personalBoardImageLoaded);
        personalBoardController=this;
        popeFavor = new ImageView[]{popeFavor1, popeFavor2, popeFavor3};
        faithImagePosition = new ImageView[]{if0, if1, if2, if3, if4, if5, if6, if7, if8, if9, if10, if11, if12, if13, if14, if15, if16, if17, if18, if19, if20, if21, if22, if23, if24};
        faithLorenzoImagePosition = new ImageView[]{ifl0, ifl1, ifl2, ifl3, ifl4, ifl5, ifl6, ifl7, ifl8, ifl9, ifl10, ifl11, ifl12, ifl13, ifl14, ifl15, ifl16, ifl17, ifl18, ifl19, ifl20, ifl21, ifl22, ifl23, ifl24};
        level2Image = new ImageView[]{lev2_1, lev2_2};
        level4Image = new ImageView[]{lev4_1, lev4_2};
        level5Image = new ImageView[]{lev5_1, lev5_2};
        level3Image = new ImageView[]{lev3_1, lev3_2, lev3_3};
        slots = new ImageView[][]{{slot1_1, slot1_2, slot1_3},{slot2_1, slot2_2, slot2_3},{slot3_1, slot3_2, slot3_3}};
    }

    /**
     * Update method for faithTrack, for single player and multiplayer
     * @param player number of player's faith track
     */
    @FXML
    private void updateFaithTrack(int player){
        //sets the position of the player in the faith track
        faithImagePosition[currentFaithPosition].setVisible(false);
        currentFaithPosition = guiView.game.getPlayerTurn(player).getPlayer().getPersonalBoard().getFaithTrack().getPosition();
        faithImagePosition[currentFaithPosition].setVisible(true);

        //sets Lorenzo's position in the faith track
        if(guiView.game.getNumPlayers()==1){
            faithLorenzoImagePosition[currentLorenzoFaithPosition].setVisible(false);
            currentLorenzoFaithPosition = guiView.game.getSelfPLayingTurn().getLorenzo().getFaithTrack().getPosition();
            faithLorenzoImagePosition[currentLorenzoFaithPosition].setVisible(true);
        }


        //updates the popeFavor cards
        int[] faithCardsPosition = guiView.game.getPlayerTurn(player).getPlayer().getPersonalBoard().getFaithTrack().getFaithCards();


        for (int i = 0; i < 3 ; i++)
        {
            switch(faithCardsPosition[i])
            {
                case 0: //card upside down
                    popeFavor[i].setImage(popeFavorBack[i]);
                    break;
                case 1: //card active
                    RotateTransition rotate = new RotateTransition(Duration.seconds(2), popeFavor[i]);
                    rotate.setCycleCount(1);
                    rotate.setToAngle(360);
                    rotate.setAxis(new Point3D(0,1,0));
                    rotate.play();
                    int intero = i;
                    rotate.setOnFinished( (e) -> popeFavor[intero].setImage(popeFavorFront[intero]) );
                    break;
                case 2: //card discarded
                    RotateTransition rotate2 = new RotateTransition(Duration.seconds(2), popeFavor[i]);
                    rotate2.setCycleCount(1);
                    rotate2.setToAngle(360);
                    rotate2.setAxis(new Point3D(0,1,0));
                    rotate2.setNode(popeFavor[i]);
                    rotate2.play();
                    int intero2 = i;
                    rotate2.setOnFinished( (e) -> popeFavor[intero2].setImage(null) );
                    break;
            }
        }
    }

    /**
     * Update method for strongbox
     * @param player number of player's strongbox to show
     */
    @FXML
    private void updateStrongBox(int player){
        Resources resStrongBox = guiView.game.getPlayerTurn(player).getPlayer().getPersonalBoard().getDeposit().getStrongBox().getResources();
        goldLabel.setText(String.valueOf(resStrongBox.getResourceNumber(ResourceTypes.GOLD)));
        servantLabel.setText(String.valueOf(resStrongBox.getResourceNumber(ResourceTypes.SERVANT)));
        shieldLabel.setText(String.valueOf(resStrongBox.getResourceNumber(ResourceTypes.SHIELD)));
        stoneLabel.setText(String.valueOf(resStrongBox.getResourceNumber(ResourceTypes.STONE)));
    }

    /**
     * Update method for warehouse
     * @param player number of player's warehouse to show
     */
    @FXML
    private void updateWarehouse(int player){
        WarehouseDepots warehouseDepots = guiView.game.getPlayerTurn(player).getPlayer().getPersonalBoard().getDeposit().getWarehouseDepots();
        //for every levels which current player has, "overwrite" levels with current player resources
        for(int i = 0; i< warehouseDepots.getNumberLevels(); i++){
            ResourceTypes resourceTypes = warehouseDepots.getResourceTypeLevel(i);
            int maxResources = warehouseDepots.getLevel(i).getMaxResourceNumber();
            String imageName = null;
            if (resourceTypes != null) {
                imageName = "images/resources/" + resourceTypes.toString().toLowerCase() + ".png";

            }
            changeLevelResources(imageName, warehouseDepots.getResourcesNumber(i), maxResources, i);
        }
        //for every levels which current player does not have, clear them
        for(int i = warehouseDepots.getNumberLevels(); i< 5; i++){
            changeLevelResources(null, 0, 2, i);
        }
    }

    /**
     * Method that changes resources in levels or clears levels,
     * depending on which player board you are watching
     * @param imageName String name of image
     * @param numberOfResources number of resources that player has in that level
     * @param maxNumberOfResources max number of resources that level has
     * @param level level to modify
     */
    private void changeLevelResources(String imageName, int numberOfResources, int maxNumberOfResources, int level){
        Image image;
        if(imageName == null)
            image = null;
        else
            image = new Image(imageName);

        for(int j = 0; j<maxNumberOfResources; j++){
            switch (level){
                case 0:
                    if (j >= numberOfResources)
                        lev1.setImage(null);
                    else
                        lev1.setImage(image);
                    break;
                case 1:
                    if (j >= numberOfResources)
                        level2Image[j].setImage(null);
                    else
                        level2Image[j].setImage(image);
                    break;
                case 2:
                    if (j >= numberOfResources)
                        level3Image[j].setImage(null);
                    else
                        level3Image[j].setImage(image);
                    break;
                case 3:
                    if (j >= numberOfResources)
                        level4Image[j].setImage(null);
                    else
                        level4Image[j].setImage(image);
                    break;
                case 4:
                    if (j >= numberOfResources)
                        level5Image[j].setImage(null);
                    else
                        level5Image[j].setImage(image);
                    break;
            }
        }
    }

    /**
     * Update method for production cards
     * @param player number of player's production cards to show
     */
    @FXML
    private void updateProductionCards(int player){
        ArrayList<DevelopmentCard>[] developmentCard = guiView.game.getPlayerTurn(player).getPlayer().getPersonalBoard().getCardBoard().getDevelopmentCardsMatrix();

        for(int k=0;k<3;k++){
            for (int i = 0; i < 3; i++) {
                if (developmentCard[i].size() > k) {
                    updateSlotImage(k,i, developmentCard[i].get(k));
                }else{
                    updateSlotImage(k,i,null);
                }
            }
        }

        Resources res = guiView.game.getPlayerTurn(player).getPlayer().getPersonalBoard().getAvailableResources();
        if(res==null){
            availableResourcesProduction.setVisible(false);
        }else{
            availableResourcesProduction.setVisible(true);
            goldLabelProduction.setText(Integer.toString(res.getResourceNumber(ResourceTypes.GOLD)));
            stoneLabelProduction.setText(Integer.toString(res.getResourceNumber(ResourceTypes.STONE)));
            servantLabelProduction.setText(Integer.toString(res.getResourceNumber(ResourceTypes.SERVANT)));
            shieldLabelProduction.setText(Integer.toString(res.getResourceNumber(ResourceTypes.SHIELD)));
        }
    }

    /**
     * Method that update personal board view setting images of production cards
     * @param level level 1, 2 or 3 to set the image of card
     * @param slot slot 1, 2 or 3 to set the image of card
     * @param developmentCard DevelopmentCard to convert in an image
     */
    private void updateSlotImage(int level,int slot, DevelopmentCard developmentCard){
        if(developmentCard!=null) {
            String nameImage = developmentCard.getImage();
            slots[slot][level].setImage(new Image("images/Cards/DevelopmentCards/" +nameImage+"-1.png"));
        }else{
            slots[slot][level].setImage(null);
        }
    }

    /**
     * Getter of personalBoardController to manage it from GameBoardController
     * @return the personalBoardController
     */
    public static PersonalBoardController getPersonalBoardController() {
        return personalBoardController;
    }

    /**
     * Method that shows strongbox when player's mouse enters the strongbox
     */
    public void showStrongBox() {
        strongBoxGrid.setOpacity(1);
    }

    /**
     * Method that hideStrongBox when player's mouse exits from strongbox
     */
    public void hideStrongBox() {
        strongBoxGrid.setOpacity(0);
    }

    /**
     * Generic update for all personal board, it recalls all update methods
     * @param player player whose board you want to see
     */
    void updatePersonalBoard(int player){
        updateFaithTrack(player);
        updateStrongBox(player);
        updateWarehouse(player);
        updateProductionCards(player);
        playerName.setText(guiView.game.getPlayerTurn(player).getPlayer().getName()+" | "+ (player + 1) +"° Player");
    }

    /**
     * Method that is invoked when client received an updated game and update the entire personal board view
     */
    @Override
    public void update() {
        Platform.runLater(() -> updatePersonalBoard(guiView.playerNumber));
    }

    /**
     * Method that update personal boards when "showing other player" button is pressed
     * @param player player whose personal board you want to see
     */
    public void update(int player) {
        updatePersonalBoard(player);
    }
}
