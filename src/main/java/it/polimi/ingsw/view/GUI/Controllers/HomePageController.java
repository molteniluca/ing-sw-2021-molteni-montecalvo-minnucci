package it.polimi.ingsw.view.GUI.Controllers;

import javafx.animation.Animation;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class HomePageController extends GenericController {
    @FXML // fx:id="title"
    private Text title; // Value injected by FXMLLoader

    @FXML
    private ImageView backgroundImage;

    @FXML
    void initialize() {

        if(backgroundImage!=null) {
            //the background image has dimensions a little big bigger than the stage so there are to white borders during the animation
            backgroundImage.setFitWidth(1510);
            backgroundImage.setFitHeight(910);
        }

        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(10), backgroundImage);

        scaleTransition.setCycleCount(Animation.INDEFINITE); //executes the animation an indefinite number of times
        scaleTransition.setAutoReverse(true); //reset to the original size
        scaleTransition.setToX(1.1);
        scaleTransition.setToY(1.1);

        scaleTransition.play();

        ScaleTransition scaleTransition1 = new ScaleTransition(Duration.seconds(6), title);

        scaleTransition1.setCycleCount(Animation.INDEFINITE); //executes the animation an indefinite number of times
        scaleTransition1.setAutoReverse(true); //reset to the original size
        scaleTransition1.setToX(1.1);
        scaleTransition1.setToY(1.1);

        scaleTransition1.play();
    }

    /**
     * Method invoked when game is started
     * @param event start of the game
     * @throws IOException if fxml file is not reachable
     */
    @FXML
    private void startGame(ActionEvent event) throws IOException{
        Parent marketViewParent = FXMLLoader.load(ClassLoader.getSystemResource("FXML/AskCreateOrJoin.fxml"));

        Scene createOrJoinScene = new Scene(marketViewParent);

        //gets the stage information
        Stage primaryStage = (Stage) ((Node)event.getSource()).getScene().getWindow();

        //access to the gridPane in the scene
        //marketGrid = (GridPane) marketScene.lookup("marketGrid");

        primaryStage.setTitle("Masters Of Renaissance");
        primaryStage.setScene(createOrJoinScene);
        primaryStage.show();

    }

    /**
     * Method that terminates game and allows player to exit
     */
    @FXML
    private void exitGame() {
        Platform.exit();
        System.exit(0);
    }
}
