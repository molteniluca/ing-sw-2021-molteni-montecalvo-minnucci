package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.board.personal.FaithTrack;
import it.polimi.ingsw.model.board.personal.storage.WarehouseDepots;
import it.polimi.ingsw.network.NetworkMessages;
import it.polimi.ingsw.network.ObjectUpdate;
import it.polimi.ingsw.view.NetworkHandler;
import it.polimi.ingsw.view.View;
import javafx.animation.Animation;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static it.polimi.ingsw.network.NetworkMessages.*;
import static it.polimi.ingsw.view.CLI.ColorCLI.*;

public class GUI {
    private GUIView guiView;

    @FXML // fx:id="title"
    private Text title; // Value injected by FXMLLoader

    @FXML
    private ImageView backgroundImage;

    @FXML // fx:id="serverAddressText"
    private TextField serverAddressText; // Value injected by FXMLLoader

    @FXML // fx:id="serverPortText"
    private TextField serverPortText; // Value injected by FXMLLoader

    @FXML // fx:id="startGameButton"
    private Button startGameButton; // Value injected by FXMLLoader

    @FXML // fx:id="exitGameButton"
    private Button exitGameButton; // Value injected by FXMLLoader

    @FXML // fx:id="printInformation"
    private Button printInformation; // Value injected by FXMLLoader

    @FXML // fx:id="resultText"
    private TextArea resultText; // Value injected by FXMLLoader

    @FXML // fx:id="wrongInput"
    private Text wrongInput; // Value injected by FXMLLoader


    @FXML
    void showMarket(ActionEvent event) throws IOException
    {
        Parent marketViewParent = FXMLLoader.load(ClassLoader.getSystemResource("FXML/Market.fxml"));


        Scene marketScene = new Scene(marketViewParent);

        //gets the stage information
        Stage marketStage = (Stage) ((Node)event.getSource()).getScene().getWindow();

        //access to the gridPane in the scene
        //marketGrid = (GridPane) marketScene.lookup("marketGrid");

        marketStage.setTitle("Market");
        marketStage.setScene(marketScene);
        marketStage.setMaximized(true);
        //marketStage.sizeToScene();
        marketStage.show();

    }

    @FXML
    void showLeaderSelection(ActionEvent event) throws IOException
    {
        Parent leaderSelectionViewParent = FXMLLoader.load(ClassLoader.getSystemResource("FXML/InitialLeaderSelection.fxml"));

        Scene leaderSelectionScene = new Scene(leaderSelectionViewParent);

        //gets the stage information
        Stage leaderSelectionStage = (Stage) ((Node)event.getSource()).getScene().getWindow();

        leaderSelectionStage.setTitle("Leader Selection");
        leaderSelectionStage.setScene(leaderSelectionScene);
        leaderSelectionStage.show();

    }




    @FXML
    void saveInformation() throws IOException {
        guiView = GUIView.singleton();

        guiView.startConnection("127.0.0.1",10000);
        guiView.createGame(1);
        guiView.sendNikname("IO");

        /*
        String currentString;
        String serverAddress = "localhost";
        int serverPort = 10000;

        String regexNumberIp = "([0-9]|[1-9][0-9]|[1][0-9][0-9]|[2][0-4][0-9]|[2][5][0-5])";

        currentString = serverAddressText.getText();

        if (!"".equals(currentString.trim())) {
            //Regex to check input server address
            String regexAddress = "^" + regexNumberIp + "\\." + regexNumberIp + "\\." + regexNumberIp + "\\." + regexNumberIp + "$";
            Pattern pattern = Pattern.compile(regexAddress);
            Matcher matcher = pattern.matcher(currentString);

            if (!matcher.matches()) {
                wrongInput.setOpacity(1);
            }
            else {
                serverAddress = currentString;
                wrongInput.setOpacity(0);
                printInformation.setDisable(false);
            }

        }

        networkHandler = new NetworkHandler(serverAddress,serverPort,this);
        networkHandler.start();

        //serverPort = Integer.parseInt(serverPortText.getText());



        //TODO now is hardcoded but the user will chose the following options
        networkHandler.sendObject(CREATEGAME);
        networkHandler.sendObject(1); //number of players
        NetworkMessages command = (NetworkMessages) waitAndGetResponse();

        if(command == SUCCESS) {
            String roomId = (String) waitAndGetResponse();
            System.out.println("\nYou created a game successfully, your room id is " + ANSI_PURPLE + roomId);
        }
        else
            System.out.println(ANSI_RED+"Something went wrong, exiting");

        networkHandler.sendObject("Pollo"); //nickname

        waitForUpdatedGame();
        playerNumber = (int) waitAndGetResponse();
        System.out.print(ANSI_GREEN);
        System.out.println(playerNumber);
        System.out.println(game.getPlayerTurn(playerNumber).getPlayer());

         */

    }

    @FXML
    void printInfo(ActionEvent event) {
        resultText.setText("Pollo");
    }


    public void backHome(ActionEvent actionEvent) throws IOException {
        Parent marketViewParent = FXMLLoader.load(ClassLoader.getSystemResource("FXML/AskCreateOrJoin.fxml"));

        Scene homeScene = new Scene(marketViewParent);

        //gets the stage information
        Stage primaryStage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();

        //marketStage.setTitle("");
        primaryStage.setScene(homeScene);
        primaryStage.sizeToScene();

        primaryStage.show();
    }


    @FXML
    public void startGame(ActionEvent event) throws IOException{
        Rectangle2D screenBounds = Screen.getPrimary().getBounds();
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

    @FXML
    public void exitGame(ActionEvent event) {
        Platform.exit();
        System.exit(0);
    }

    @FXML
    void initialize() {

        Rectangle2D screenBounds = Screen.getPrimary().getBounds();
        if(backgroundImage!=null) {
            backgroundImage.setFitWidth(screenBounds.getMaxX());
            backgroundImage.setFitHeight(screenBounds.getMaxY());
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

        //animates the background
        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(5), title);
        translateTransition.setCycleCount(Animation.INDEFINITE);
        translateTransition.setAutoReverse(true);
        translateTransition.setByX(250);

        //translateTransition1.play();
    }
}
