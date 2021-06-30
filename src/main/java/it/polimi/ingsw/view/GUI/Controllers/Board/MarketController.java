package it.polimi.ingsw.view.GUI.Controllers.Board;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import it.polimi.ingsw.model.board.general.Market;
import it.polimi.ingsw.model.board.personal.storage.WarehouseDepots;
import it.polimi.ingsw.model.cards.specialAbility.ExtraResource;
import it.polimi.ingsw.model.resources.ResourceTypes;
import it.polimi.ingsw.model.resources.Resources;
import it.polimi.ingsw.view.GUI.Controllers.GenericController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;


public class MarketController extends GenericController implements Initializable {
    private static MarketController marketController;
    private static Image marketImage = new Image("/images/marketBoard.png");
    private static Image shield = new Image("/images/Marble/blue marble.png");
    private static Image gold = new Image("/images/Marble/yellow marble.png");
    private static Image servant = new Image("/images/Marble/purple marble.png");
    private static Image stone = new Image("/images/Marble/gray marble.png");
    private static Image faith = new Image("/images/Marble/red marble.png");
    private static Image blank = new Image("/images/Marble/white marble.png");

    @FXML
    private ComboBox extraEffectComboBox;
    @FXML
    private ImageView marketImageView;
    @FXML
    private Button bTake, bPlace, bConfirmSwap;
    @FXML
    private Label goldLabel, servantLabel, shieldLabel, stoneLabel, lYouHaveNow, extraEffectLabel;

    Label[] resLabels;
    ResourceTypes[] resourceTypes;

    @FXML // fx:id="marketGrid"
    private GridPane marketGrid, gridSwapArea; // Value injected by FXMLLoader
    @FXML //fx:id="externalResource"
    private ImageView externalResource;
    @FXML
    private ImageView ig0_0,ig0_1, ig0_2, ig0_3, ig1_0, ig1_1, ig1_2, ig1_3, ig2_0, ig2_1, ig2_2, ig2_3;
    @FXML
    private ImageView iav1, iav2, iav3, iav4, iah1, iah2, iah3; //i= image, a= arrow, v= vertical, h= horizontal
    private ImageView[] arrows;
    @FXML
    private RadioButton rb1_1, rb1_2, rb1_3, rb2_1, rb2_2, rb2_3; //r= radio, b= button, 1_2 means in the first VBOX the second radioButton
    @FXML
    private RadioButton rbGold, rbServant, rbShield, rbStone;
    @FXML
    public Rectangle rMarket, rMarketTotal;

    private RadioButton[] levelRadioButtons;
    private RadioButton[] resourceRadioButtons;

    int column = -1, row = -1, arrowsSelected = 0;

    ToggleGroup levelToggleGroup;
    ToggleGroup resourceToggleGroup;

    Resources resourcesFromMarket;
    int  numResOccupied, numResToAdd, numResToMove;

    ArrayList<ExtraResource> effects;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        marketImageView.setImage(marketImage);

        marketController = this;

        assert marketGrid != null : "fx:id=\"marketGrid\" was not injected: check your FXML file 'Market.fxml'.";

        levelToggleGroup = new ToggleGroup();

        levelRadioButtons = new RadioButton[]{rb1_1, rb1_2, rb1_3, rb2_1, rb2_2, rb2_3};
        for (int i = 0; i < 6; i++) {
            levelRadioButtons[i].setToggleGroup(levelToggleGroup);
        }

        resourceToggleGroup = new ToggleGroup();
        resourceRadioButtons = new RadioButton[] {rbGold, rbServant, rbShield, rbStone};
        for(int i = 0; i < 4; i++ ) {
            resourceRadioButtons[i].setToggleGroup(resourceToggleGroup);
        }

        arrows = new ImageView[]{iav1, iav2, iav3, iav4, iah1, iah2, iah3};
        resLabels = new Label[]{goldLabel, servantLabel, shieldLabel, stoneLabel};
        resourceTypes = new ResourceTypes[]{ResourceTypes.GOLD, ResourceTypes.SERVANT, ResourceTypes.SHIELD, ResourceTypes.STONE};
        Platform.runLater(this::updateMarketMatrix);

    }

    private void updateMarketMatrix() {
        Market market = guiView.game.getPlayerTurn(guiView.playerNumber).getPlayer().getPersonalBoard().getGeneralBoard().getMarket();
        ResourceTypes[][] marketMatrix = market.getMarketMatrix();

        ImageView[][] gridMarbles;
        gridMarbles = new ImageView[][]{{ig0_0, ig0_1, ig0_2, ig0_3},{ ig1_0, ig1_1, ig1_2, ig1_3},{ ig2_0, ig2_1, ig2_2, ig2_3}};

        //enable arrow images again
        for (ImageView arrow : arrows) {
            arrow.setDisable(false);
        }

        for(int i=0; i < market.ROWS; i++)
        {
            for(int j=0; j < market.COLUMNS; j++)
            {
                gridMarbles[i][j].setImage(fromResourceToMarbleImage(marketMatrix[i][j]));
            }
        }

        externalResource.setImage(fromResourceToMarbleImage(market.getExternalResource()));
    }

    public void chooseRowColumn(MouseEvent mouseEvent) {
        String tempString = mouseEvent.getPickResult().getIntersectedNode().getId();
        ImageView tempImageView = stringIdToImageView(tempString);
        if (tempImageView.getOpacity()!=1) {
            tempImageView.setOpacity(1);
            if (tempImageView.getId().charAt(2) == 'h')
                row = (int) tempImageView.getId().charAt(3) - 49;
            else
                column = (int) tempImageView.getId().charAt(3) -49;
            arrowsSelected++;
        }
        else {
            tempImageView.setOpacity(0);
            if (tempImageView.getId().charAt(2) == 'h')
                row = -1;
            else
                column = -1;
            arrowsSelected--;
        }
    }

    /**
     * Shows the swap area after buying from market
     * also enables all the necessary buttons setting their opacity to one
     */
    private void showSwapArea() {
        setAbleButtons();
        lYouHaveNow.setOpacity(1);
    }

    private void updateSwap(){
        WarehouseDepots warehouseDepots = guiView.game.getPlayerTurn(guiView.playerNumber).getPlayer().getPersonalBoard().getDeposit().getWarehouseDepots();
        resourcesFromMarket = warehouseDepots.getSwapDeposit();
        for (int i = 0; i < 4; i++) {
            resLabels[i].setText(String.valueOf(resourcesFromMarket.getResourceNumber(resourceTypes[i])));
        }

        if (resourcesFromMarket.getTotalResourceNumber() == 0 && bTake.getOpacity() != 0){ //only one control on the opacity is necessary
            bTake.setDisable(true);
            bPlace.setDisable(true);
            bTake.setOpacity(0.5);
            bPlace.setOpacity(0.5);
        }
    }

    public void confirmRowColumn() {
        if (arrowsSelected == 1){
            if (row != -1){
                try {
                    int selectedEffect=-1;
                    for(int i=0; i< effects.size();i++){
                        if(extraEffectComboBox.getValue()==effects.get(i).getResource()){
                            selectedEffect=i;
                        }
                    }
                    guiView.marketBuyRow(row, selectedEffect);
                    guiView.isSuccessReceived();
                    disableMarket();
                    showSwapArea();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if (column != -1){
                try {
                    int selectedEffect=-1;
                    for(int i=0; i< effects.size();i++){
                        if(extraEffectComboBox.getValue()==effects.get(i).getResource()){
                            selectedEffect=i;
                        }
                    }
                    guiView.marketBuyColumn(column, selectedEffect);guiView.isSuccessReceived();
                    guiView.isSuccessReceived();
                    disableMarket();
                    showSwapArea();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void takeResources() {
        int level;
        WarehouseDepots warehouseDepots = guiView.game.getPlayerTurn(guiView.playerNumber).getPlayer().getPersonalBoard().getDeposit().getWarehouseDepots();
        RadioButton selectedRadioButton = (RadioButton) levelToggleGroup.getSelectedToggle();
        String selectedRadioButtonString = selectedRadioButton.getText();
        level = selectedRadioButtonString.charAt(selectedRadioButtonString.length()-1) -49;


        if (warehouseDepots.getResourcesNumber(level) == 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("Illegal action");
            alert.setContentText("You selected an empty level, please select another one");

            alert.showAndWait();
        }
        try {
            guiView.swapMoveToSwap(level);
            guiView.isSuccessReceived();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void placeResources() {
        int level;
        ResourceTypes resourceTypesToMove = null; //resource type obtained from the resource selected in resourceToggleGroup
        WarehouseDepots warehouseDepots = guiView.game.getPlayerTurn(guiView.playerNumber).getPlayer().getPersonalBoard().getDeposit().getWarehouseDepots();
        RadioButton selectedRadioButton = (RadioButton) levelToggleGroup.getSelectedToggle();
        String selectedRadioButtonString = selectedRadioButton.getText();
        level = selectedRadioButtonString.charAt(selectedRadioButtonString.length()-1) -49;

        if (warehouseDepots.getResourcesNumber(level) > level || level > 2 && warehouseDepots.getResourcesNumber(level) > 1) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("Illegal action");
            alert.setContentText("You selected a full level, please select another one");

            alert.showAndWait();
        }

        if (warehouseDepots.getResourcesNumber(level) == 0 && !warehouseDepots.getLevel(level).getFixedResource()) {


            RadioButton selectedResourceRadioButton = (RadioButton) resourceToggleGroup.getSelectedToggle();
            String selectedResourceRadioButtonString = selectedResourceRadioButton.getText();

            switch (selectedResourceRadioButtonString) {
                case "Gold":
                    resourceTypesToMove = ResourceTypes.GOLD;
                    break;
                case "Servant":
                    resourceTypesToMove = ResourceTypes.SERVANT;
                    break;
                case "Shield":
                    resourceTypesToMove = ResourceTypes.SHIELD;
                    break;
                case "Stone":
                    resourceTypesToMove = ResourceTypes.STONE;
                    break;
            }

        } else
            resourceTypesToMove = warehouseDepots.getResourceTypeLevel(level);


        if (warehouseDepots.getResourcesNumber(level) == 0 || warehouseDepots.getResourceTypeLevel(level).equals(resourceTypesToMove)) {
            numResOccupied = warehouseDepots.getResourcesNumber(level);

            if (0 <= level && level < 3)
                numResToAdd = level + 1 - numResOccupied;
            else
                numResToAdd = 2 - numResOccupied;

            if (resourcesFromMarket.getResourceNumber(resourceTypesToMove) < numResToAdd)
                numResToMove = resourcesFromMarket.getResourceNumber(resourceTypesToMove);
            else
                numResToMove = numResToAdd;

            try {
                guiView.swapMoveToLevel(level, resourceTypesToMove, numResToMove);
                guiView.isSuccessReceived();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void confirmSwap(){
        boolean exit = false;

        if(resourcesFromMarket.getTotalResourceNumber() > 0) {
            try {
                guiView.swapDropResources();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(guiView.isSuccessReceived()){
                exit = true;
            }
        }
        if (resourcesFromMarket.getTotalResourceNumber() == 0 || exit){
            //Before exit from market and swap
            for (RadioButton button: levelRadioButtons) {
                button.setDisable(true);
                button.setOpacity(0);
            }

            for (RadioButton button: resourceRadioButtons) {
                button.setDisable(true);
                button.setOpacity(0);
            }

            gridSwapArea.setOpacity(0);
            bTake.setDisable(true);
            bPlace.setDisable(true);
            bConfirmSwap.setDisable(true);
            bTake.setOpacity(0);
            bPlace.setOpacity(0);
            bConfirmSwap.setOpacity(0);
            lYouHaveNow.setOpacity(0);
        }
    }

    /**
     * Method that associates a resource type to a marbleImage
     * @param resourceTypes the resource type that has to be showed
     * @return the image associated at the resourceType
     */
    private Image fromResourceToMarbleImage(ResourceTypes resourceTypes)
    {
        switch (resourceTypes)
        {
            case GOLD:
                return gold;

            case SERVANT:
                return servant;

            case SHIELD:
                return shield;

            case STONE:
                return stone;

            case FAITH:
                return faith;
        }
        return blank;
    }

    private ImageView stringIdToImageView(String string){
        ImageView image = null;
        for (ImageView iArrows: arrows) {
            if (iArrows.getId().equals(string))
                image = iArrows;
        }
        return image;
    }

    public static MarketController getMarketController() {
        return marketController;
    }

    /**
     * Enables the buttons setting the disable
     */
    private void setAbleButtons(){
        for (RadioButton button: levelRadioButtons) {
            button.setDisable(false);
            button.setOpacity(1);
        }

        for (RadioButton button: resourceRadioButtons) {
            button.setDisable(false);
            button.setOpacity(1);
        }

        gridSwapArea.setOpacity(1);
        bTake.setDisable(false);
        bPlace.setDisable(false);
        bConfirmSwap.setDisable(false);
        bTake.setOpacity(1);
        bPlace.setOpacity(1);
        bConfirmSwap.setOpacity(1);

    }

    private void disableMarket(){
        for (ImageView image: arrows) {
            image.setDisable(true);
        }
    }

    public void setClickable(boolean set){
        rMarketTotal.setVisible(!set);
    }

    @Override
    public void update() {
        Platform.runLater( () -> {
            updateMarketMatrix();
            updateSwap();
            effects = guiView.game.getPlayerTurn(guiView.playerNumber).getPlayer().getPersonalBoard().getLeaderBoard().getExtraResource();
            if(effects.size()>0) {
                extraEffectComboBox.setVisible(true);
                extraEffectLabel.setVisible(true);
                for (ExtraResource effect : effects) {
                    extraEffectComboBox.getItems().remove(effect.getResource());
                    extraEffectComboBox.getItems().add(effect.getResource());
                    extraEffectComboBox.setValue(effect.getResource());
                }
            }else{
                extraEffectComboBox.setVisible(false);
                extraEffectLabel.setVisible(false);
            }
        }
        );
    }

}



