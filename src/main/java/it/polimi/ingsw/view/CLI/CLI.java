package it.polimi.ingsw.view.CLI;

import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.model.board.personal.LeaderBoard;
import it.polimi.ingsw.model.cards.DevelopmentCardRequirement;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.model.cards.specialAbility.*;
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

import java.util.*;
import java.util.List;
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

        waitForUpdatedGame();


        choseLeaderCards();

        waitForUpdatedGame();

        System.out.println(waitAndGetResponse()); //gamestarted
        while(true) {
            if(waitAndGetResponse() == TURNBEGIN) {
                waitForUpdatedGame();
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


            WarehouseDepots warehouseDepots = game.getTurn(playerNumber).getPlayer().getPersonalBoard().getDeposit().getWarehouseDepots();
            showWarehouse(warehouseDepots);

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
                    showLeaderBoard();
                    break;
                case 5:
                    if(actionDone) {
                        try {
                            networkHandler.sendObject(TURNEND);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        System.out.println(ANSI_GREEN+"You need to do at least one action before ending a turn"+RESET);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        currentAction = -1;
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
        printMainTitle();
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
        printMainTitle();

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
        while(currentAction != 0) {
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

            WarehouseDepots warehouseDepots = game.getTurn(playerNumber).getPlayer().getPersonalBoard().getDeposit().getWarehouseDepots();
            showWarehouse(warehouseDepots);

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
                            isSuccessReceived();
                            showSwapArea();

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
                            isSuccessReceived();
                            showSwapArea();
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
                currentAction = integerInput("Select action: ", 0, 0);
            }
        }
    }



    /**
     * Method that helps player to organize resources gotten from market in warehouse
     */
    public void showSwapArea() {
        Resources resourcesFromMarket;
        int currentAction, level, tmp, numResOccupied, numResToAdd, numResToMove;
        ResourceTypes resourceTypesToMove;
        boolean exit = false;

        refresh();
        System.out.println("SWAP AREA\n");

        while(!(game.getTurn(playerNumber).getPlayer().getPersonalBoard().getDeposit().getWarehouseDepots().getSwapDeposit().getTotalResourceNumber() ==0 || exit)) {
            WarehouseDepots warehouseDepots = game.getTurn(playerNumber).getPlayer().getPersonalBoard().getDeposit().getWarehouseDepots();
            resourcesFromMarket = warehouseDepots.getSwapDeposit();

            System.out.print("Now you have: ");
            printResources(resourcesFromMarket, "real");
            System.out.print("\n\n");
            showWarehouse(warehouseDepots);

            System.out.println("1) Place resources");
            System.out.println("2) Take resources");
            System.out.println("0) Exit");

            currentAction = integerInput("Select action: ", 0, 2);
            System.out.print("\n");

            switch (currentAction) {
                case 0:
                    String currentString = null;
                    do {
                        System.out.println(ANSI_GREEN + "Are you sure you want to exit? \nEvery unallocated resource would be lost and \nfor each unallocated resource other players move one step forward in their faith track (y/n)" + RESET);
                        try {
                            currentString = input.readLine();
                            currentString = currentString.toLowerCase();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    while (!(currentString.equals("y") || currentString.equals("n")));

                    if(currentString.equals("y"))
                        exit = true;
                    if(resourcesFromMarket.getTotalResourceNumber() > 0) {
                        try {
                            networkHandler.sendObject(DROPRESOURCES);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if(waitAndGetResponse()==SUCCESS){
                            exit=true;
                        }else if(waitAndGetResponse()==ERROR){
                            exit=false;
                            System.out.println(waitAndGetResponse());
                        }
                    }
                    break;
                case 1:
                    do {
                        level = integerInput("On which level? (1-" + warehouseDepots.getNumberLevels() + "): ", 0, warehouseDepots.getNumberLevels()) - 1;
                        if (level == -1)
                            break;
                        if (warehouseDepots.getResourcesNumber(level) > level || level > 2 && warehouseDepots.getResourcesNumber(level) > 1)
                            System.out.println("\nYou selected a full level, please select another one or 0 to quit");
                    }
                    while (warehouseDepots.getResourcesNumber(level) > level || level > 2 && warehouseDepots.getResourcesNumber(level) > 1);

                    if (level == -1)
                        break;

                    if (warehouseDepots.getResourcesNumber(level) == 0 && !warehouseDepots.getLevel(level).getFixedResource()) {
                        do {
                            tmp = integerInput("Which type of resource?\n1) " + ColoredResources.GOLD + "\n2) " + ColoredResources.SERVANT + "\n3) " + ColoredResources.SHIELD + "\n4) " + ColoredResources.STONE + "\n", 1, 4);

                            resourceTypesToMove = numberToResourceType(tmp);
                            if(resourcesFromMarket.getResourceNumber(resourceTypesToMove) == 0)
                                wrongInput();
                        }while(resourcesFromMarket.getResourceNumber(resourceTypesToMove) == 0);
                    } else
                        resourceTypesToMove = warehouseDepots.getResourceTypeLevel(level);

                    if (warehouseDepots.getResourcesNumber(level) == 0 || warehouseDepots.getResourceTypeLevel(level).equals(resourceTypesToMove)) {
                        numResOccupied = warehouseDepots.getResourcesNumber(level);

                        if (0 <= level && level < 3)
                            numResToAdd = level + 1 - numResOccupied;
                        else
                            numResToAdd = 2 - numResOccupied;

                        if (resourcesFromMarket.getResourceNumber(resourceTypesToMove) < numResToAdd)
                            numResToMove = resourcesFromMarket.getResourceNumber(resourceTypesToMove);
                        else
                            numResToMove = numResToAdd;

                        try{
                            networkHandler.sendObject(MOVETOLEVEL);
                            networkHandler.sendObject(level);
                            networkHandler.sendObject(resourceTypesToMove);
                            networkHandler.sendObject(numResToMove);
                        }catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                        if (waitAndGetResponse() == ERROR){
                            System.out.println(waitAndGetResponse());//probably player tries to put res with different type in the same level
                            break;
                        }

                        resourceMovedCorrectly();
                        break;
                    }
                    //
                case 2:
                    do {
                        level = integerInput("From which level? (1-" + warehouseDepots.getNumberLevels() + "): ", 0, warehouseDepots.getNumberLevels()) - 1;
                        if(level == -1)
                            break;
                        if (warehouseDepots.getResourcesNumber(level) == 0)
                            System.out.println("\nYou selected an empty level, please select another one or 0 to quit");
                    }
                    while (warehouseDepots.getResourcesNumber(level) == 0);

                    if (level == -1)
                        break;

                    try{
                        warehouseDepots.moveToSwap(1);
                        networkHandler.sendObject(MOVETOSWAP);
                        networkHandler.sendObject(level);
                    }catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    if (waitAndGetResponse() == ERROR) {
                        System.out.println(waitAndGetResponse());
                        break;
                    }

                    resourceMovedCorrectly();
                    break;

            }
            waitForUpdatedGame();
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
        System.out.println("CARD DEALER");


        for(int i = 0; i <= 2; i++)
        {
            for(int j = 0; j<4; j++)
            {
                currentLine.add(cardMatrix[i][j].peek());
            }
            printDevelopmentCards(currentLine);
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
                switch (currentAction) {

                    case 1:

                        int level = integerInput("Chose development card level (1-3): ", 1, 3);
                        int row = -1; //it will generate error if row does not change
                        switch (level){
                            case 1:
                                row = 2;
                                break;
                            case 2:
                                row = 1;
                                break;
                            case 3:
                                row = 0;
                                break;
                        }
                        int column = integerInput("Chose column (1-4): ", 1, 4) - 1;
                        int place = integerInput("Where do you want to place the card (1-3): ", 1, 3) - 1;

                        networkHandler.sendObject(BUYCARD);
                        networkHandler.sendObject(row);
                        networkHandler.sendObject(column);
                        networkHandler.sendObject(place);

                        actionDone = isSuccessReceived();

                        break;
                    case 0:
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
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
    public void showWarehouse(WarehouseDepots warehouseDepots) {
        int k;
        System.out.println("WAREHOUSE");

        for(int i = 1; i <= warehouseDepots.getNumberLevels(); i++){//For different levels
            //Warehouse layout from left side of screen for each level

            if(i == 1)
                System.out.print("    ");
            else if(i == 2 || i > 3) {
                if(i == 4)
                    System.out.println("Extra deposit");
                System.out.print("  ");
            }


            //Warehouse Resources printed
            for (k = 1; k <= warehouseDepots.getResourcesNumber(i - 1); k++)
                System.out.print(ColoredResources.valueOf(warehouseDepots.getResourceTypeLevel(i - 1).toString()) + " ");

            int j;
            j = (0 < i && i < 4) ? i : 2;

            for (int l = warehouseDepots.getResourcesNumber(i - 1); l < j; l++)
                System.out.print(BLANK + " ");

            if (i > 3)
                System.out.print(ColorCLI.ANSI_GREEN + warehouseDepots.getResourceTypeLevel(i-1).toString() + RESET);

            System.out.print("\n");
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

        System.out.println("\n\nSTRONGBOX");
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
            printDevelopmentCards(Arrays.asList(cardBoard.getUpperDevelopmentCards()));
    }


    /**
     * Method that shows the production options available for a player
     * and sands the choice to the server
     */
    private void showProduce() {
        int currentAction = -1;
        Object message;

        PersonalBoard personalBoard = game.getTurn(playerNumber).getPlayer().getPersonalBoard();
        WarehouseDepots warehouseDepots = game.getTurn(playerNumber).getPlayer().getPersonalBoard().getDeposit().getWarehouseDepots();
        CardBoard cardBoard = personalBoard.getCardBoard();
        Resources totalResources = game.getTurn(playerNumber).getPlayer().getPersonalBoard().getDeposit().getTotalResources();

        refresh();
        showCardBoard();
        showStrongbox();
        showWarehouse(warehouseDepots);

        try {
            networkHandler.sendObject(PRODUCTION);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (currentAction!=0) {
            if (!actionDone) {
                System.out.println("\n1) Base production ");
                System.out.println("2) Card production");
                System.out.println("3) Leader card production"); //FIXME appears only if the player has a particular leader card
                System.out.println("0) Exit");
                currentAction = integerInput("Select action: ", 0, 3);

                try {

                    switch (currentAction) {
                        //Base production
                        case 1:
                            int firstResource, secondResource, productionResult;
                            networkHandler.sendObject(PROD1);
                            System.out.println(ANSI_GREEN + "\nYou can produce one generic resource (except faith) using 2 resources" + RESET);
                            System.out.println("1) Gold, 2) Servant, 3) Shield, 4) Stone");
                            firstResource = integerInput("Select first resource: ", 1, 4);
                            secondResource = integerInput("Select second resource: ", 1, 4);
                            productionResult = integerInput("Select production result: ", 1, 4);

                            networkHandler.sendObject(numberToResourceType(firstResource));
                            networkHandler.sendObject(numberToResourceType(secondResource));
                            networkHandler.sendObject(numberToResourceType(productionResult));
                            //networkHandler.sendObject(ENDPRODUCTION);

                            isSuccessReceived();

                            /*
                            message = waitAndGetResponse();

                            if (message == SUCCESS) {
                                waitForUpdatedGame();
                            }
                            else {
                                message = waitAndGetResponse();
                                System.out.println(message);
                                //System.out.println(ANSI_RED + "You don't have enough resources to activate this production power" + RESET);
                            }

                             */
                            break;

                        //Development card production
                        case 2:
                            int currentCard;
                            networkHandler.sendObject(PROD2);
                            currentCard = integerInput("Select card (0,1,2): ", 0, 2);
                            networkHandler.sendObject(currentCard);
                            isSuccessReceived();

                            /*

                            message = waitAndGetResponse();

                            if (message == SUCCESS) {
                                waitForUpdatedGame();
                            }
                            else {
                                message = waitAndGetResponse(); //Wait for the string that describes the error
                                System.out.println(message);
                                //System.out.println(ANSI_RED + "You don't have enough resources to activate this production power" + RESET);
                            }

                             */
                            break;

                        //leader card production
                        case 3:
                            networkHandler.sendObject(PROD3);
                            //game.getTurn(0).getPlayer().getPersonalBoard().getLeaderBoard().getProductionEffects();
                            //FIXME only if the player has one or two leader cards with the extraProduction effect
                            System.out.println("Leader card effect, NOT IMPLEMENTED YET");
                            System.out.println("1)Activate leader effect one");
                            System.out.println("2)Activate leader effect two");
                            integerInput("Select action: ", 0, 2);
                            //FIXME send action and receive effects
                            break;
                        case 0:
                            actionDone = true;
                            networkHandler.sendObject(ENDPRODUCTION);
                            break;
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {
                System.out.println(ANSI_GREEN + "You already did a basic action" + RESET);
                System.out.println("0) Exit");
                currentAction = integerInput("Select action: ", 0, 0);
            }
        }

    }


    /**
     * Method that shows the leader cards in the hand of an user
     * and asks wat to do with them, discard or play. It also sands
     * the answer to the server
     */
    private void showLeaderBoard() {
        int currentAction;

        LeaderBoard leaderBoard = game.getTurn(playerNumber).getPlayer().getPersonalBoard().getLeaderBoard();
        ArrayList<LeaderCard> leaderInHand = leaderBoard.getLeaderCardsInHand();

        refresh();
        //System.out.println("LEADER CARDS");

        printLeaderCards(leaderBoard.getLeaderCardsInHand());

        System.out.println("\n1) Play leader");
        System.out.println("2) Discard leader");
        System.out.println("0) Exit");
        currentAction = integerInput("Select action: ", 0,2);

        try{
            switch (currentAction) {
                case 0:
                    break;
                case 1:
                    networkHandler.sendObject(ACTIVATELEADER);
                    currentAction = integerInput("Select card: ",1,leaderInHand.size()) - 1;
                    networkHandler.sendObject(currentAction);
                    isSuccessReceived();
                    break;

                case 2:
                    networkHandler.sendObject(DISCARDLEADER);
                    currentAction = integerInput("Select card: ", 1,leaderInHand.size()) - 1;
                    networkHandler.sendObject(currentAction);
                    isSuccessReceived();
                    break;
            }

        }catch (IOException e)
        {
            e.printStackTrace();
        }
    }


    /**
     * Method that allows the user to chose two leader cards at
     * the beginning of the game
     */
    private void choseLeaderCards() {
        Integer[] chose= new Integer[2];
        Object message; //the message received

        LeaderBoard leaderBoard = game.getTurn(playerNumber).getPlayer().getPersonalBoard().getLeaderBoard();

        ArrayList<LeaderCard> leaderCardsInHand = leaderBoard.getLeaderCardsInHand();

        printLeaderCards(leaderCardsInHand);

        chose[0] = integerInput("Select first leader (1-4): ", 1,4) - 1;

        do {
            chose[1] = integerInput("Select second leader (1-4): ", 1, 4)-1;
            if(chose[0].equals(chose[1]))
                System.out.println(ANSI_GREEN+"\nYou already selected this card\n"+RESET);
        }while(chose[0].equals(chose[1]));


        try
        {
            networkHandler.sendObject(chose);
            isSuccessReceived();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

    }


    //SUPPORT METHODS

    /**
     * Method that receives a message and check if it is a success or an error.
     * If it receives a success also wait for the game update, if an error is received error message is printed
     * @return true or false depending on the message received: SUCCESS --> true, ERROR --> false
     */
    private boolean isSuccessReceived() {
        Object message;

        message = waitAndGetResponse();

        if(message == ERROR)
        {
            message = waitAndGetResponse(); //receive the error message
            System.out.print(ANSI_RED);
            System.out.print(message+RESET); //prints the error message
            return false;
        }

        if(message == SUCCESS)
            waitForUpdatedGame();

        return true;
    }

    /**
     * Method that returns resourceTypes associated to number: 1 = GOLD, 2 = SERVANT, 3 = SHIELD, 4 = STONE
     * @param number number from input that you want to convert in resourceTypes
     * @return resourceTypes associated to the number passed
     */
    private ResourceTypes numberToResourceType(int number) {
        ResourceTypes resourceType = null;
        switch (number) {
            case 1:
                resourceType = GOLD;
                break;
            case 2:
                resourceType = SERVANT;
                break;
            case 3:
                resourceType = SHIELD;
                break;
            case 4:
                resourceType = STONE;
                break;
        }
        return resourceType;
    }


    /**
     * Method that prints the end of swap section
     */
    private void resourceMovedCorrectly() {
        String currentString = null;
        do {
            System.out.println("\nResources moved correctly, press enter to continue...");
            try {
                currentString = input.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        while (!"".equals(currentString));
    }


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
    private void printMainTitle() {
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
     * @param mod the way of printing that resources:
     * 'real' prints only shield, stone, gold and servant
     * 'all' prints also faith and blank
     */
    private int printResources(Resources resources, String mod) {
        int printedResources=0;

        if(mod.equals("real")) {
            for (ResourceTypes res : EnumSet.of(GOLD, STONE, SHIELD, SERVANT)) //Only real resources are counted
            {
                int amount = resources.getResourceNumber(res);
                if (amount > 0) {
                    System.out.print(amount + "" + selectResourceColor(res) + ", ");
                    printedResources++;
                }
            }
        }

        if(mod.equals("all")){
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
        int currentAction, secondCurrentAction;

        refresh();
        switch(playerNumber){
            case 0:
                break;

            case 1:
                System.out.println(ANSI_GREEN+"\nYou can have one resource"+RESET);
                System.out.println("1) Gold, 2) Servant, 3) Shield, 4) Stone");
                currentAction = integerInput("Select resource: ", 1, 4);

                try{
                    networkHandler.sendObject(numberToResourceType(currentAction));
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
                    networkHandler.sendObject(numberToResourceType(currentAction));
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
                    networkHandler.sendObject(numberToResourceType(currentAction));
                    networkHandler.sendObject(numberToResourceType(secondCurrentAction));
                }catch (IOException e)
                {
                    e.printStackTrace();
                }
                break;
        }
    }


    /**
     * It prints out an ArrayList of cards one by one in the same line
     * @param cards the array of cards that has to be printed
     */
    private void printDevelopmentCards(List<DevelopmentCard> cards) {
        int tabs; //the number of tabs used for formatting

        //Type of the card
        System.out.print("\n");
        for (DevelopmentCard card : cards)
        {
            if(card!=null) {
                switch (card.getType()) {
                    case 'b':
                        System.out.print("Type: " + ANSI_BLUE + "Blue" + RESET + "\t\t\t\t\t\t\t");
                        break;
                    case 'g':
                        System.out.print("Type: " + ANSI_GREEN + "Green" + RESET + "\t\t\t\t\t\t\t");
                        break;
                    case 'p':
                        System.out.print("Type: " + ANSI_PURPLE + "Purple" + RESET + "\t\t\t\t\t\t");
                        break;
                    case 'y':
                        System.out.print("Type: " + ANSI_YELLOW + "Yellow" + RESET + "\t\t\t\t\t\t");
                        break;
                }
            }
        }
        //Level of the card
        System.out.print("\n");
        for (DevelopmentCard card : cards)
        {
            if(card!=null) {
                System.out.print("Level: " + card.getLevel() + "\t\t\t\t\t\t\t");
            }
        }

        //Cost of the card
        System.out.print("\n");
        for(DevelopmentCard card : cards)
        {
            if(card!=null) {
                Resources cost = card.getCost();
                System.out.print("Cost: ");
                tabs = printResources(cost, "real");
                switch (tabs) {
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

        }

        //Production cost
        System.out.print("\n");
        for(DevelopmentCard card : cards)
        {
            if(card!=null) {
                Resources productionCost = card.getProductionCost();
                System.out.print("Production Cost: ");
                tabs = printResources(productionCost, "real");

                switch (tabs) {
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
        }

        //Production power
        System.out.print("\n");
        for(DevelopmentCard card : cards)
        {
            if(card!=null) {
                Resources productionPower = card.getProductionPower();
                System.out.print("Production Power: ");
                tabs = printResources(productionPower, "all");

                switch (tabs) {
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

    }


    /**
     * It prints out an ArrayList of LeaderCards in different lines
     * @param cards the array of cards that has to be printed
     */
    private void printLeaderCards(ArrayList<LeaderCard> cards) {
        Resources resourceRequirements;
        DevelopmentCardRequirement levelRequirements;
        List<DevelopmentCardRequirement> colorRequirements;
        SpecialAbility specialAbility;

        System.out.println("LEADER CARDS");
        showLegend();
        System.out.println(ANSI_BLUE + "🁢" + RESET+ "1 : Color and number of a development card\n");
        for(LeaderCard card : cards)
        {
            resourceRequirements = card.getResourceRequirements();
            levelRequirements = card.getDevelopmentCardRequirementWithLevel();
            colorRequirements = card.getDevelopmentCardRequirementOnlyColor();
            specialAbility = card.getSpecialAbility();

            System.out.print(ANSI_BOLD+"Requirements: "+RESET);

            if(resourceRequirements!=null) {
                printResources(resourceRequirements, "real");
                System.out.print("\t  ");
            }

            if(levelRequirements!=null)
            {
                switch (levelRequirements.getType())
                {
                    case 'b':
                        System.out.print(ANSI_BLUE + "🁢"+ RESET);
                        break;
                    case 'g':
                        System.out.print(ANSI_GREEN + "🁢" + RESET);
                        break;
                    case 'p':
                        System.out.print(ANSI_PURPLE + "🁢" + RESET);
                        break;
                    case 'y':
                        System.out.print(ANSI_YELLOW+ "🁢" +RESET);
                        break;
                }
                System.out.print("level " + levelRequirements.getLevel()+"");
            }


            if(colorRequirements!=null)
            {
                ArrayList<Character> tmpType = new ArrayList<>();
                ArrayList<Character> tmp2Type;

                for(DevelopmentCardRequirement cardRequirement: colorRequirements)
                    tmpType.add(cardRequirement.getType());

                tmp2Type = new ArrayList<>(tmpType);

                Set<Character> set = new HashSet<>(tmpType); //used to remove every duplicated object
                tmpType.clear();
                tmpType.addAll(set);

                for(Character car1 : tmpType)
                {
                    switch (car1)
                    {
                        case 'b':
                            System.out.print(ANSI_BLUE + "🁢" + RESET);
                            break;
                        case 'g':
                            System.out.print(ANSI_GREEN + "🁢" + RESET);
                            break;
                        case 'p':
                            System.out.print(ANSI_PURPLE + "🁢" + RESET);
                            break;
                        case 'y':
                            System.out.print(ANSI_YELLOW+ "🁢" +RESET);
                            break;
                    }
                    System.out.print(Collections.frequency(tmp2Type, car1) + "  ");

                }
            }

            System.out.print(ANSI_BOLD+"  Special ability: "+RESET);

            if(specialAbility.getClass() == Discount.class)
                System.out.print("Discount: your development cards will cost 1 " +((Discount) specialAbility).getResourceDiscount().toString() + " less");

            if(specialAbility.getClass() == ExtraDeposit.class)
                System.out.print("Extra deposit: you can have 2 extra "+ ((ExtraDeposit) specialAbility).getResourceType().toString()+ " deposits");

            if(specialAbility.getClass() == ExtraProduction.class)
                System.out.print("Extra production: you can use 1 "+((ExtraProduction) specialAbility).getProductionCost()+" to produce 1 resource and 1 faith point");

            if(specialAbility.getClass() == ExtraResource.class)
                System.out.print("Extra resource: all the BLANK resources you get from market will become "+((ExtraResource) specialAbility).getResource());

            System.out.print("\n");
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
