package it.polimi.ingsw.view.GUI.Controllers.Board;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import it.polimi.ingsw.model.board.general.Market;
import it.polimi.ingsw.model.board.personal.storage.WarehouseDepots;
import it.polimi.ingsw.model.resources.ResourceTypes;
import it.polimi.ingsw.model.resources.Resources;
import it.polimi.ingsw.view.GUI.Controllers.GenericController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;


public class MarketController extends GenericController implements Initializable {
    private static MarketController marketController;


    @FXML
    private Button bTake, bPlace, bConfirmSwap;

    @FXML
    private Label goldLabel, servantLabel, shieldLabel, stoneLabel, lYouHaveNow;

    Label[] resLabels;
    ResourceTypes[] resourceTypes;

    @FXML // fx:id="marketGrid"
    private GridPane marketGrid; // Value injected by FXMLLoader

    @FXML //fx:id="externalResource"
    private ImageView externalResource;

    @FXML
    private ImageView ig0_0,ig0_1, ig0_2, ig0_3, ig1_0, ig1_1, ig1_2, ig1_3, ig2_0, ig2_1, ig2_2, ig2_3;

    @FXML
    private ImageView iav1, iav2, iav3, iav4, iah1, iah2, iah3; //i= image, a= arrow, v= vertical, h= horizontal
    private ImageView[] arrows;

    @FXML
    private RadioButton rb1_1, rb1_2, rb1_3, rb2_1, rb2_2, rb2_3; //r= radio, b= button, 1_2 means in the first VBOX the second radioButton

    private RadioButton[] radioButtons;

    int column = -1, row = -1, arrowsSelected = 0;

    Resources resourcesFromMarket;
    int level, tmp, numResOccupied, numResToAdd, numResToMove;
    ResourceTypes resourceTypesToMove;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        marketController = this;

        assert marketGrid != null : "fx:id=\"marketGrid\" was not injected: check your FXML file 'Market.fxml'.";

        ToggleGroup toggleGroup = new ToggleGroup();

        radioButtons = new RadioButton[]{rb1_1, rb1_2, rb1_3, rb2_1, rb2_2, rb2_3};
        for (int i = 0; i < 6; i++) {
            radioButtons[i].setToggleGroup(toggleGroup);
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

        for(int i=0; i < market.ROWS; i++)
        {
            for(int j=0; j < market.COLUMNS; j++)
            {
                gridMarbles[i][j].setImage(new Image(fromResourceToMarbleImage(marketMatrix[i][j])));
            }
        }

        externalResource.setImage(new Image(fromResourceToMarbleImage(market.getExternalResource())));
    }


    /**
     * Method that associates a resource type to a marbleImage
     * @param resourceTypes the resource type that has to be showed
     * @return the image associated at the resourceType
     */
    private String fromResourceToMarbleImage(ResourceTypes resourceTypes)
    {
        String imageName = "images/Marble/";
        switch (resourceTypes)
        {
            case GOLD:
                return imageName + "yellow marble.png";

            case SERVANT:
                return imageName + "purple marble.png";

            case SHIELD:
                return imageName + "blue marble.png";

            case STONE:
                return imageName + "gray marble.png";

            case FAITH:
                return imageName + "red marble.png";
        }
        return imageName + "white marble.png";
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

    private void showSwapArea() {

        setAbleButtons();
        WarehouseDepots warehouseDepots = guiView.game.getPlayerTurn(guiView.playerNumber).getPlayer().getPersonalBoard().getDeposit().getWarehouseDepots();
        resourcesFromMarket = warehouseDepots.getSwapDeposit();

        lYouHaveNow.setOpacity(1);
        for (int i = 0; i < 4; i++) {
            resLabels[i].setText(String.valueOf(resourcesFromMarket.getResourceNumber(resourceTypes[i])));
        }
    }


/*

    public void wrong() {
        while(!(game.getPlayerTurn(playerNumber).getPlayer().getPersonalBoard().getDeposit().getWarehouseDepots().getSwapDeposit().getTotalResourceNumber() ==0 || exit)) {
            System.out.println("1) Place resources");
            System.out.println("2) Take resources");
            System.out.println("0) Exit");

            switch (currentAction) {
                case 1:
                    do {
                        level = cliSupporter.integerInput("On which level? (1-" + warehouseDepots.getNumberLevels() + "): ", 0, warehouseDepots.getNumberLevels()) - 1;
                        if (level == -1)
                            break;
                        if (warehouseDepots.getResourcesNumber(level) > level || level > 2 && warehouseDepots.getResourcesNumber(level) > 1)
                            System.out.println("\nYou selected a full level, please select another one or 0 to quit");
                    }
                    while (warehouseDepots.getResourcesNumber(level) > level || level > 2 && warehouseDepots.getResourcesNumber(level) > 1);

                    if (level == -1)
                        break;

                    if (warehouseDepots.getResourcesNumber(level) == 0 && !warehouseDepots.getLevel(level).getFixedResource()) {
                        do {
                            tmp = cliSupporter.integerInput("Which type of resource?\n1) " + ColoredResources.GOLD + "\n2) " + ColoredResources.SERVANT + "\n3) " + ColoredResources.SHIELD + "\n4) " + ColoredResources.STONE + "\n", 1, 4);

                            resourceTypesToMove = cliSupporter.numberToResourceType(tmp);
                            if(resourcesFromMarket.getResourceNumber(resourceTypesToMove) == 0)
                                cliSupporter.wrongInput();
                        }while(resourcesFromMarket.getResourceNumber(resourceTypesToMove) == 0);
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

                        try{
                            swapMoveToLevel(level,resourceTypesToMove,numResToMove);
                        }catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                        if (waitAndGetResponse() == ERROR){
                            System.out.println(waitAndGetResponse());//probably player tries to put res with different type in the same level
                            break;
                        }

                        cliSupporter.resourceMovedCorrectly();
                        break;
                    }
                    //
                case 2:
                    do {
                        level = cliSupporter.integerInput("From which level? (1-" + warehouseDepots.getNumberLevels() + "): ", 0, warehouseDepots.getNumberLevels()) - 1;
                        if(level == -1)
                            break;
                        if (warehouseDepots.getResourcesNumber(level) == 0)
                            System.out.println("\nYou selected an empty level, please select another one or 0 to quit");
                    }
                    while (warehouseDepots.getResourcesNumber(level) == 0);

                    if (level == -1)
                        break;

                    try{
                        warehouseDepots.moveToSwap(1);
                        swapMoveToSwap(level);
                    }catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    if (waitAndGetResponse() == ERROR) {
                        System.out.println(waitAndGetResponse());
                        break;
                    }

                    cliSupporter.resourceMovedCorrectly();
                    break;

            }
            waitForUpdatedGame();
        }
    }

 */



    private void setAbleButtons(){
        for (RadioButton button: radioButtons) {
            button.setDisable(false);
            button.setOpacity(1);
        }
        bTake.setDisable(false);
        bPlace.setDisable(false);
        bConfirmSwap.setDisable(false);
        bTake.setOpacity(1);
        bPlace.setOpacity(1);
        bConfirmSwap.setOpacity(1);

    }

    public void confirmSwap(MouseEvent mouseEvent){
        boolean exit = false;
        //TODO ask if player is sure, he can lose resources (alert/popup?)

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
            for (RadioButton button: radioButtons) {
                button.setDisable(true);
                button.setOpacity(0);
            }
            bTake.setDisable(true);
            bPlace.setDisable(true);
            bConfirmSwap.setDisable(true);
            bTake.setOpacity(0);
            bPlace.setOpacity(0);
            bConfirmSwap.setOpacity(0);
        }
    }

    public void confirmRowColumn(ActionEvent actionEvent) {
        if (arrowsSelected == 1){
            if (row != -1){
                try {
                    guiView.marketBuyRow(row, -1); //TODO insert correct index of leaderCard with white marble effect instead of 1
                    guiView.isSuccessReceived();
                    guiView.waitForUpdatedGame();
                    disableMarket();
                    showSwapArea();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if (column != -1){
                try {
                    guiView.marketBuyColumn(column, -1); //TODO insert correct index of leaderCard with white marble effect instead of 1
                    guiView.isSuccessReceived();
                    guiView.waitForUpdatedGame();
                    disableMarket();
                    showSwapArea();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void disableMarket(){
        for (ImageView image: arrows) {
            image.setDisable(true);
        }
    }

    public void takeResources(ActionEvent actionEvent) {
        //TODO update warehouse in PersonalBoard

    }

    public void placeResources(ActionEvent actionEvent) {
        //TODO update warehouse in PersonalBoard

    }

    @Override
    public void update() {
        updateMarketMatrix();
    }
}



