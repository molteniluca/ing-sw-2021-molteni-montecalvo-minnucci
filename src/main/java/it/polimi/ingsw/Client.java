package it.polimi.ingsw;

import it.polimi.ingsw.controller.ClientHandler;
import it.polimi.ingsw.view.CLI.CLI;
import it.polimi.ingsw.view.GUI.GUI;
import it.polimi.ingsw.view.NetworkHandler;
import it.polimi.ingsw.view.View;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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
            view = new GUI();
        else {
            view = new CLI();
        }
        view.start();
    }
}


