package it.polimi.ingsw;

import it.polimi.ingsw.view.CLI.CLI;
import it.polimi.ingsw.view.GUI.GUI;
import it.polimi.ingsw.view.GUI.GUIApplication;
import it.polimi.ingsw.view.View;
import javafx.application.Application;

public class Client {

    public static void main(String[] args) {
        View view;

        boolean choseGui = false;

        for (String arg : args) {
            if (arg.equals("--gui")) {
                choseGui = true;
                break;
            }
        }

        if (choseGui)
        {
            Application.launch(GUIApplication.class);
        }
        else {
            view = new CLI();
            view.start();
        }
    }
}


