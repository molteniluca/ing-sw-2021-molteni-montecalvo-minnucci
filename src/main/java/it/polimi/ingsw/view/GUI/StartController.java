package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.view.NetworkHandler;
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

public class StartController{

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


    @FXML
    void printInfo(ActionEvent event) {
        resultText.setText("Pollo");
    }


    @FXML
    void saveInformation(ActionEvent event) {
        String currentString;
        String serverAddress = "127.0.0.1";
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



        //serverPort = Integer.parseInt(serverPortText.getText());


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



}
