package it.polimi.ingsw.view.CLI;

import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.controller.NetworkMessages;
import it.polimi.ingsw.view.NetworkHandler;
import it.polimi.ingsw.view.View;
import java.util.regex.*;

import java.io.*;

import static it.polimi.ingsw.controller.NetworkMessages.*;
import static it.polimi.ingsw.view.CLI.ColoredResources.*;
import static it.polimi.ingsw.view.CLI.ColorCLI.*;

public class CLI extends View {

    private static final int MAX_POSITION = 25;
    private final BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
    private NetworkHandler networkHandler;

    @Override
    public void run() {
        initializeView();

        showHomepage();
    }
    
    @Override
    public void initializeView(){
        welcomeInfo();

        askServerInfo();

        askCreateOrJoin();

        askNickname();
    }

    @Override
    public void welcomeInfo() {
        refresh();
        printTitle();
    }

    @Override
    public void askCreateOrJoin(){
        int currentAction = -1;
        int numberOfPlayers;
        String roomId;
        String currentString;
        NetworkMessages command;

        refresh();
        printTitle();

        do{
            try {
                System.out.println(ANSI_GREEN + "\n1)Create game\n2)Join Game" + RESET);
                System.out.print("Select option: ");
                currentAction = Integer.parseInt(input.readLine());
            }
            catch (IOException | NumberFormatException e)
            {
               wrongInput();
            }

        }while((currentAction < 1) || (currentAction>2));


        try
        {
            //Create game
            if(currentAction == 1)
            {
                do {
                    System.out.print("Insert number of players (max 4): ");
                    currentString = input.readLine();

                    if (!"".equals(currentString.trim()))
                        try {
                            numberOfPlayers = Integer.parseInt(currentString);
                        }catch (NumberFormatException e)
                        {
                            wrongInput();
                            numberOfPlayers = 6;
                        }
                    else
                        numberOfPlayers = 0;

                    if(numberOfPlayers > 4 )
                        System.out.println(ANSI_RED+"The game support max 4 players"+RESET);
                    if(numberOfPlayers <= 0)
                        System.out.println(ANSI_RED+"Insert at least one player"+RESET);

                } while((numberOfPlayers<=0) || (numberOfPlayers >= 5));

                networkHandler.sendObject(CREATEGAME);
                networkHandler.sendObject(numberOfPlayers);

                command = (NetworkMessages) waitAndGetResponse();

                if(command == SUCCESS) {
                    roomId = (String) waitAndGetResponse();
                    System.out.println("\nYou created a game successfully, your room id is " + ANSI_PURPLE + roomId + RESET);
                }
                else
                    System.out.println(ANSI_RED+"Something went wrong, exiting"+RESET);
            }

            //Join game
            else
            {
                do {
                    System.out.print("Insert room id: ");
                    roomId = input.readLine().toUpperCase();
                    if(roomId.length() !=5)
                        System.out.println(ANSI_RED+"Room id must be 5 characters long, retry"+RESET);
                }while(roomId.length() !=5);

                networkHandler.sendObject(JOINGAME);
                networkHandler.sendObject(roomId);
            }

        }catch (IOException  e)
        {
            e.printStackTrace();
        }


    }

    @Override
    public void askServerInfo() {
        String currentString;
        String serverAddress = "localhost";
        int serverPort = 10000;
        String regexNumberIp = "([0-9]|[1-9][0-9]|[1][0-9][0-9]|[2][0-4][0-9]|[2][5][0-5])";

        System.out.println("Please specify the following settings (default values between brackets)");

        boolean correctInput = false;
        while (!correctInput) {
            try {
                System.out.print("Enter server address [127.0.0.1] : ");
                currentString = input.readLine();

                if (!"".equals(currentString.trim())) {
                    //Regex to check input server address
                    String regexAddress = "^" + regexNumberIp + "\\." + regexNumberIp + "\\." + regexNumberIp + "\\." + regexNumberIp + "$";
                    Pattern pattern = Pattern.compile(regexAddress);
                    Matcher matcher = pattern.matcher(currentString);

                    if (!matcher.matches())
                        throw new IOException();

                    serverAddress = currentString;
                }

                correctInput = true;


            } catch (IOException e) {
                wrongInput();
                //correct = false;
            }


        }

        correctInput = false;
        while (!correctInput)
        {
            try {
                System.out.print("Enter server port ["+serverPort+"] : ");
                currentString = input.readLine();

                if (!"".equals(currentString.trim()))
                    serverPort = Integer.parseInt(currentString);

                correctInput = true;
            } catch (NumberFormatException e) {
                wrongInput();
                correctInput = false;
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
         }

        try {
            networkHandler = new NetworkHandler(serverAddress,serverPort,this);
            networkHandler.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void askNickname() {
        String nickname;
        boolean correctInput = false;

        do{
            try
            {
                System.out.print("Please enter your name: ");
                nickname = input.readLine();

                //check if nickname already exists and eventually throws an Exception
                correctInput = true;
                networkHandler.sendObject(nickname);

                System.out.println(waitAndGetResponse());
                /*
               Send nickname and receive ack or nack from the server if name is already taken
               throw new NameAlreadyPresentException("Name already present, chose another one");
                */
            }
            catch (NumberFormatException e) //NameAlreadyPresentException
            {
               wrongInput();
            }

            catch (IOException e)
            {
                System.out.println(e.getMessage());
            }

        }while(!correctInput);
    }

    private void printTitle() {
        System.out.println(ANSI_YELLOW +
                "888b     d888                   888                                            .d888      8888888b.                            d8b                                                      \n" +
                "8888b   d8888                   888                                           d88P\"       888   Y88b                           Y8P                                                      \n" +
                "88888b.d88888                   888                                           888         888    888                                                                                    \n" +
                "888Y88888P888  8888b.  .d8888b  888888 .d88b.  888d888 .d8888b        .d88b.  888888      888   d88P .d88b.  88888b.   8888b.  888 .d8888b  .d8888b   8888b.  88888b.   .d8888b .d88b.  \n" +
                "888 Y888P 888     \"88b 88K      888   d8P  Y8b 888P\"   88K           d88\"\"88b 888         8888888P\" d8P  Y8b 888 \"88b     \"88b 888 88K      88K          \"88b 888 \"88b d88P\"   d8P  Y8b \n" +
                "888  Y8P  888 .d888888 \"Y8888b. 888   88888888 888     \"Y8888b.      888  888 888         888 T88b  88888888 888  888 .d888888 888 \"Y8888b. \"Y8888b. .d888888 888  888 888     88888888 \n" +
                "888   \"   888 888  888      X88 Y88b. Y8b.     888          X88      Y88..88P 888         888  T88b Y8b.     888  888 888  888 888      X88      X88 888  888 888  888 Y88b.   Y8b.     \n" +
                "888       888 \"Y888888  88888P'  \"Y888 \"Y8888  888      88888P'       \"Y88P\"  888         888   T88b \"Y8888  888  888 \"Y888888 888  88888P'  88888P' \"Y888888 888  888  \"Y8888P \"Y8888  \n" +
                "                                                                                                                                                                                        \n" + RESET);
    }

    @Override
    public void showHomepage() {
        refresh();
        showLegend();
        showFaithTrack();
    }

    private void showLegend(){
        System.out.println("Legend\tFaith:" + FAITH + " Gold:" + GOLD +" Shield:" + SHIELD + " Servant:" + SERVANT + " Stone:" + STONE);
    }

    @Override
    public void showFaithTrack(){
        //To add position received from Server
        int position = 2;

        System.out.println("\nFAITH TRACK");
        for(int i =0; i< MAX_POSITION; i++) {
            if ((i >= 5) && (i <= 8) || (i>=12) && (i<=16) || i>=19) {
                System.out.print(ColorCLI.ANSI_YELLOW);
                if (i % 8 == 0)
                    System.out.print(ColorCLI.ANSI_RED);
            } else
                System.out.print(ColorCLI.RESET);
            System.out.print("[ ");
            if (i == position)
                System.out.print("\bX");
            System.out.print("] ");
        }
    }

    @Override
    public void updateObjects(Game game) {

    }

    private void refresh() {
        System.out.print(ColorCLI.CLEAR);
        System.out.flush();
    }

    private void wrongInput(){
        System.out.println(ANSI_RED+"Wrong input, retry"+RESET);
    }

}
