package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.model.board.general.Market;
import it.polimi.ingsw.model.board.personal.storage.WarehouseDepots;
import it.polimi.ingsw.model.resources.ResourceTypes;
import it.polimi.ingsw.network.NetworkMessages;
import it.polimi.ingsw.network.ObjectUpdate;
import it.polimi.ingsw.view.NetworkHandler;
import it.polimi.ingsw.view.View;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static it.polimi.ingsw.network.NetworkMessages.*;
import static it.polimi.ingsw.view.CLI.ColorCLI.*;

public class GUI extends View {

    protected Game game;
    protected int playerNumber = 210;
    protected NetworkHandler networkHandler;

    @FXML // fx:id="serverAddressText"
    private TextField serverAddressText; // Value injected by FXMLLoader

    @FXML // fx:id="serverPortText"
    private TextField serverPortText; // Value injected by FXMLLoader

    @FXML // fx:id="startGameButton"
    private Button startGameButton; // Value injected by FXMLLoader

    @FXML // fx:id="printInformation"
    private Button printInformation; // Value injected by FXMLLoader

    @FXML // fx:id="resultText"
    private TextArea resultText; // Value injected by FXMLLoader

    @FXML // fx:id="wrongInput"
    private Text wrongInput; // Value injected by FXMLLoader



    @Override
    public void run() {
        Application.launch(GUIApplication.class);
    }

    @Override
    public void initializeView() {

    }

    @Override
    protected boolean isSuccessReceived() {
        Object message;

        message = waitAndGetResponse();

        if(message == ERROR)
        {
            message = waitAndGetResponse(); //receive the error message
            System.out.print(ANSI_RED);
            System.out.println(message); //prints the error message
            System.out.println("Press enter to continue ");

            return false;
        }

        if(message == SUCCESS)
            waitForUpdatedGame();

        return true;
    }

    @Override
    public void welcomeInfo() {

    }

    @Override
    public void askCreateOrJoin() {

    }

    @Override
    public void askServerInfo() {

    }

    @Override
    public void askNickname() {

    }

    @Override
    public void showHomepage() {

    }

    @Override
    public void showFaithTrack() {

    }

    @Override
    public void showWarehouse(WarehouseDepots warehouseDepots) {

    }

    @Override
    public void showStrongbox() {

    }

    @Override
    public synchronized void updateObjects(Game game) {
        this.game = game;
        notify();
    }

    @Override
    protected void notifyEndGame(boolean youWon) {

    }

    @Override
    public void notifyNewUpdate(ObjectUpdate read) {
        
    }


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
        //marketStage.setFullScreen(true);
        //marketStage.sizeToScene();
        marketStage.show();

    }




    @FXML
    void saveInformation(ActionEvent event) throws IOException {
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
        System.out.println(game.getTurn(playerNumber).getPlayer());

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
        //primaryStage.setFullScreen(true);
        primaryStage.sizeToScene();

        primaryStage.show();
    }

    public void exitGame(ActionEvent event) {
        Platform.exit();
        System.exit(0);
    }

    public Game getGame()
    {
        return game;
    }

}
