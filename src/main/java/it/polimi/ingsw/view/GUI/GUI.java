package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.model.board.personal.storage.WarehouseDepots;
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
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static it.polimi.ingsw.network.NetworkMessages.*;

public class GUI extends View {

    private NetworkHandler networkHandler;

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
    public void updateObjects(Game game) {

    }

    @Override
    protected void notifyEndGame(boolean youWon) {

    }







    @FXML
    void saveInformation(ActionEvent event) throws IOException {
        String currentString;
        String serverAddress = "localhost";
        int serverPort = 10000;
        boolean correctInput = false;

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


    }

    @FXML
    void printInfo(ActionEvent event) {
        resultText.setText("Pollo");
    }

    public void startGame(ActionEvent event) throws IOException{
        Parent marketViewParent = FXMLLoader.load(ClassLoader.getSystemResource("FXML/Market.fxml"));

        Scene marketScene = new Scene(marketViewParent);

        //gets the stage information
        Stage marketStage = (Stage) ((Node)event.getSource()).getScene().getWindow();

        marketStage.setTitle("Market");
        marketStage.setScene(marketScene);
        //marketStage.setFullScreen(true);
        //marketStage.sizeToScene();
        marketStage.show();
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
}
