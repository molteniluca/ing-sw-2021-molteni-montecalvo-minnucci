package it.polimi.ingsw.view;


import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.board.personal.FaithTrack;
import it.polimi.ingsw.model.board.personal.storage.WarehouseDepots;
import it.polimi.ingsw.network.NetworkMessages;
import it.polimi.ingsw.network.ObjectUpdate;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import static it.polimi.ingsw.network.NetworkMessages.SUCCESS;

public abstract class View extends Thread{
    public final Queue<Object> messages = new LinkedList<>(); //List of received messages
    protected boolean gameUpdated =false;

    public abstract void initializeView();

    public abstract void welcomeInfo();

    public abstract void askCreateOrJoin();

    public abstract void askServerInfo();

    public abstract void askNickname();

    public abstract void showHomepage();

    public abstract void showFaithTrack(FaithTrack faithTrack);

    public abstract void showWarehouse(WarehouseDepots warehouseDepots);

    public abstract void showStrongbox(int showPlayer);

    protected abstract boolean isSuccessReceived();

    public void notifyResponse(Object o){
        synchronized (this) {
        if(o.getClass()== NetworkMessages.class)
            if(o==SUCCESS)
                gameUpdated =false;
            messages.add(o);
            this.notify();
        }
    }

    public void notifyNewGame(Game game){
        updateObjects(game);
    }

    public abstract void updateObjects(Game game);

    /**
     * Suspend the view thread in wait for a new game object
     */
    protected void waitForUpdatedGame() {
        while (!gameUpdated){
            synchronized (this){
                try{
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

    public abstract void notifyNewUpdate(ObjectUpdate read);
}
