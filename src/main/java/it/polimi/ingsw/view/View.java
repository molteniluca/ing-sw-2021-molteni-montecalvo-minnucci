package it.polimi.ingsw.view;


import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.model.resources.ResourceTypes;
import it.polimi.ingsw.network.NetworkMessages;
import it.polimi.ingsw.network.ObjectUpdate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import static it.polimi.ingsw.network.NetworkMessages.*;

public abstract class View extends Thread {
    private NetworkHandler networkHandler;
    public Game game;
    public int playerNumber; //the number of the player received before GAMESTARTED

    public final Queue<Object> messages = new LinkedList<>(); //List of received messages
    protected boolean gameUpdated = false;

    protected abstract boolean isSuccessReceived();

    public void closeConnection() {
        networkHandler.closeConnection();
    }

    public void notifyResponse(Object o) {
        synchronized (this) {
            if (o.getClass() == NetworkMessages.class)
                if (o == SUCCESS)
                    gameUpdated = false;
            messages.add(o);
            this.notify();
        }
    }

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
        this.game = game;
        //notify(); wakes up the thread that was waiting for the game
        //gameUpdated = true;
    }

    /**
     * Suspend the view thread in wait for a new game object
     */
    public void waitForUpdatedGame() {
        while (!gameUpdated) {
            synchronized (this) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Suspend the view thread if there are no messages,
     * otherwise return and remove the first object in the list of messages
     *
     * @return the message or the object received
     */
    protected Object waitAndGetResponse() {
        synchronized (this) {
            while (messages.size() == 0) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return messages.remove();
        }
    }

    protected abstract void notifyEndGame(boolean youWon);

    public void notifyNewUpdate(ObjectUpdate read) {
        switch (read.getUpdateType()){
            case LEADERCARDS:
                game.getPlayerTurn(read.getPlayer()).getPlayer().getPersonalBoard().getLeaderBoard()
                        .setLeaderCardsInHand((ArrayList<LeaderCard>) read.getObject());
                break;
            default:
                System.out.println("UNSUPPORTED UPDATE");
        }
        gameUpdated =true;
        synchronized (this) {
            this.notify();
        }
    }


    public void setInitialResources(ResourceTypes res1) throws IOException {
        networkHandler.sendObject(res1);
    }

    public void setInitialResources(ResourceTypes res1, ResourceTypes res2) throws IOException {
        networkHandler.sendObject(res1);
        networkHandler.sendObject(res2);
    }

    public void chooseLeader(Integer[] chose) throws IOException {
        networkHandler.sendObject(chose);
    }

    public void discardLeader(int currentCard) throws IOException {
        networkHandler.sendObject(currentCard);
    }

    public void activateLeader(int currentCard) throws IOException {
        networkHandler.sendObject(ACTIVATELEADER);
        networkHandler.sendObject(currentCard);
    }

    public void endProduction() throws IOException {
        networkHandler.sendObject(ENDPRODUCTION);
    }

    public void productionProd1(ResourceTypes res1, ResourceTypes res2, ResourceTypes res3) throws IOException {
        networkHandler.sendObject(PRODUCTION);
        networkHandler.sendObject(PROD1);
        networkHandler.sendObject(res1);
        networkHandler.sendObject(res2);
        networkHandler.sendObject(res3);
    }

    public void productionProd2(int currentCard) throws IOException {
        networkHandler.sendObject(PRODUCTION);
        networkHandler.sendObject(PROD2);
        networkHandler.sendObject(currentCard);
    }

    public void productionProd3(ResourceTypes res1, ResourceTypes res2) throws IOException {
        networkHandler.sendObject(PRODUCTION);
        networkHandler.sendObject(PROD3);
        networkHandler.sendObject(res1);
        networkHandler.sendObject(res2);
    }

    public void marketBuyCard(int row, int column, int place) throws IOException {
        networkHandler.sendObject(BUYCARD);
        networkHandler.sendObject(row);
        networkHandler.sendObject(column);
        networkHandler.sendObject(place);
    }

    public void swapMoveToSwap(int level) throws IOException {
        networkHandler.sendObject(MOVETOSWAP);
        networkHandler.sendObject(level);
    }

    public void swapMoveToLevel(int level, ResourceTypes resourceTypesToMove, int numResToMove) throws IOException {
        networkHandler.sendObject(MOVETOLEVEL);
        networkHandler.sendObject(level);
        networkHandler.sendObject(resourceTypesToMove);
        networkHandler.sendObject(numResToMove);
    }

    public void swapDropResources() throws IOException {
        networkHandler.sendObject(DROPRESOURCES);
    }

    public void marketBuyColumn(int column, int extraResourceIndex) throws IOException {
        networkHandler.sendObject(BUYCOLUMN);
        networkHandler.sendObject(column);
        networkHandler.sendObject(extraResourceIndex);
    }

    public void marketBuyRow(int row, int extraResourceIndex) throws IOException {
        networkHandler.sendObject(BUYROW);
        networkHandler.sendObject(row);
        networkHandler.sendObject(extraResourceIndex);
    }

    public void sendNickname(String nickname) throws IOException {
        networkHandler.sendObject(nickname);
        playerNumber = (int) waitAndGetResponse();
    }

    public void startConnection(String serverAddress, int serverPort) throws IOException {
        networkHandler = new NetworkHandler(serverAddress,serverPort,this);
        networkHandler.start();
    }

    public void joinGame(String roomId) throws IOException {
        networkHandler.sendObject(JOINGAME);
        networkHandler.sendObject(roomId);
    }

    public void createGame(int numberOfPlayers) throws IOException {
        networkHandler.sendObject(CREATEGAME);
        networkHandler.sendObject(numberOfPlayers);
    }

    public void endTurn() throws IOException {
        networkHandler.sendObject(TURNEND);
    }
}
