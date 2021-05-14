package it.polimi.ingsw.view.CLI;

import it.polimi.ingsw.controller.ClientHandler;
import it.polimi.ingsw.controller.Server;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.view.View;
import it.polimi.ingsw.view.exceptions.NameAlreadyPresentException;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLOutput;
import java.util.Scanner;
import java.util.regex.Pattern;

public class CLI implements View {

    BufferedReader input = new BufferedReader(new InputStreamReader(System.in));



    @Override
    public void welcomeInfo() {
        refresh();
        printTitle();
    }

    @Override
    public void askCreateOrJoin() throws Exception{
        int currentAction;
        int numberOfPlayers;

        refresh();
        printTitle();
        System.out.println(ColorCLI.ANSI_RED + "\n1)Create game\n2)Join Game"+ ColorCLI.RESET);
        System.out.print("Select option: ");
        currentAction = Integer.parseInt(input.readLine());


        if(currentAction == 1)
        {
            System.out.print("Insert number of players: ");
            numberOfPlayers = Integer.parseInt(input.readLine());
            askServerInfo();
        }

    }

    @Override
    public void askServerInfo() {
        String currentString;
        String serverAddress;
        int serverPort = -1;

        System.out.println("Please specify the following settings (default values between brackets)");

        boolean correctInput = false;
        while (!correctInput) {
            try {
                System.out.print("Enter server address [127.0.0.1] : ");
                currentString = input.readLine();

                //input check sul formato della stringa con RegEX
                if(!"".equals(currentString.trim()))
                    serverAddress = currentString;

                correctInput = true;

            } catch (IOException e) {
                System.out.println("Wrong input, retry");
                //correct = false;
            }


        }

        do{
            try {
                System.out.print("Enter server port [32000] : ");
                currentString = input.readLine();

                if(!"".equals(currentString.trim()))
                    serverPort = Integer.parseInt(currentString);

            }
            catch (NumberFormatException e){
                System.out.println("Input errato, riprovare");
            }
            catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }while (serverPort < 0);

        ///Notify server about creation of a game, receive roomId

       // System.out.print("\nYou created a game, your room id is " + ColorCLI.ANSI_RED + ColorCLI.RESET: ");


    }

    @Override
    public void askNickname() {
        String nickname = null;
        boolean correctInput = false;

        do{
            try
            {
                System.out.println("Please enter your name: ");
                nickname = input.readLine();

                //check if nickname already exists and eventually throws an Exception
                correctInput = true;

                /*
               Send nickname and receive ack or nack from the server if name is already taken
                  throw new NameAlreadyPresentException("Name already present, chose another one");

                 */

            }
            catch (NumberFormatException e) //NameAlreadyPresentException
            {
                System.out.println("Wrong input, retry");
            }

            catch (IOException e)
            {
                System.out.println(e.getMessage());
            }

        }while(!correctInput);


    }



    private void printTitle() {
        System.out.println(ColorCLI.ANSI_YELLOW +"888b     d888                   888                                            .d888      8888888b.                            d8b                                                      \n" +
                "8888b   d8888                   888                                           d88P\"       888   Y88b                           Y8P                                                      \n" +
                "88888b.d88888                   888                                           888         888    888                                                                                    \n" +
                "888Y88888P888  8888b.  .d8888b  888888 .d88b.  888d888 .d8888b        .d88b.  888888      888   d88P .d88b.  88888b.   8888b.  888 .d8888b  .d8888b   8888b.  88888b.   .d8888b .d88b.  \n" +
                "888 Y888P 888     \"88b 88K      888   d8P  Y8b 888P\"   88K           d88\"\"88b 888         8888888P\" d8P  Y8b 888 \"88b     \"88b 888 88K      88K          \"88b 888 \"88b d88P\"   d8P  Y8b \n" +
                "888  Y8P  888 .d888888 \"Y8888b. 888   88888888 888     \"Y8888b.      888  888 888         888 T88b  88888888 888  888 .d888888 888 \"Y8888b. \"Y8888b. .d888888 888  888 888     88888888 \n" +
                "888   \"   888 888  888      X88 Y88b. Y8b.     888          X88      Y88..88P 888         888  T88b Y8b.     888  888 888  888 888      X88      X88 888  888 888  888 Y88b.   Y8b.     \n" +
                "888       888 \"Y888888  88888P'  \"Y888 \"Y8888  888      88888P'       \"Y88P\"  888         888   T88b \"Y8888  888  888 \"Y888888 888  88888P'  88888P' \"Y888888 888  888  \"Y8888P \"Y8888  \n" +
                "                                                                                                                                                                                        \n" + ColorCLI.RESET);
    }

    private void refresh() {
        System.out.print(ColorCLI.CLEAR);
        System.out.flush();
    }
}
