package it.polimi.ingsw.view.GUI.Controllers;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static it.polimi.ingsw.network.NetworkMessages.SUCCESS;

public class AskCreateOrJoinController extends GenericController {
    @FXML // fx:id="serverAddressText"
    private TextField serverAddressText; // Value injected by FXMLLoader

    @FXML // fx:id="wrongInput"
    private Text wrongInput; // Value injected by FXMLLoader

    @FXML // fx:id="nameJoin"
    private TextField nameJoin; // Value injected by FXMLLoader

    @FXML // fx:id="gameId"
    private TextField gameId; // Value injected by FXMLLoader

    @FXML // fx:id="nameCreate"
    private TextField nameCreate; // Value injected by FXMLLoader

    @FXML // fx:id="comboBox"
    private ComboBox<Integer> comboBox; // Value injected by FXMLLoader

    @FXML // fx:id="serverResponse"
    private Text serverResponse; // Value injected by FXMLLoader

    @FXML
    public void joinGame(ActionEvent actionEvent) throws IOException {
        connectToServer();

        guiView.joinGame(gameId.getText());
        waitForPlayers(nameJoin.getText(), actionEvent);

        loadGame(actionEvent);
    }

    private void waitForPlayers(String name, ActionEvent event){
        Task task = new Task<Void>() {
            @Override public Void call() {
                try {
                    guiView.sendNickname(name);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                guiView.waitForUpdatedGame();
                Platform.runLater(() -> {
                    try {
                        loadGame(event);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                return null;
            }
        };
        new Thread(task).start();
    }


    @FXML
    void createGame(ActionEvent event) throws IOException
    {
        connectToServer();

        guiView.createGame(comboBox.getValue());
        if(guiView.waitAndGetResponse()==SUCCESS) {
            serverResponse.setText("Game id=" + guiView.waitAndGetResponse());
            waitForPlayers(nameCreate.getText(),event);
        }else
            serverResponse.setText("Error");
    }

    private void loadGame(ActionEvent event) throws IOException {
        Parent leaderSelectionViewParent = FXMLLoader.load(ClassLoader.getSystemResource("FXML/InitialLeaderSelection.fxml"));

        Scene leaderSelectionScene = new Scene(leaderSelectionViewParent);

        //gets the stage information
        Stage leaderSelectionStage = (Stage) ((Node)event.getSource()).getScene().getWindow();

        leaderSelectionStage.setTitle("Leader Selection");
        leaderSelectionStage.setScene(leaderSelectionScene);
        leaderSelectionStage.show();
    }

    @FXML
    void connectToServer() throws IOException {

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
            }

        }

        guiView.startConnection(serverAddress,serverPort);
    }

    @FXML
    void initialize() {
        comboBox.getItems().addAll(1,2,3,4);
    }
}
