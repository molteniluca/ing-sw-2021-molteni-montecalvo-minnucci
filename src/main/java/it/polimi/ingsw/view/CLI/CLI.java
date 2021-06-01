package it.polimi.ingsw.view.CLI;

import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.network.NetworkMessages;
import it.polimi.ingsw.model.board.general.Market;
import it.polimi.ingsw.model.board.personal.CardBoard;
import it.polimi.ingsw.model.board.personal.PersonalBoard;
import it.polimi.ingsw.model.cards.DevelopmentCard;
import it.polimi.ingsw.model.board.personal.storage.WarehouseDepots;
import it.polimi.ingsw.model.resources.ResourceTypes;
import it.polimi.ingsw.model.board.personal.storage.StrongBox;
import it.polimi.ingsw.model.resources.Resources;
import it.polimi.ingsw.view.NetworkHandler;
import it.polimi.ingsw.view.View;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Stack;
import java.util.regex.*;

import java.io.*;

import static it.polimi.ingsw.network.NetworkMessages.*;
import static it.polimi.ingsw.model.resources.ResourceTypes.GOLD;
import static it.polimi.ingsw.model.resources.ResourceTypes.SERVANT;
import static it.polimi.ingsw.model.resources.ResourceTypes.SHIELD;
import static it.polimi.ingsw.model.resources.ResourceTypes.STONE;
import static it.polimi.ingsw.view.CLI.ColoredResources.*;
import static it.polimi.ingsw.view.CLI.ColorCLI.*;

/**
 * Concrete class that represent the Command Line interface created by the user
 */
public class CLI extends View{

    private static final int MAX_POSITION = 25;
    private final BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
    private NetworkHandler networkHandler;
    private Game game;
    private boolean gameUpdated = false;
    private boolean actionDone = false; //says if a main action (produce, market, cardDealer) is already done
    private int playerNumber; //the number of the player received before GAMESTARTED

    @Override
    public void run(){

        initializeView();

        while(!gameUpdated){
            try {
                synchronized (this) { //FIXME synchronized should be 'this'?
                    wait();
                }
            }catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        gameUpdated = false;

        //System.out.println(game.getTurn(0).getPlayer().getName()); // prints the name of a player
        playerNumber = (int) waitAndGetResponse();

        //Asks the player the resources he wants depending on the playerNumber
        selectInitialResources();

        //FIXME, it has to be a method that selects the leader cards and than sends them
        {
            Integer[] leaders = {1,2};
            try {
                networkHandler.sendObject(leaders);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println(waitAndGetResponse()); //gamestarted
        while(true) {
            if(waitAndGetResponse() == TURNBEGIN) {
                actionDone = false;
                showHomepage();
            }
        }

    }


    /**
     * Initialize the view and performs basilar operations
     */
    @Override
    public void initializeView() {
        welcomeInfo();

        askServerInfo();

        askCreateOrJoin();

        askNickname();
    }

    /**
     * Method that shows the personalBoard of the user
     */
    @Override
    public void showHomepage() {
        //gameUpdated = false;
        int currentAction = -1;


        while ((currentAction!=0) && (currentAction !=5)){
            refresh();
            showLegend();
            showFaithTrack();
            showCardBoard();
            showStrongbox();
            showWarehouse();

            System.out.println(RESET + "\n1) Show market");
            System.out.println("2) Show card dealer");
            System.out.println("3) Produce");
            System.out.println("4) Show leader cards");
            System.out.println("5) End turn");
            System.out.println("0) Exit game");

            currentAction = integerInput("Select action: ", 0, 5);

            switch (currentAction) {
                case 1:
                    showMarket();
                    break;
                case 2:
                    showCardDealer();
                    break;
                case 3:
                    showProduce();
                    break;
                case 4:
                    System.out.println("NOT IMPLEMENTED YET");
                    break;
                case 5:
                    try {
                        networkHandler.sendObject(TURNEND);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 0:
                    System.exit(0);
                    break;
            }
        }
    }


    /**
     * It clears the screen and then prints the initial
     * information like the title of the game
     */
    @Override
    public void welcomeInfo() {
        refresh();
        printTitle();
    }

    /**
     * Method that asks the user if it wants to create a new game or join
     * an already existing game, than send the answer to the server
     */
    @Override
    public void askCreateOrJoin() {
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


    /**
     * It asks the user information about the server. And than
     * creates the networkHandler with the given info
     */
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

    /**
     * Method that asks the nickname and sends it to thw server
     */
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
                System.out.println("\nWaiting for players ...");
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

    /**
     * It shows the legend of the CLI associating every
     * resource to a colored circle
     */
    private void showLegend() {
        System.out.println(RESET+"Legend\tFaith:" + FAITH + " Gold:" + ColoredResources.GOLD +" Shield:" + ColoredResources.SHIELD + " Servant:" + ColoredResources.SERVANT + " Stone:" + ColoredResources.STONE+"\n");
    }

    /**
     * Method that prints out the faith track of a user
     */
    @Override
    public void showFaithTrack() {
        //To add position received from Server
        int position = game.getTurn(playerNumber).getPlayer().getPersonalBoard().getFaithTrack().getPosition();

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
        System.out.println(RESET);
        System.out.print("\n");
    }

    /**
     * Method that prints the market matrix and the external resource
     * the player is not important because the general board is shared
     * between the players
     */
    public void showMarket() {
        int currentAction = -1;
        int column;
        int row;

        Market market = game.getTurn(playerNumber).getPlayer().getPersonalBoard().getGeneralBoard().getMarket();

        ResourceTypes[][] marketMatrix;
        ResourceTypes externalResource;

        //Prints the market matrix
        while(currentAction!=0) {
            refresh();
            showLegend();

            marketMatrix = market.getMarketMatrix();
            externalResource = market.getExternalResource();
            System.out.println(RESET + "\nMARKET:");
            System.out.print("\t\t\t   ");
            System.out.println(selectResourceColor(externalResource) + ": external resource");

            for (int i = 0; i < market.ROWS; i++) {
                for (int j = 0; j < market.COLUMNS; j++) {
                    System.out.print(selectResourceColor(marketMatrix[i][j]) + "\t");
                }
                System.out.print(RESET + "\b← " + i + "\n");

            }

            System.out.println(" ↑ \t ↑ \t ↑ \t ↑ \t");
            System.out.println(" 0 \t 1 \t 2 \t 3 \t\n");

            showWarehouse();

            //Asks the user if it wants to buy a column or a row
            if(!actionDone) {
                System.out.println("\n1) Buy column");
                System.out.println("2) Buy row");
                System.out.println("0) Exit");

                currentAction = integerInput("Select action: ", 0, 2);

                switch (currentAction) {
                    case 1:
                        column = integerInput("Chose column: ", 0, market.COLUMNS - 1);
                        try {
                            networkHandler.sendObject(BUYCOLUMN);
                            networkHandler.sendObject(column);
                            networkHandler.sendObject(-1); //null effect index
                            market.buyColumn(column); //modify the local copy of the market in order to see the action
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        actionDone = true;
                        break;
                    case 2:
                        row = integerInput("Chose row: ", 0, market.ROWS - 1);
                        try {
                            networkHandler.sendObject(BUYROW);
                            networkHandler.sendObject(row);
                            networkHandler.sendObject(-1); //null effect index
                            market.buyRow(row); //modify the local copy of the market in order to see the action
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        actionDone = true;
                        break;
                    case 0:
                        break;
                }
            }

            else {
                System.out.println(ANSI_GREEN + "You already did a basic action" + RESET);
                System.out.println("0) Exit");
                integerInput("Select action: ", 0, 0);
            }
        }
    }

    /**
     * Method that shows the card dealer, the player is not important because
     * the card dealer is a common object
     */
    public void showCardDealer() {
        int currentAction;
        ArrayList<DevelopmentCard> currentLine = new ArrayList<>(3);
        Stack<DevelopmentCard>[][] cardMatrix = game.getTurn(playerNumber).getPlayer().getPersonalBoard().getGeneralBoard().getCardDealer().getCardMatrix();

        refresh();
        System.out.println("CARD DEALER:");


        for(int i = 0; i <= 2; i++)
        {
            for(int j = 0; j<4; j++)
            {
                currentLine.add(cardMatrix[i][j].peek());
            }
            printCards(currentLine);
            currentLine.clear();
            System.out.print("\n");
        }

        System.out.println();
        showLegend();
        //Asks the user if it wants to buy a column or a row
        if(!actionDone) {
            System.out.println("\n1) Buy card");
            System.out.println("0) Exit");
            currentAction = integerInput("Select action: ", 0, 1);

            try {
                networkHandler.sendObject(BUYCARD);
                //networkHandler.sendObject(row, column, place); //FIXME Place it's the place on the cardBoard
            } catch (IOException e) {
                e.printStackTrace();
            }


            switch (currentAction) {

                case 1:
                    int row = integerInput("Chose row: ", 0, 2);
                    int column = integerInput("Chose column: ", 0, 3);
                    actionDone = true;
                    break;
                case 0:
                    break;
            }
        }
        else
        {
            System.out.println(ANSI_GREEN + "You already did a basic action" + RESET);
            System.out.println("0) Exit");
            integerInput("Select action: ", 0, 0);
        }

    }

    /**
     * Method that shows the WareHouse of a player
     */
    @Override
    public void showWarehouse() {
        System.out.println("WAREHOUSE");
        WarehouseDepots warehouseDepots = game.getTurn(playerNumber).getPlayer().getPersonalBoard().getDeposit().getWarehouseDepots();

        for(int i = 0; i < warehouseDepots.getNumberLevels(); i++){
            //Warehouse layout

            if(i == 0)
                System.out.print("\t");
            else if(i == 1 || i > 2) {
                if(i == 3)
                    System.out.println("Extra deposit");
                System.out.print("  ");
            }

            //Warehouse Resources printed
            if(warehouseDepots.getResourceTypeLevel(i) != null)
                System.out.println(ColoredResources.valueOf(warehouseDepots.getResourceTypeLevel(i).toString()) + ": " + warehouseDepots.getResourcesNumber(i));
            else {
                for (int j = 0; j <= i; j++) {
                    System.out.print(BLANK + " ");
                }
                System.out.print("\n");
            }
        }
    }

    /**
     * Method that shows the strongbox of a player
     */
    @Override
    public void showStrongbox() {
        StrongBox strongBox = game.getTurn(playerNumber).getPlayer().getPersonalBoard().getDeposit().getStrongBox();
        Resources res = strongBox.getResources();
        int i = 0;

        System.out.println("\nSTRONGBOX");
        for (ResourceTypes resourceTypes: ResourceTypes.values()) {
            if(i == 2)
                System.out.println();
            if(!resourceTypes.toString().equals("BLANK") && !resourceTypes.toString().equals("FAITH"))
                System.out.print(ColoredResources.valueOf(resourceTypes.toString()) + ": " + res.getResourceNumber(resourceTypes) + "\t");
            i++;
        }
        System.out.print("\n");
    }


    /**
     * Method that shows the card Board of a player
     */
    public void showCardBoard() {
        CardBoard cardBoard = game.getTurn(playerNumber).getPlayer().getPersonalBoard().getCardBoard();

        System.out.println("CARD BOARD");
        if(cardBoard.getDevelopmentCards().size() == 0)
            System.out.println(ANSI_GREEN+"There are no development cards"+RESET);
        else
            printCards(cardBoard.getDevelopmentCards());
    }


    public void showProduce() {
        int currentAction;

        refresh();
        showCardBoard();
        showStrongbox();
        showWarehouse();

        PersonalBoard personalBoard = game.getTurn(playerNumber).getPlayer().getPersonalBoard();
        Resources totalResources = game.getTurn(playerNumber).getPlayer().getPersonalBoard().getDeposit().getTotalResources();

        if(!actionDone) {
            System.out.println("\n1)Card production");
            System.out.println("2)Base production");
            System.out.println("3)Leader card production"); //FIXME appears only if the player has a particular leader card
            System.out.println("0)Exit");
            currentAction = integerInput("Select action: ", 0, 3);

            //networkHandler.sendObject(PRODUCTION);

            switch (currentAction) {
                case 1:
                    int currentCard;
                    currentCard = integerInput("Select card: ", 0, 3);
                    //FIXME send resources and check if the production is possible
                    break;
                case 2:
                    int firstResource, secondResource, productionResult;
                    System.out.println(ANSI_GREEN + "\nYou can produce one generic resource (except faith) using 2 resources" + RESET);
                    System.out.println("1) Gold, 2) Servant, 3) Shield, 4) Stone");
                    firstResource = integerInput("Select first resource: ", 1, 4);
                    secondResource = integerInput("Select second resource: ", 1, 4);
                    productionResult = integerInput("Select production result: ", 1, 4);
                    //FIXME send resources and than check if you can produce
                    break;
                case 3:
                    //game.getTurn(0).getPlayer().getPersonalBoard().getLeaderBoard().getProductionEffects();
                    //FIXME only if the player has one or two leader cards with the extraProduction effect
                    System.out.println("Leader card effect, NOT IMPLEMENTED YET");
                    System.out.println("1)Activate leader effect one");
                    System.out.println("2)Activate leader effect two");
                    integerInput("Select action: ", 0, 2);
                    //FIXME send action and receive effects
                    break;
                case 0:
                    break;
            }
        }
        else {
            System.out.println(ANSI_GREEN+"You already did a basic action"+RESET);
            System.out.println("0) Exit");
            integerInput("Select action: ", 0, 0);
        }

    }


    //SUPPORT METHODS

    /**
     * Method that sets the game when it is updated. Called by the NetworkHandler if
     * a game object is received
     * @param game the new game received from the server
     */
    @Override
    public synchronized void updateObjects(Game game) {
        this.game = game;
        notify(); //wakes up the thread that was waiting for the game
        gameUpdated = true;
    }




    /**
     * It clears the screen printing a clear character
     */
    private void refresh() {
        System.out.print(ColorCLI.CLEAR);
        System.out.flush();
    }


    /**
     * Method that prints the title of the game in ASCIIArt
     */
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


    /**
     * Method that prints the resources given
     * @param resources the resources that has to be printed
     * @param modality the wat of printing that resources:
     * 'real' prints only shield, stone, gold and servant
     * 'all' prints also faith and blank
     */
    private int printResources(Resources resources, String modality) {
        int printedResources=0;

        if(modality.equals("real")) {
            for (ResourceTypes res : EnumSet.of(GOLD, STONE, SHIELD, SERVANT)) //Only real resources are counted
            {
                int amount = resources.getResourceNumber(res);
                if (amount > 0) {
                    System.out.print(amount + "" + selectResourceColor(res) + ", ");
                    printedResources++;
                }
            }
        }

        if(modality.equals("all")){
            for (ResourceTypes res :ResourceTypes.values()) //Only real resources are counted
            {
                int amount = resources.getResourceNumber(res);
                if (amount > 0) {
                    System.out.print(amount + "" + selectResourceColor(res) + ", ");
                    printedResources++;
                }
            }
        }
        System.out.print("\b\b"); //FIXME erase the last comma and the last space but does not work in terminal
        return printedResources;
    }


    /**
     * Method that associates a ResourceType to a ColoredResources
     * @param resource the resource that has to be printed
     * @return the color of the resource
     */
    private ColoredResources selectResourceColor(ResourceTypes resource) {
        switch (resource) {
            case GOLD:
                return ColoredResources.GOLD;

            case BLANK:
                return ColoredResources.BLANK;

            case FAITH:
                return ColoredResources.FAITH;

            case STONE:
                return ColoredResources.STONE;

            case SHIELD:
                return ColoredResources.SHIELD;

            case SERVANT:
                return ColoredResources.SERVANT;

        }

        return null;
    }


    /**
     * Depending on the number of the player allows the user to
     * select the initial resources they want and sends them to the server
     */
    private void selectInitialResources() {
        int currentAction, secondCurrentAction, sameChoice;
        Resources selectedResources = new Resources();

        refresh();
        switch(playerNumber){
            case 0:
                break;

            case 1:
                System.out.println(ANSI_GREEN+"\nYou can have one resource"+RESET);
                System.out.println("1) Gold, 2) Servant, 3) Shield, 4) Stone");
                currentAction = integerInput("Select resource: ", 1, 4);

                try{
                    switch (currentAction)
                    {
                        case 1:
                            networkHandler.sendObject(GOLD);

                            break;
                        case 2:
                            networkHandler.sendObject(SERVANT);
                            break;
                        case 3:
                            networkHandler.sendObject(SHIELD);
                            break;
                        case 4:
                            networkHandler.sendObject(STONE);
                            break;
                    }
                }catch (IOException e)
                {
                    e.printStackTrace();
                }

                break;

            case 2:
                System.out.println(ANSI_GREEN+"\nYou can have one resource and one faith point"+RESET);
                System.out.println("1) Gold, 2) Servant, 3) Shield, 4) Stone");
                currentAction = integerInput("Select resource: ", 1, 4);
                try{
                    switch (currentAction)
                    {
                        case 1:
                            networkHandler.sendObject(GOLD);
                            break;
                        case 2:
                            networkHandler.sendObject(SERVANT);
                            break;
                        case 3:
                            networkHandler.sendObject(SHIELD);
                            break;
                        case 4:
                            networkHandler.sendObject(STONE);
                            break;
                    }
                }catch (IOException e)
                {
                    e.printStackTrace();
                }
                break;

            case 3:
                System.out.println(ANSI_GREEN+"\nYou can have two resources and one faith point"+RESET);
                System.out.println("1) Gold, 2) Servant, 3) Shield, 4) Stone");
                currentAction = integerInput("Select first resource: ", 1, 4);
                secondCurrentAction = integerInput("Select second resource: ", 1,4);
                try{
                    switch (currentAction)
                    {
                        case 1:
                            networkHandler.sendObject(GOLD);
                            break;
                        case 2:
                            networkHandler.sendObject(SERVANT);
                            break;
                        case 3:
                            networkHandler.sendObject(SHIELD);
                            break;
                        case 4:
                            networkHandler.sendObject(STONE);
                            break;
                    }
                    switch (secondCurrentAction)
                    {
                        case 1:
                            networkHandler.sendObject(GOLD);
                            break;
                        case 2:
                            networkHandler.sendObject(SERVANT);
                            break;
                        case 3:
                            networkHandler.sendObject(SHIELD);
                            break;
                        case 4:
                            networkHandler.sendObject(STONE);
                            break;
                    }

                }catch (IOException e)
                {
                    e.printStackTrace();
                }
                break;
        }
    }


    /**
     * It prints out an ArrayList of cards one by one in the same line
     * @param cards the array of cards that as to be printed
     */
    private void printCards(ArrayList<DevelopmentCard> cards) {
        int tabs; //the number of tabs used for formatiing

        //Type of the card
        System.out.print("\n");
        for (DevelopmentCard card : cards)
        {
            switch (card.getType()){
                case 'b':
                    System.out.print("Type: " + ANSI_BLUE + "Blue" + RESET +"\t\t\t\t\t\t\t");
                    break;
                case 'g':
                    System.out.print("Type: " + ANSI_GREEN + "Green" + RESET +"\t\t\t\t\t\t\t");
                    break;
                case 'p':
                    System.out.print("Type: " + ANSI_PURPLE + "Purple" + RESET +"\t\t\t\t\t\t");
                    break;
                case 'y':
                    System.out.print("Type: " + ANSI_YELLOW + "Yellow" + RESET +"\t\t\t\t\t\t");
                    break;
            }
        }
        //Level of the card
        System.out.print("\n");
        for (DevelopmentCard card : cards)
        {
            System.out.print("Level: "+card.getLevel()+ "\t\t\t\t\t\t\t");
        }

        //Cost of the card
        System.out.print("\n");
        for(DevelopmentCard card : cards)
        {
            Resources cost = card.getCost();
            System.out.print("Cost: ");
            tabs = printResources(cost, "real");
            switch (tabs){
                case 1:
                    System.out.print("\t\t\t\t\t\t\t");
                    break;
                case 2:
                    System.out.print("\t\t\t\t\t");
                    break;
                case 3:
                    System.out.print("\t\t\t\t");
                    break;
            }

        }

        //Production cost
        System.out.print("\n");
        for(DevelopmentCard card : cards)
        {
            Resources productionCost = card.getProductionCost();
            System.out.print("Production Cost: ");
            tabs = printResources(productionCost, "real");

            switch (tabs){
                case 1:
                    System.out.print("\t\t\t\t");
                    break;
                case 2:
                    System.out.print("\t\t\t");
                    break;
                case 3:
                    System.out.print("\t\t");
                    break;
            }
        }

        //Production power
        System.out.print("\n");
        for(DevelopmentCard card : cards)
        {
            Resources productionPower = card.getProductionPower();
            System.out.print("Production Power: ");
            tabs = printResources(productionPower, "all");

            switch (tabs){
                case 1:
                    System.out.print("\t\t\t\t");
                    break;
                case 2:
                    System.out.print("\t\t");
                    break;
                case 3:
                    System.out.print("\t");
                    break;
            }
        }
    }


    /**
     * Method that returns a correct integer input for a particular request
     * @param request The string that has to be print, spaces and columns are required in the string
     * @param min minimum value of the range
     * @param max maximum value of the range
     * @return a correct integer in the range
     */
    private int integerInput(String request, int min, int max) {
        int value = -1;
        do{
            try{
                System.out.print(request);
                value = Integer.parseInt(input.readLine());
                if(value<min || value> max)
                    throw new NumberFormatException();
            }catch (IOException | NumberFormatException e )
            {
                wrongInput();
            }
        }while (value<min || value >max);

        return value;
    }


    /**
     * It notifies the user about a wrong input
     */
    private void wrongInput() {
        System.out.println(ANSI_RED+"Wrong input, retry"+RESET);
    }

}
