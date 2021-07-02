package it.polimi.ingsw.view.CLI;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.board.personal.FaithTrack;
import it.polimi.ingsw.model.board.personal.LeaderBoard;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.model.cards.specialAbility.*;
import it.polimi.ingsw.network.NetworkMessages;
import it.polimi.ingsw.model.board.general.Market;
import it.polimi.ingsw.model.board.personal.CardBoard;
import it.polimi.ingsw.model.cards.DevelopmentCard;
import it.polimi.ingsw.model.board.personal.storage.WarehouseDepots;
import it.polimi.ingsw.model.resources.ResourceTypes;
import it.polimi.ingsw.model.board.personal.storage.StrongBox;
import it.polimi.ingsw.model.resources.Resources;
import it.polimi.ingsw.network.exceptions.FullRoomException;
import it.polimi.ingsw.view.View;
import it.polimi.ingsw.view.exceptions.UnknownIdException;

import java.util.*;
import java.util.regex.*;

import java.io.*;

import static it.polimi.ingsw.network.NetworkMessages.*;
import static it.polimi.ingsw.view.CLI.ColoredResources.*;
import static it.polimi.ingsw.view.CLI.ColorCLI.*;

/**
 * Concrete class that represent the Command Line interface created by the user
 */
public class CLI extends View implements Runnable{

    private static final int MAX_POSITION = 25;
    private final BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
    private int winOrLose = -1; //says if a player won the game, 1 won 0 don't
    private boolean actionDone = false; //says if a main action (produce, market, cardDealer) has been done
    private boolean singlePlayer = false; //says if a player is playing alone
    private CliSupporter cliSupporter; //supporter class that makes the CLI thinner


    @Override
    public void run() {

        cliSupporter = new CliSupporter(input);

        initializeView();

        waitForUpdatedGame();

        //System.out.println(game.getTurn(0).getPlayer().getName()); // prints the name of a player

        //Asks to the player the resources it wants depending on playerNumber
        selectInitialResources();

        waitForUpdatedGame();

        cliSupporter.refresh();

        choseLeaderCards();

        waitForUpdatedGame();


        System.out.println(ANSI_GREEN + "Waiting for players ..." + RESET);

        waitAndGetResponse(); //game started

        while (winOrLose < 0) {
            if(waitAndGetResponse()==TURNBEGIN){
                try {
                    System.out.print(ANSI_GREEN + "\rIt's your turn, " + ANSI_BOLD + game.getPlayerTurn(playerNumber).getPlayer().getName() + RESET + ANSI_GREEN + " press enter to start " + RESET);
                    input.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                waitForUpdatedGame();
                actionDone = false;
                showHomepage();
            }
        }


        if(winOrLose == 1) {
            cliSupporter.refresh();
            System.out.println("CONGRATULATIONS, YOU WON");
        }
        else {
            cliSupporter.refresh();
            System.out.println("You lose :(");
        }

        System.out.println("Press enter to close the game ");
        try {
            input.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        closeConnection();
        System.exit(0);

    }


    /**
     * Initialize the view and performs basilar operations
     */
    public void initializeView() {
        welcomeInfo();

        askServerInfo();

        askCreateOrJoin();

        askNickname();
    }


    /**
     * Method that shows the personalBoard of the user
     */
    public void showHomepage() {

        int currentAction = -1;
        Player player;

        while ((currentAction!=0) && (currentAction !=6)){
            player = game.getPlayerTurn(playerNumber).getPlayer();
            cliSupporter.refresh();
            cliSupporter.showLegend();
            cliSupporter.printName(player);
            cliSupporter.printTitle("\nFAITH TRACK");
            showFaithTrack(player.getPersonalBoard().getFaithTrack());

            if(singlePlayer)
            {
                cliSupporter.printTitle("\nLORENZO'S FAITH TRACK");
                showFaithTrack(this.game.getSelfPLayingTurn().getLorenzo().getFaithTrack());
            }

            showCardBoard(game.getPlayerTurn(playerNumber).getPlayer());
            showStrongbox(playerNumber);

            WarehouseDepots warehouseDepots = game.getPlayerTurn(playerNumber).getPlayer().getPersonalBoard().getDeposit().getWarehouseDepots();
            showWarehouse(warehouseDepots);
            if(game.getPlayerTurn(playerNumber).getPlayer().getPersonalBoard().getLeaderBoard().getLeaderCards().size()>0) {
                cliSupporter.printTitle("\nACTIVE LEADER CARDS");
                cliSupporter.printLeaderCards(game.getPlayerTurn(playerNumber).getPlayer().getPersonalBoard().getLeaderBoard().getLeaderCards());
            }

            System.out.println(RESET + "\n1) Show market");
            System.out.println("2) Show card dealer");
            System.out.println("3) Produce");
            System.out.println("4) Show leader cards");
            System.out.println("5) Show other players");
            System.out.println("6) End turn");
            System.out.println("0) Exit game");

            currentAction = cliSupporter.integerInput("\nSelect action: ", 0, 6);

            switch (currentAction) {
                case 1:
                    showMarket();
                    break;
                case 2:
                    showCardDealer(playerNumber);
                    break;
                case 3:
                    showProduce();
                    break;
                case 4:
                    showLeaderBoard();
                    break;
                case 5:
                    if(!singlePlayer) {
                        //Show other players name except the current player
                        for(int i =0; i<game.getNumPlayers(); i++)
                        {
                            if(i!=playerNumber) {
                                System.out.println(i + 1 + ") " + game.getPlayerTurn(i).getPlayer().getName());
                            }
                        }
                        int chosePlayer = cliSupporter.integerInput("Select player (0 to exit): ", 0, game.getNumPlayers())-1;
                        if((chosePlayer == -1) || (chosePlayer == playerNumber)){
                            currentAction = -1;
                            continue;
                        }

                        cliSupporter.refresh();
                        cliSupporter.showLegend();
                        cliSupporter.printName(game.getPlayerTurn(chosePlayer).getPlayer());
                        cliSupporter.printTitle("\nFAITH TRACK");
                        showFaithTrack(game.getPlayerTurn(chosePlayer).getPlayer().getPersonalBoard().getFaithTrack());
                        showCardBoard(game.getPlayerTurn(chosePlayer).getPlayer());
                        showStrongbox(chosePlayer);
                        WarehouseDepots warehouseDepotsPlayer = game.getPlayerTurn(chosePlayer).getPlayer().getPersonalBoard().getDeposit().getWarehouseDepots();
                        showWarehouse(warehouseDepotsPlayer);

                        if(game.getPlayerTurn(chosePlayer).getPlayer().getPersonalBoard().getLeaderBoard().getLeaderCards().size() > 0) {
                            cliSupporter.printTitle("\nACTIVE LEADER CARDS");
                            cliSupporter.printLeaderCards(game.getPlayerTurn(chosePlayer).getPlayer().getPersonalBoard().getLeaderBoard().getLeaderCards());
                        }
                    }
                    else
                        System.out.println(ANSI_GREEN+"\nYour are playing against Lorenzo the Magnificent, he does not reveal his secrets"+RESET);
                    System.out.print(ANSI_GREEN+"\nPress enter to continue "+RESET);
                    try {
                        input.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    currentAction = -1;
                    break;
                case 6:
                    if(actionDone) {
                        try {
                            endTurn();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if(singlePlayer)
                        {
                            System.out.println(ANSI_GREEN+"Lorenzo the Magnificent is playing "+RESET);
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            System.out.println(ANSI_GREEN+game.getSelfPLayingTurn().getLorenzo().getLastAction().toString()+RESET);
                        }
                        else
                            System.out.print(ANSI_GREEN+"Waiting for players ... "+RESET);
                    }
                    if(!actionDone)
                    {
                        System.out.print(ANSI_GREEN+"You need to do at least one action before ending a turn, press enter to continue "+RESET);
                        try {
                            input.readLine();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        currentAction = -1;
                    }

                    break;

                case 0:
                    System.out.print("\nAre you sure you want to exit the game? (y/n): ");
                    try {
                        if(input.readLine().trim().equals("y")) {
                            System.exit(0);
                            break;
                        }
                        else
                            currentAction = -1;
                            break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
        }
    }


    /**
     * It clears the screen and then prints the initial
     * information like the title of the game
     */
    public void welcomeInfo() {
        cliSupporter.refresh();
        cliSupporter.printMainTitle();
    }


    /**
     * Method that asks the user if it wants to create a new game or join
     * an already existing game, than send the answer to the server
     */
    public void askCreateOrJoin() {
        int currentAction = -1;
        int numberOfPlayers;
        String roomId;
        String currentString;
        NetworkMessages command;

        cliSupporter.refresh();
        cliSupporter.printMainTitle();

        do{
            try {
                System.out.println(ANSI_GREEN + "\n1)Create game\n2)Join Game" + RESET);
                System.out.print("Select option: ");
                currentAction = Integer.parseInt(input.readLine());
            }
            catch (IOException | NumberFormatException e)
            {
                cliSupporter.wrongInput();
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
                            cliSupporter.wrongInput();
                            numberOfPlayers = 6; //used because while requires numberOfPlayers <=0 or >= 5
                        }
                    else
                        numberOfPlayers = 0;

                    if(numberOfPlayers > 4 )
                        System.out.println(ANSI_RED+"The game support max 4 players"+RESET);
                    if(numberOfPlayers <= 0)
                        System.out.println(ANSI_RED+"Insert at least one player"+RESET);

                } while((numberOfPlayers<=0) || (numberOfPlayers >= 5));


                if(numberOfPlayers == 1)
                    singlePlayer = true;

                createGame(numberOfPlayers);

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
                joinGame(roomId);
            }

        }catch (IOException  e)
        {
            System.out.println(ANSI_RED+"Interrupted connection to the server"+RESET);
            System.exit(1);
            //e.printStackTrace();
        }


    }


    /**
     * It asks the user information about the server. And than
     * creates the networkHandler with the given info
     */
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
                cliSupporter.wrongInput();
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
                cliSupporter.wrongInput();
                correctInput = false;
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

        try {
            startConnection(serverAddress,serverPort);
        } catch (IOException e) {
            System.out.println(ANSI_RED+"Cannot connect to the server"+RESET);
            System.exit(1);
        }
    }


    /**
     * Method that asks the nickname and sends it to thw server
     */
    public void askNickname() {
        String nickname;
        boolean correctInput = false;

        do{
            try
            {
                System.out.print("Please enter your name: ");
                nickname = input.readLine();

                try {
                    sendNickname(nickname);
                } catch (UnknownIdException e) {
                    System.out.print(ANSI_RED+"\nTrying to join a non existing room"+RESET);
                } catch (FullRoomException s) {
                    System.out.print(ANSI_RED+"\nTrying to join a full room"+RESET);
                }
                //check if nickname already exists and eventually throws an Exception
                correctInput = true;
                System.out.println("\nWaiting for players ...");
            }
            catch (NumberFormatException e) //NameAlreadyPresentException
            {
                cliSupporter.wrongInput();
            }

            catch (IOException e)
            {
                System.out.println(e.getMessage());
            }

        }while(!correctInput);
    }


    /**
     * Method that prints out the faith track of a user
     * @param faithTrack the faith track that has to be shown
     */
    public void showFaithTrack(FaithTrack faithTrack) {
        //To add position received from Server

        int position = faithTrack.getPosition();
        int[] faithCards = faithTrack.getFaithCards();

        for(int i =0; i< MAX_POSITION; i++) {
            if ((i >= 5) && (i <= 8) || (i>=12) && (i<=16) || i>=19) {
                System.out.print(ColorCLI.ANSI_YELLOW);
                if (i % 8 == 0)
                    System.out.print(ColorCLI.ANSI_RED);

            } else
                System.out.print(ColorCLI.RESET);
            System.out.print("[ ");
            if(i == 8)
                System.out.print(printFaithCard(faithCards[0]));
            if(i == 16)
                System.out.print(printFaithCard(faithCards[1]));
            if(i == 24)
                System.out.print(printFaithCard(faithCards[2]));
            if (i == position)
                System.out.print("\bX");
            System.out.print("] ");
        }
        System.out.println(RESET);
        System.out.println("X: current position, ▲: activated card, 🀫: discarded card");
        System.out.print("\n");
    }

    /**
     * Associates each FaithCard to a particular symbol
     * @param card the pope Card that has to be printed
     * @return the symbol associated with each faithCard depending on its position
     */
    private String printFaithCard(int card) {
        switch (card){
            case 1:
                return "▲";
            case 2:
                return "🀫";
        }
        return "";
    }


    /**
     * Method that prints the market matrix and the external resource
     * the player is not important because the general board is shared
     * between the players
     */
    public void showMarket() {
        int currentAction = -1;
        int extraResourceIndex; //null effect index
        int column;
        int row;

        Market market;
        LeaderBoard leaderBoard = game.getPlayerTurn(playerNumber).getPlayer().getPersonalBoard().getLeaderBoard();
        ArrayList<ExtraResource> extraResources  = game.getPlayerTurn(playerNumber).getPlayer().getPersonalBoard().getLeaderBoard().getExtraResource();

        ResourceTypes[][] marketMatrix;
        ResourceTypes externalResource;


        while(currentAction != 0) {

            extraResourceIndex = -1;

            cliSupporter.refresh();
            cliSupporter.showLegend();

            market = game.getPlayerTurn(playerNumber).getPlayer().getPersonalBoard().getGeneralBoard().getMarket();
            marketMatrix = market.getMarketMatrix();
            externalResource = market.getExternalResource();

            cliSupporter.printTitle("\nMARKET");

            System.out.print("\t\t\t   ");
            System.out.println(cliSupporter.selectResourceColor(externalResource) + ": external resource");

            //Prints the market matrix
            for (int i = 0; i < market.ROWS; i++) {
                for (int j = 0; j < market.COLUMNS; j++) {
                    System.out.print(cliSupporter.selectResourceColor(marketMatrix[i][j]) + "\t");
                }
                System.out.print(RESET + "\b← " + i + "\n");

            }

            System.out.println(" ↑ \t ↑ \t ↑ \t ↑ \t");
            System.out.println(" 0 \t 1 \t 2 \t 3 \t\n");

            WarehouseDepots warehouseDepots = game.getPlayerTurn(playerNumber).getPlayer().getPersonalBoard().getDeposit().getWarehouseDepots();
            showWarehouse(warehouseDepots);

            //Asks the user if it wants to buy a column or a row
            if(!actionDone) {
                System.out.println("\n1) Buy column");
                System.out.println("2) Buy row");
                System.out.println("0) Exit");

                currentAction = cliSupporter.integerInput("Select action: ", 0, 2);

                switch (currentAction) {
                    case 1:
                        column = cliSupporter.integerInput("Chose column: ", 0, market.COLUMNS - 1);

                        if(extraResources.size() > 0)
                        {
                            System.out.println("\nACTIVE LEADER CARDS");
                            cliSupporter.printLeaderCards(leaderBoard.getLeaderCards());
                            extraResourceIndex = 0;

                            if(extraResources.size() == 2)
                                extraResourceIndex = cliSupporter.integerInput("Which leader effect do you want to apply (1-2) ?", 1,2)-1;
                        }

                        try {
                            marketBuyColumn(column,extraResourceIndex);

                            isSuccessReceived();
                            showSwapArea();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        actionDone = true;
                        break;

                    case 2:
                        row = cliSupporter.integerInput("Chose row: ", 0, market.ROWS - 1);

                        if(extraResources.size() > 0)
                        {
                            System.out.println("\nACTIVE LEADER CARDS");
                            cliSupporter.printLeaderCards(leaderBoard.getLeaderCards());
                            extraResourceIndex = 0;

                            if(extraResources.size() == 2)
                                extraResourceIndex = cliSupporter.integerInput("Which leader effect do you want to apply (1-2) ?", 1,2)-1;
                        }

                        try {
                            marketBuyRow(row,extraResourceIndex);
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
                currentAction = cliSupporter.integerInput("Select action: ", 0, 0);
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

        cliSupporter.refresh();
        System.out.println("SWAP AREA\n");

        while(!(game.getPlayerTurn(playerNumber).getPlayer().getPersonalBoard().getDeposit().getWarehouseDepots().getSwapDeposit().getTotalResourceNumber() == 0 || exit)) {
            WarehouseDepots warehouseDepots = game.getPlayerTurn(playerNumber).getPlayer().getPersonalBoard().getDeposit().getWarehouseDepots();
            resourcesFromMarket = warehouseDepots.getSwapDeposit();

            System.out.print("Now you have: ");
            cliSupporter.printResources(resourcesFromMarket, "real");
            System.out.print("\n\n");
            showWarehouse(warehouseDepots);

            System.out.println("1) Place resources");
            System.out.println("2) Take resources");
            System.out.println("0) Exit");

            currentAction = cliSupporter.integerInput("Select action: ", 0, 2);
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

                    if (currentString.equals("y"))
                        exit = true;
                    if (resourcesFromMarket.getTotalResourceNumber() > 0) {
                        try {
                            swapDropResources();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (waitAndGetResponse() == SUCCESS) {
                            exit = true;
                        } else if (waitAndGetResponse() == ERROR) {
                            exit = false;
                            System.out.println(waitAndGetResponse());
                        }
                    }
                    break;
                case 1:
                    do {
                        level = cliSupporter.integerInput("On which level? (1-" + warehouseDepots.getNumberLevels() + "): ", 0, warehouseDepots.getNumberLevels()) - 1;
                        if (level == -1)
                            break;
                        if (warehouseDepots.getResourcesNumber(level) > level || level > 2 && warehouseDepots.getResourcesNumber(level) > 1)
                            System.out.println("\nYou selected a full level, please select another one or 0 to quit");
                    }
                    while (warehouseDepots.getResourcesNumber(level) > level || level > 2 && warehouseDepots.getResourcesNumber(level) > 1);

                    if (level == -1)
                        break;

                    if (warehouseDepots.getResourcesNumber(level) == 0 && !warehouseDepots.getLevel(level).isFixedResource()) {
                        do {
                            tmp = cliSupporter.integerInput("Which type of resource?\n1) " + ColoredResources.GOLD + "\n2) " + ColoredResources.SERVANT + "\n3) " + ColoredResources.SHIELD + "\n4) " + ColoredResources.STONE + "\n", 1, 4);

                            resourceTypesToMove = cliSupporter.numberToResourceType(tmp);
                            if (resourcesFromMarket.getResourceNumber(resourceTypesToMove) == 0)
                                cliSupporter.wrongInput();
                        } while (resourcesFromMarket.getResourceNumber(resourceTypesToMove) == 0);
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

                        try {
                            swapMoveToLevel(level, resourceTypesToMove, numResToMove);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (waitAndGetResponse() == ERROR) {
                            System.out.println(waitAndGetResponse());//probably player tries to put res with different type in the same level
                            break;
                        }

                        cliSupporter.resourceMovedCorrectly();
                        break;
                    }
                    //
                case 2:
                    do {
                        level = cliSupporter.integerInput("From which level? (1-" + warehouseDepots.getNumberLevels() + "): ", 0, warehouseDepots.getNumberLevels()) - 1;
                        if (level == -1)
                            break;
                        if (warehouseDepots.getResourcesNumber(level) == 0)
                            System.out.println("\nYou selected an empty level, please select another one or 0 to quit");
                    }
                    while (warehouseDepots.getResourcesNumber(level) == 0);

                    if (level == -1)
                        break;

                    try {
                        warehouseDepots.moveToSwap(1);
                        swapMoveToSwap(level);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (waitAndGetResponse() == ERROR) {
                        System.out.println(waitAndGetResponse());
                        break;
                    }

                    cliSupporter.resourceMovedCorrectly();
                    break;

            }
            waitForUpdatedGame();
        }
    }


    /**
     *  Method that shows the card dealer
     * @param player that has to be shown, used to print out the card board when showing
     *               the card dealer
     */
    public void showCardDealer(int player) {
        int currentAction;
        ArrayList<DevelopmentCard> currentLine = new ArrayList<>(3);
        Stack<DevelopmentCard>[][] cardMatrix;

        do {
            cardMatrix = game.getPlayerTurn(player).getPlayer().getPersonalBoard().getGeneralBoard().getCardDealer().getCardMatrix();
            cliSupporter.refresh();
            cliSupporter.printTitle("CARD DEALER");

            //Prints the card dealer line by line
            for (int i = 0; i <= 2; i++) {
                for (int j = 0; j < 4; j++) {
                    try {
                        currentLine.add(cardMatrix[i][j].peek());
                    } catch (EmptyStackException e) {
                        currentLine.add(null);
                    }
                }
                cliSupporter.printDevelopmentCards(currentLine, game.getPlayerTurn(player).getPlayer().getPersonalBoard());
                currentLine.clear();
                System.out.print("\n");
            }

            System.out.println();

            //separate the card dealer from the card board
            for (int i = 0; i < 70; i++)
                System.out.print("= "); //there must be a space


            System.out.print("\n");
            showCardBoard(game.getPlayerTurn(player).getPlayer());
            System.out.print("\n\n");
            cliSupporter.showLegend();

            //Asks the user if it wants to buy a column or a row
            if (!actionDone) {
                System.out.println("\n1) Buy card");
                System.out.println("0) Exit");
                currentAction = cliSupporter.integerInput("Select action: ", 0, 1);

                try {
                    switch (currentAction) {

                        case 1:

                            int row = 3 - cliSupporter.integerInput("Chose level (1-3, 0 to exit): ", 0, 3);
                            if (row == 3)
                                continue;

                            int column = cliSupporter.integerInput("Chose column (1-4, 0 to exit): ", 0, 4) - 1;
                            if (column == -1)
                                continue;

                            int place = cliSupporter.integerInput("Where do you want to place the card in your card board (1-3, 0 to exit)? ", 0, 3) - 1;
                            if (place == -1)
                                continue;

                            try {
                                cardMatrix[row][column].peek();
                            } catch (EmptyStackException e) {
                                System.out.print(ANSI_RED + "There are no more development cards in the selected cell, press enter to continue " + RESET);
                                input.readLine();
                                continue;
                            }

                            marketBuyCard(row,column,place);

                            actionDone = isSuccessReceived();

                            break;
                        case 0:
                            break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println(ANSI_GREEN + "You already did a basic action" + RESET);
                System.out.println("0) Exit");
                currentAction = cliSupporter.integerInput("Select action: ", 0, 0);
            }
        }while (currentAction !=0 );
    }


    /**
     * Method that shows the WareHouse of a player
     * @param warehouseDepots the wareHouse that has to be shown
     */
    public void showWarehouse(WarehouseDepots warehouseDepots) {
        int k;
        cliSupporter.printTitle("\nWAREHOUSE");

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
            j = (0 < i && i < 4) ? i : 2; //levels from 1 to 3 has 1 to 3 resources, others (4, 5) have 2 resource (leaders)

            for (int l = warehouseDepots.getResourcesNumber(i - 1); l < j; l++)
                System.out.print(BLANK + " ");

            if (i > 3)
                System.out.print(ColorCLI.ANSI_GREEN + warehouseDepots.getResourceTypeLevel(i-1).toString() + RESET);

            System.out.print("\n");
        }
    }


    /**
     * Method that shows the strongbox of a player
     * @param showPlayer the player that owns the the strongbox that has to be shown
     */
    public void showStrongbox(int showPlayer) {
        StrongBox strongBox = game.getPlayerTurn(showPlayer).getPlayer().getPersonalBoard().getDeposit().getStrongBox();
        Resources res = strongBox.getResources();
        int i = 0;

        cliSupporter.printTitle("\n\nSTRONGBOX");

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
     * @param player the player that owns the card board that has to b shown
     */
    public void showCardBoard(Player player) {
        CardBoard cardBoard = player.getPersonalBoard().getCardBoard();

        cliSupporter.printTitle("CARD BOARD");
        cliSupporter.printDevelopmentCards(Arrays.asList(cardBoard.getUpperDevelopmentCards()), player.getPersonalBoard());

    }


    /**
     * Method that shows the production options available for a player
     * and sands the choice to the server
     */
    private void showProduce() {
        int currentAction;
        int upperLimit; // the limit of integer input while selecting the action, it depends by the presence of leader cards with extra production effects
        //three boolean values because every single production can fail but if one of them succeed a basic action is done
        boolean temporaryAction1 = false;
        boolean temporaryAction2 = false;
        boolean temporaryAction3 = false;

        boolean[] alreadyUsed = {false, false, false};

        WarehouseDepots warehouseDepots = game.getPlayerTurn(playerNumber).getPlayer().getPersonalBoard().getDeposit().getWarehouseDepots();

        LeaderBoard leaderBoard = game.getPlayerTurn(playerNumber).getPlayer().getPersonalBoard().getLeaderBoard();
        ArrayList<LeaderCard> activeLeaders = leaderBoard.getLeaderCards();

        do {
            upperLimit = 2;
            ArrayList<ExtraProduction> extraProductionEffect = game.getPlayerTurn(playerNumber).getPlayer().getPersonalBoard().getLeaderBoard().getProductionEffects(); //extra production effect of the active leader cards
           // warehouseDepots = game.getPlayerTurn(playerNumber).getPlayer().getPersonalBoard().getDeposit().getWarehouseDepots();
            cliSupporter.refresh();
            showCardBoard(game.getPlayerTurn(playerNumber).getPlayer());

            cliSupporter.printTitle("\n\nAVAILABLE RESOURCES");
            {
                if(game.getPlayerTurn(playerNumber).getPlayer().getPersonalBoard().getAvailableResources() == null)
                {
                    showStrongbox(playerNumber);
                    showWarehouse(warehouseDepots);
                }
                else
                    cliSupporter.printResources(game.getPlayerTurn(playerNumber).getPlayer().getPersonalBoard().getAvailableResources(), "real");
            }

            cliSupporter.printTitle("\n\nACTIVE LEADER CARDS");
            if(activeLeaders.size()>0)
                cliSupporter.printLeaderCards(activeLeaders);
            else
                System.out.println(ANSI_GREEN+"There are no active leader cards"+RESET);
            System.out.println();

            if(!actionDone)
            {
                System.out.println("\n1) Base production");
                System.out.println("2) Card production");
                if(extraProductionEffect.size()>0)
                {
                    System.out.println("3) Leader card production");
                    upperLimit++;
                }

                System.out.println("0) Exit");
                currentAction = cliSupporter.integerInput("Select action: ", 0, upperLimit);

                try {
                    switch (currentAction) {
                        case 1:
                            if(!temporaryAction1) {
                                int firstResource, secondResource, productionResult;

                                System.out.println(ANSI_GREEN + "\nYou can produce one generic resource (except faith) using 2 resources" + RESET);
                                System.out.println("1) Gold, 2) Servant, 3) Shield, 4) Stone");


                                firstResource = cliSupporter.integerInput("Select first resource (0 to exit): ", 0, 4);
                                if(firstResource == 0)
                                    continue;

                                secondResource = cliSupporter.integerInput("Select second resource (0 to exit): ", 0, 4);
                                if(secondResource == 0)
                                    continue;

                                productionResult = cliSupporter.integerInput("Select production result (0 to exit): ", 0, 4);
                                if(productionResult == 0)
                                    continue;

                                productionProd1(cliSupporter.numberToResourceType(firstResource),cliSupporter.numberToResourceType(secondResource),cliSupporter.numberToResourceType(productionResult));

                                temporaryAction1 = isSuccessReceived();
                                if(!temporaryAction1){
                                    endProduction();
                                    isSuccessReceived();
                                }
                            }
                            else
                            {
                                System.out.print(ANSI_GREEN+"You already used this production power, press enter to continue "+RESET);
                                input.readLine();
                            }
                            break;

                        case 2:
                            //if(!temporaryAction2) {
                                int currentCard;
                                DevelopmentCard[] developmentCards = game.getPlayerTurn(playerNumber).getPlayer().getPersonalBoard().getCardBoard().getUpperDevelopmentCards();

                                currentCard = cliSupporter.integerInput("Select card (1,2,3) (0 to exit): ", 0, 3) - 1;

                                if (currentCard == -1)
                                    continue;

                                if(developmentCards[currentCard] == null)
                                {
                                    System.out.print(ANSI_RED+"There is no development card in the selected position, press enter to continue "+RESET);
                                    input.readLine();
                                    continue;
                                }

                                if(!alreadyUsed[currentCard])
                                {
                                    alreadyUsed[currentCard] = true;
                                    productionProd2(currentCard);
                                    temporaryAction2 = isSuccessReceived();
                                    if(!temporaryAction2){
                                        endProduction();
                                        isSuccessReceived();
                                    }
                                }

                                else
                                {
                                    System.out.println(ANSI_GREEN+"You already produced with this card"+RESET);
                                    input.readLine();
                                    continue;
                                }
                            break;

                        case 3:
                            if(!temporaryAction3) {
                                int currentLeader, currentResource;

                                System.out.println("\nACTIVE LEADER CARDS");
                                if (activeLeaders.size() > 0) {
                                    cliSupporter.printLeaderCards(activeLeaders);
                                    System.out.println("\n1)Activate leader effect one");


                                    if (extraProductionEffect.size() > 1)
                                        System.out.println("2)Activate leader effect two");

                                } else
                                    System.out.println(ANSI_GREEN + "There are no active leader cards" + RESET);


                                currentLeader = cliSupporter.integerInput("Select action (0 to exit): ", 0, extraProductionEffect.size()) - 1;

                                if (currentLeader == -1)
                                    continue;


                                System.out.println(ANSI_GREEN + "\nYou can produce one of the following resources:" + RESET);
                                System.out.println("1)Gold, 2)Servant, 3)Shield, 4)Stone");
                                currentResource = cliSupporter.integerInput("Select resource: ", 1, 4);

                                productionProd3(extraProductionEffect.get(currentLeader).getProductionCost(),cliSupporter.numberToResourceType(currentResource));

                                temporaryAction3 = isSuccessReceived();
                                if(!temporaryAction3){
                                    endProduction();
                                    isSuccessReceived();
                                }
                            }
                            else
                            {
                                System.out.print(ANSI_GREEN+"You already used this production power, press enter to continue "+RESET);
                                input.readLine();
                            }
                            break;

                        case 0:
                            if(temporaryAction1 || temporaryAction2 || temporaryAction3){
                                actionDone = true;
                                endProduction();
                                isSuccessReceived();
                            }
                            break;
                    }
                }catch (IOException e)
                {
                    e.printStackTrace();
                }
            }

            else{
                System.out.println(ANSI_GREEN+"You already did a basic action"+RESET);
                System.out.println("0) Exit");
                currentAction = cliSupporter.integerInput("Select action : ", 0, 0);
            }


        }while(currentAction != 0);

    }


    /**
     * Method that shows the leader cards in the hand of an user
     * and asks wat to do with them, discard or play. It also sands
     * the answer to the server
     */
    private void showLeaderBoard() {
        int currentAction = 0;
        int currentCard;

        do {
            cliSupporter.refresh();
            LeaderBoard leaderBoard = game.getPlayerTurn(playerNumber).getPlayer().getPersonalBoard().getLeaderBoard();
            ArrayList<LeaderCard> leaderInHand = leaderBoard.getLeaderCardsInHand();
            ArrayList<LeaderCard> activeLeader = leaderBoard.getLeaderCards();

            if (leaderInHand.size() > 0 || activeLeader.size() > 0) {
                if (leaderInHand.size() > 0)
                    cliSupporter.printTitle("LEADER CARDS IN HAND\n");

                cliSupporter.printLeaderCards(leaderBoard.getLeaderCardsInHand());

                if (activeLeader.size() > 0)
                    cliSupporter.printTitle("\nACTIVE LEADER CARDS\n");

                cliSupporter.printLeaderCards(activeLeader);

                System.out.println();
                cliSupporter.showLegend();
                System.out.println(ANSI_BLUE + "🁢" + RESET + "1 : Color and number of a development card\n");

                if (!(!game.getPlayerTurn(playerNumber).isLeaderAction() && actionDone ||//&& game.getPlayerTurn(playerNumber).isAlreadyDone()||
                        game.getPlayerTurn(playerNumber).isHandlingSwap() ||
                        game.getPlayerTurn(playerNumber).isProducing()))
                {
                    if (leaderInHand.size() > 0) {
                        System.out.println("\n1) Play leader");
                        System.out.println("2) Discard leader");
                        System.out.println("0) Exit");
                        currentAction = cliSupporter.integerInput("Select action: ", 0, 2);
                    } else {
                        System.out.println(ANSI_GREEN + "\nYou activated or discarded all you leader cards" + RESET);
                        System.out.println("0) Exit");
                        currentAction = cliSupporter.integerInput("Select action: ", 0, 0);
                    }

                }

                else
                {
                    System.out.print(ANSI_GREEN+"You already did a leader card action, press enter to continue"+RESET);
                    try {
                        input.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                try {
                    switch (currentAction) {

                        case 1:
                            currentCard = cliSupporter.integerInput("Select card (0 to exit): ", 0, leaderInHand.size()) - 1;
                            if (currentCard == -1)
                                continue;
                            activateLeader(currentCard);
                            if(!isSuccessReceived())
                                continue;
                            break;

                        case 2:
                            currentCard = cliSupporter.integerInput("Select card (0 to exit): ", 0, leaderInHand.size()) - 1;
                            if (currentCard == -1)
                                continue;
                            discardLeader(currentCard);
                            if(!isSuccessReceived())
                                continue;
                            break;

                        case 0:
                            break;
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                System.out.print(ANSI_GREEN + "You discarded all your leader cards, press enter to continue " + RESET);
                try {
                    input.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }while (currentAction!=0);

    }


    /**
     * Method that allows the user to chose two leader cards at
     * the beginning of the game
     */
    private void choseLeaderCards() {
        Integer[] chose= new Integer[2];

        LeaderBoard leaderBoard = game.getPlayerTurn(playerNumber).getPlayer().getPersonalBoard().getLeaderBoard();

        ArrayList<LeaderCard> leaderCardsInHand = leaderBoard.getLeaderCardsInHand();

        System.out.println("LEADER CARDS");
        cliSupporter.showLegend();
        System.out.println(ANSI_BLUE + "🁢" + RESET+ "1 : Color and number of a development card\n");
        cliSupporter.printLeaderCards(leaderCardsInHand);

        chose[0] = cliSupporter.integerInput("Select first leader (1-4): ", 1,4) - 1;

        do {
            chose[1] = cliSupporter.integerInput("Select second leader (1-4): ", 1, 4)-1;
            if(chose[0].equals(chose[1]))
                System.out.println(ANSI_GREEN+"\nYou already selected this card\n"+RESET);
        }while(chose[0].equals(chose[1]));


        try
        {
            chooseLeader(chose);
            isSuccessReceived();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

    }


    //SUPPORT METHODS

    /**
     * Method that receives a message and check if it is a success or an error.
     * If  a success is received it also waits for the game update, if an error is received error message is printed
     * @return true or false depending on the message received: SUCCESS --> true, ERROR --> false
     */
    protected boolean isSuccessReceived() {
        Object message;

        message = waitAndGetResponse();

        if(message == ERROR)
        {
            message = waitAndGetResponse(); //receive the error message
            System.out.print(ANSI_RED);
            System.out.println(message); //prints the error message
            System.out.println("Press enter to continue "+RESET);

            try {
                input.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return false;
        }

        if(message == SUCCESS)
            waitForUpdatedGame();

        if(message == TURNEND)
        {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return true;
    }


    /**
     * Method that notifies the ending of a game
     * @param youWon says if the user is the winner or not
     */
    @Override
    protected synchronized void notifyEndGame(boolean youWon) {
        if(youWon) {
            System.out.println("\nYou won, "+ game.getPlayerTurn(playerNumber).getVictoryPoints());
            winOrLose = 1;
        }
        else {
            winOrLose = 0;
            System.out.println("\nYou lost, "+ game.getPlayerTurn(playerNumber).getVictoryPoints());
        }
    }


    @Override
    public synchronized void notifyTurnStarted() {
    }


    @Override
    public synchronized void notifyTurnEnded() {
    }

    @Override
    public void notifyDisconnection() {
        System.out.println(ANSI_RED+"\nA client has disconnected, exiting game"+RESET);
        System.exit(1);
    }


    /**
     * Depending on the number of the player allows the user to
     * select the initial resources they want and sends them to the server
     */
    private void selectInitialResources() {
        int currentAction, secondCurrentAction;

        cliSupporter.refresh();
        switch(playerNumber){
            case 0:
                break;

            case 1:
                System.out.println(ANSI_GREEN+"\nYou can have one resource"+RESET);
                System.out.println("1) Gold, 2) Servant, 3) Shield, 4) Stone");
                currentAction = cliSupporter.integerInput("Select resource: ", 1, 4);

                try{
                    setInitialResources(cliSupporter.numberToResourceType(currentAction));
                }catch (IOException e)
                {
                    e.printStackTrace();
                }

                break;

            case 2:
                System.out.println(ANSI_GREEN+"\nYou can have one resource and one faith point"+RESET);
                System.out.println("1) Gold, 2) Servant, 3) Shield, 4) Stone");
                currentAction = cliSupporter.integerInput("Select resource: ", 1, 4);
                try{
                    setInitialResources(cliSupporter.numberToResourceType(currentAction));
                }catch (IOException e)
                {
                    e.printStackTrace();
                }
                break;

            case 3:
                System.out.println(ANSI_GREEN+"\nYou can have two resources and one faith point"+RESET);
                System.out.println("1) Gold, 2) Servant, 3) Shield, 4) Stone");
                currentAction = cliSupporter.integerInput("Select first resource: ", 1, 4);
                secondCurrentAction = cliSupporter.integerInput("Select second resource: ", 1,4);
                try{
                    setInitialResources(cliSupporter.numberToResourceType(currentAction),cliSupporter.numberToResourceType(secondCurrentAction));
                }catch (IOException e)
                {
                    e.printStackTrace();
                }
                break;
        }
    }

}
