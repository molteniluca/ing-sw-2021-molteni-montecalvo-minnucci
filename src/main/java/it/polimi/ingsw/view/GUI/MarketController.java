/**
 * Sample Skeleton for 'Market.fxml' Controller Class
 */

package it.polimi.ingsw.view.GUI;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.model.board.general.Market;
import it.polimi.ingsw.model.resources.ResourceTypes;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;


public class MarketController extends GUI implements Initializable {


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

    //initializes the images of the marbles
    private Image blueMarble = new Image("images/Marble/blue marble.png");
    private Image grayMarble = new Image("images/gray Marble.png");
    private Image purpleMarble = new Image("images/purple Marble.png");
    private Image redMarble = new Image("images/red Marble.png");
    private Image whiteMarble = new Image("images/white Marble.png");
    private Image yellowMarble = new Image("images/yellow Marble.png");


    @FXML
    public void backHome(ActionEvent actionEvent) throws IOException {
        Parent marketViewParent = FXMLLoader.load(ClassLoader.getSystemResource("FXML/AskCreateOrJoin.fxml"));

        Scene homeScene = new Scene(marketViewParent);

        //gets the stage information
        Stage primaryStage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();

        //marketStage.setTitle("");
        primaryStage.setScene(homeScene);
        //primaryStage.setFullScreen(true);
        primaryStage.sizeToScene();

        primaryStage.show();
    }



    @FXML
    public void exitGame(ActionEvent event) {
        Platform.exit();
        System.exit(0);
    }

/*
    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {

        assert marketGrid != null : "fx:id=\"marketGrid\" was not injected: check your FXML file 'Market.fxml'.";

        System.out.println(playerNumber);


        Platform.runLater(() -> {
            Market market = new Market();//game.getTurn(playerNumber).getPlayer().getPersonalBoard().getGeneralBoard().getMarket();
            ResourceTypes[][] marketMatrix = market.getMarketMatrix();

            //access to the gridPane in the scene
            //marketGrid = (GridPane) marketScene.lookup("marketGrid");

            for(int i=0; i < market.ROWS; i++)
            {
                for(int j=0; j < market.COLUMNS; j++)
                {
                    ImageView currentMarble = new ImageView();
                    currentMarble.setImage(fromResourceToMarbleImage(marketMatrix[i][j]));
                    marketGrid.add(currentMarble,j,i);
                }
            }

            externalResource.setImage(fromResourceToMarbleImage(market.getExternalResource()));

        });


        //updateMarketMatrix();
    }


 */

    private void updateMarketMatrix() {
        Market market = game.getPlayerTurn(playerNumber).getPlayer().getPersonalBoard().getGeneralBoard().getMarket();
        ResourceTypes[][] marketMatrix = market.getMarketMatrix();

        //access to the gridPane in the scene
        //marketGrid = (GridPane) marketScene.lookup("marketGrid");

        for(int i=0; i < market.ROWS; i++)
        {
            for(int j=0; j < market.COLUMNS; j++)
            {
                ImageView currentMarble = new ImageView();
                currentMarble.setImage(fromResourceToMarbleImage(marketMatrix[i][j]));
                marketGrid.add(currentMarble,j,i);
            }
        }

        externalResource.setImage(fromResourceToMarbleImage(market.getExternalResource()));
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
                return yellowMarble;

            case SERVANT:
                return purpleMarble;

            case SHIELD:
                return blueMarble;

            case STONE:
                return grayMarble;

            case FAITH:
                return redMarble;
        }
        return whiteMarble;
    }


    public void setGame(Game game) {
        this.game = game;
    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        assert marketGrid != null : "fx:id=\"marketGrid\" was not injected: check your FXML file 'Market.fxml'.";

        System.out.println(playerNumber);


        Platform.runLater(() -> {
            Market market = game.getPlayerTurn(playerNumber).getPlayer().getPersonalBoard().getGeneralBoard().getMarket();
            ResourceTypes[][] marketMatrix = market.getMarketMatrix();

            //access to the gridPane in the scene
            //marketGrid = (GridPane) marketScene.lookup("marketGrid");

            for(int i=0; i < market.ROWS; i++)
            {
                for(int j=0; j < market.COLUMNS; j++)
                {
                    ImageView currentMarble = new ImageView();
                    currentMarble.setImage(fromResourceToMarbleImage(marketMatrix[i][j]));
                    marketGrid.add(currentMarble,j,i);
                }
            }

            externalResource.setImage(fromResourceToMarbleImage(market.getExternalResource()));

        });
    }
}



