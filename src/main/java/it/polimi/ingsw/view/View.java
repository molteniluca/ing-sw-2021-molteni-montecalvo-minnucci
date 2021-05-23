package it.polimi.ingsw.view;



import it.polimi.ingsw.controller.Game;

import java.util.LinkedList;
import java.util.Queue;

public abstract class View extends Thread{
    public final Queue<Object> messages = new LinkedList<>(); //List of received messages

    public abstract void initializeView();

    public abstract void welcomeInfo();

    public abstract void askCreateOrJoin();

    public abstract void askServerInfo();

    public abstract void askNickname();

    public abstract void showHomepage();

    public abstract void showFaithTrack();

    public abstract void showWarehouse();

    public abstract void showStrongbox();

    public void notifyResponse(Object o){
        messages.add(o);
        synchronized (this) {
            this.notify();
        }
    }

    public abstract void updateObjects(Game game);


    /**
     * Suspend the view thread if there are no messages,
     * otherwise return and remove the first object in the list of messages
     * @return the message or the object received
     */
    protected Object waitAndGetResponse() {
        synchronized (this){
            if(messages.size()==0) {
                try{
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return messages.remove();
    }
}
