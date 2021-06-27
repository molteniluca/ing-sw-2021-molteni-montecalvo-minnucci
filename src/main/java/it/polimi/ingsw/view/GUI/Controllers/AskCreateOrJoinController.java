package it.polimi.ingsw.view.GUI.Controllers;

import javafx.animation.Animation;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AskCreateOrJoinController extends GenericController {
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

    @FXML // fx:id="comboBox"
    private ComboBox<Integer> comboBox; // Value injected by FXMLLoader



    @FXML
    void showLeaderSelection(ActionEvent event) throws IOException
    {
        saveInformation();
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

        String currentString;
        String serverAddress = "127.0.0.1";
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

        guiView.startConnection(serverAddress,serverPort);

        guiView.createGame(1);
        guiView.sendNikname("IO");
        guiView.waitForUpdatedGame();
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

        comboBox.getItems().addAll(1,2,3,4);
        //translateTransition1.play();
    }
}
