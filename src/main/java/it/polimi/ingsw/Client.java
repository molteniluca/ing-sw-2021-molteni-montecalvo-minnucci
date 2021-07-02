package it.polimi.ingsw;

import it.polimi.ingsw.view.CLI.CLI;
import it.polimi.ingsw.view.GUI.GUIApplication;
import javafx.application.Application;

public class Client {

    public static void main(String[] args) {
        boolean choseGui = true;

        for (String arg : args) {
            if (arg.equals("--cli")) {
                choseGui = false;
                break;
            }
        }

        if (choseGui)
            Application.launch(GUIApplication.class);
        else {
            new Thread(new CLI()).start();
        }
    }
}


