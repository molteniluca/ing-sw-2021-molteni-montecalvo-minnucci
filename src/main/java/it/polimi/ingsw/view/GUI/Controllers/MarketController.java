package it.polimi.ingsw.view.GUI.Controllers;
import java.net.URL;
import java.util.ResourceBundle;

import it.polimi.ingsw.model.board.general.Market;
import it.polimi.ingsw.model.resources.ResourceTypes;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;


public class MarketController extends GenericController implements Initializable {

    @FXML // fx:id="backHomeButton"
    private Button backHomeButton; // Value injected by FXMLLoader

    @FXML // fx:id="exitGameButton"
    private Button exitGameButton; // Value injected by FXMLLoader

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

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

    int column = -1, row = -1;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        assert marketGrid != null : "fx:id=\"marketGrid\" was not injected: check your FXML file 'Market.fxml'.";

        System.out.println(guiView.playerNumber);

        ToggleGroup toggleGroup = new ToggleGroup();

        radioButtons = new RadioButton[]{rb1_1, rb1_2, rb1_3, rb2_1, rb2_2, rb2_3};
        for (int i = 0; i < 6; i++) {
            radioButtons[i].setToggleGroup(toggleGroup);
        }

        arrows = new ImageView[]{iav1, iav2, iav3, iav4, iah1, iah2, iah3};

        Platform.runLater(() -> {
            updateMarketMatrix();
        });
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
        }
        else {
            tempImageView.setOpacity(0);
            if (tempImageView.getId().charAt(2) == 'h')
                row = -1;
            else
                column = -1;
        }
        //TODO Francesco, why columns don't change their layout?
    }

    private ImageView stringIdToImageView(String string){
        ImageView image = null;
        for (ImageView iArrows: arrows) {
            if (iArrows.getId().equals(string))
                image = iArrows;
        }
        return image;
    }
}



