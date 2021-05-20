package it.polimi.ingsw.view;

//used to implement an observable interface

import it.polimi.ingsw.controller.Game;

import java.util.LinkedList;
import java.util.Queue;

public abstract class View extends Thread{
    private final Queue<Object> messsages= new LinkedList<>();

    public abstract void welcomeInfo();

    public abstract void askCreateOrJoin();

    public abstract void askServerInfo();

    public abstract void askNickname();

    public void notifyResponse(Object o){
        messsages.add(o);
        this.notify();
    }

    public void updateObjects(Game game){

    }

    protected Object waitAndGetResponse() {
        if(messsages.size()==0) {
            synchronized (this){
                try{
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return messsages.remove();
    }
}
