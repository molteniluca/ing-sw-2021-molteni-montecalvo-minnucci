package it.polimi.ingsw.view;



import it.polimi.ingsw.controller.Game;

import java.util.LinkedList;
import java.util.Queue;

public abstract class View extends Thread{
    private final Queue<Object> messages = new LinkedList<>();

    public abstract void initializeView();

    public abstract void welcomeInfo();

    public abstract void askCreateOrJoin();

    public abstract void askServerInfo();

    public abstract void askNickname();

    public abstract void showHomepage();

    public abstract void showFaithTrack();

    public void notifyResponse(Object o){
        messages.add(o);
        synchronized (this) {
            this.notify();
        }
    }

    public void updateObjects(Game game){

    }

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
