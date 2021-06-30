package it.polimi.ingsw.view;

import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.model.resources.ResourceTypes;
import it.polimi.ingsw.network.NetworkMessages;
import it.polimi.ingsw.network.ObjectUpdate;
import it.polimi.ingsw.network.exceptions.FullRoomException;
import it.polimi.ingsw.view.exceptions.UnknownIdException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import static it.polimi.ingsw.network.NetworkMessages.*;


/**
 * Class that represents a view of the game on the server
 */
public abstract class View {
    private NetworkHandler networkHandler;
    public Game game;
    public int playerNumber; //The number of the player

    public final Queue<Object> messages = new LinkedList<>(); //List of received messages
    protected boolean gameUpdated = false;

    /**
     * Method that closes connection with the server
     */
    public void closeConnection() {
        networkHandler.closeConnection();
    }

    /**
     * Method that notifies a response to this view
     * @param o The response
     */
    public synchronized void notifyResponse(Object o) {
        if (o.getClass() == NetworkMessages.class)
            if (o == SUCCESS)
                gameUpdated = false;
        messages.add(o);
        this.notifyAll();
    }

    /**
     * Method that notifies this object of a new game
     * @param game The new game
     */
    public void notifyNewGame(Game game) {
        updateObjects(game);
    }

    /**
     * Method that sets the game when it is updated. Called by the NetworkHandler if
     * a game object is received
     *
     * @param game the new game received from the server
     */
    public synchronized void updateObjects(Game game) {
        if(this.game != null)
            game.getPlayerTurn(playerNumber).getPlayer().getPersonalBoard().getLeaderBoard().setLeaderCardsInHand(
                    this.game.getPlayerTurn(playerNumber).getPlayer().getPersonalBoard().getLeaderBoard().getLeaderCardsInHand()
            );
        this.game = game;
    }

    /**
     * Suspend the view thread in wait for a new game object
     */
    public synchronized void waitForUpdatedGame() {
        while (!gameUpdated) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Suspend the view thread if there are no messages,
     * otherwise return and remove the first object in the list of messages
     *
     * @return the message or the object received
     */
    protected synchronized Object waitAndGetResponse() {
        while (messages.size() == 0) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return messages.remove();
    }

    /**
     * Method that notifies this view of a new update
     * @param read The update
     */
    public synchronized void notifyNewUpdate(ObjectUpdate read) {
        switch (read.getUpdateType()){
            case LEADERCARDS:
                game.getPlayerTurn(read.getPlayer()).getPlayer().getPersonalBoard().getLeaderBoard()
                        .setLeaderCardsInHand((ArrayList<LeaderCard>) read.getObject());
                break;
            default:
                System.out.println("UNSUPPORTED UPDATE");
        }
        gameUpdated = true;
        this.notifyAll();
    }

    /**
     * Method that sets the initial resources
     * @param res1 The first and only selectable resource
     * @throws IOException In case there's a problem with the communication
     */
    public void setInitialResources(ResourceTypes res1) throws IOException {
        networkHandler.sendObject(res1);
    }

    /**
     * Method that sets the initial resources
     * @param res1 The first resource
     * @param res2 The second resource
     * @throws IOException In case there's a problem with the communication
     */
    public void setInitialResources(ResourceTypes res1, ResourceTypes res2) throws IOException {
        networkHandler.sendObject(res1);
        networkHandler.sendObject(res2);
    }

    /**
     * Choose the initial leader cards
     * @param chose The selected leader cards
     * @throws IOException In case there's a problem with the communication
     */
    public void chooseLeader(Integer[] chose) throws IOException {
        networkHandler.sendObject(chose);
    }

    /**
     * Method to discard a leader card
     * @param card The card to be discarded
     * @throws IOException In case there's a problem with the communication
     */
    public void discardLeader(int card) throws IOException {
        networkHandler.sendObject(DISCARDLEADER);
        networkHandler.sendObject(card);
    }

    /**
     * Method to activate a leader card
     * @param card The card to be activated
     * @throws IOException In case there's a problem with the communication
     */
    public void activateLeader(int card) throws IOException {
        networkHandler.sendObject(ACTIVATELEADER);
        networkHandler.sendObject(card);
    }

    /**
     * Method to end the production
     * @throws IOException In case there's a problem with the communication
     */
    public void endProduction() throws IOException {
        networkHandler.sendObject(ENDPRODUCTION);
    }

    /**
     * Method that uses the default production
     * @param res1 The first input resource
     * @param res2 The second input resource
     * @param output The output resource
     * @throws IOException In case there's a problem with the communication
     */
    public void productionProd1(ResourceTypes res1, ResourceTypes res2, ResourceTypes output) throws IOException {
        networkHandler.sendObject(PRODUCTION);
        networkHandler.sendObject(PROD1);
        networkHandler.sendObject(res1);
        networkHandler.sendObject(res2);
        networkHandler.sendObject(output);
    }

    /**
     * Method that uses a card production
     * @param currentCard The card to use in this production
     * @throws IOException In case there's a problem with the communication
     */
    public void productionProd2(int currentCard) throws IOException {
        networkHandler.sendObject(PRODUCTION);
        networkHandler.sendObject(PROD2);
        networkHandler.sendObject(currentCard);
    }

    /**
     * Method that uses a leader production
     * @param input The input resource
     * @param output The output resource
     * @throws IOException In case there's a problem with the communication
     */
    public void productionProd3(ResourceTypes input, ResourceTypes output) throws IOException {
        networkHandler.sendObject(PRODUCTION);
        networkHandler.sendObject(PROD3);
        networkHandler.sendObject(input);
        networkHandler.sendObject(output);
    }

    /**
     * Method that buys a card from the card dealer
     * @param row The row of the card
     * @param column The column of the card
     * @param place The place to insert the card
     * @throws IOException In case there's a problem with the communication
     */
    public void marketBuyCard(int row, int column, int place) throws IOException {
        networkHandler.sendObject(BUYCARD);
        networkHandler.sendObject(row);
        networkHandler.sendObject(column);
        networkHandler.sendObject(place);
    }

    /**
     * Method that moves a level to the swap
     * @param level The level to be moved
     * @throws IOException In case there's a problem with the communication
     */
    public void swapMoveToSwap(int level) throws IOException {
        networkHandler.sendObject(MOVETOSWAP);
        networkHandler.sendObject(level);
    }

    /**
     * Method that moves resources to the level
     * @param level The level to move the resources
     * @param resourceTypesToMove The type of resources
     * @param numResToMove The number of resources
     * @throws IOException In case there's a problem with the communication
     */
    public void swapMoveToLevel(int level, ResourceTypes resourceTypesToMove, int numResToMove) throws IOException {
        networkHandler.sendObject(MOVETOLEVEL);
        networkHandler.sendObject(level);
        networkHandler.sendObject(resourceTypesToMove);
        networkHandler.sendObject(numResToMove);
    }

    /**
     * Method that drop the swap resources
     * @throws IOException In case there's a problem with the communication
     */
    public void swapDropResources() throws IOException {
        networkHandler.sendObject(DROPRESOURCES);
    }

    /**
     * Method that buys a column from the market
     * @param column The column to buy
     * @param extraResourceIndex The extra resource effect
     * @throws IOException In case there's a problem with the communication
     */
    public void marketBuyColumn(int column, int extraResourceIndex) throws IOException {
        networkHandler.sendObject(BUYCOLUMN);
        networkHandler.sendObject(column);
        networkHandler.sendObject(extraResourceIndex);
    }

    /**
     * Method that buys a row from the market
     * @param row The row to buy
     * @param extraResourceIndex The extra resource effect
     * @throws IOException In case there's a problem with the communication
     */
    public void marketBuyRow(int row, int extraResourceIndex) throws IOException {
        networkHandler.sendObject(BUYROW);
        networkHandler.sendObject(row);
        networkHandler.sendObject(extraResourceIndex);
    }

    /**
     * Method that sends the nickname to the server
     * @param nickname The nickname
     * @throws IOException In case there's a problem with the communication
     * @throws UnknownIdException In case the room doesn't exist
     * @throws FullRoomException In case the room is full
     */
    public void sendNickname(String nickname) throws IOException, UnknownIdException, FullRoomException {
        networkHandler.sendObject(nickname);
        Object o = waitAndGetResponse();
        if(o.getClass() == NetworkMessages.class){
            switch ((NetworkMessages)o){
                case FULLROOMERROR:
                    throw new FullRoomException("Trying to join a full room");
                case UNKNOWNIDERROR:
                    throw new UnknownIdException("Trying to join a non existent room");
            }
        }else {
            playerNumber = (int) o;
        }
    }

    /**
     * Method that starts a connection with the server
     * @param serverAddress The address of the server
     * @param serverPort The port of the server
     * @throws IOException In case there's a problem with the communication
     */
    public void startConnection(String serverAddress, int serverPort) throws IOException {
        networkHandler = new NetworkHandler(serverAddress,serverPort,this);
        networkHandler.start();
    }

    /**
     * Method that joins a game
     * @param roomId The room id of the game to join
     * @throws IOException In case there's a problem with the communication
     */
    public void joinGame(String roomId) throws IOException {
        networkHandler.sendObject(JOINGAME);
        networkHandler.sendObject(roomId);
    }

    /**
     * Method that creates a game
     * @param numberOfPlayers The number of players of this game
     * @throws IOException In case there's a problem with the communication
     */
    public void createGame(int numberOfPlayers) throws IOException {
        networkHandler.sendObject(CREATEGAME);
        networkHandler.sendObject(numberOfPlayers);
    }

    /**
     * Ends the turn
     * @throws IOException In case there's a problem with the communication
     */
    public void endTurn() throws IOException {
        networkHandler.sendObject(TURNEND);
    }

    /**
     * Method that handles the server messages
     * @return True if is success and false if error
     */
    protected abstract boolean isSuccessReceived();

    /**
     * Method that notifies this object of a victory
     * @param youWon True if you won and false if not
     */
    protected abstract void notifyEndGame(boolean youWon);

    /**
     * Method that notifies this object that the turn has started
     */
    public abstract void notifyTurnStarted();

    /**
     * Method that notifies this object that the turn has ended
     */
    public abstract void notifyTurnEnded();

    /**
     * Method that notifies this object that the server has disconnected
     */
    public abstract void notifyDisconnection();
}
