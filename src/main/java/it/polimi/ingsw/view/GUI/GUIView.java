package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.network.NetworkMessages;
import it.polimi.ingsw.view.GUI.Controllers.GameBoardController;
import it.polimi.ingsw.view.View;


public class GUIView extends View {
    public static GUIView singleton;
    public String lastErrorMessage;
    public GameBoardController gameBoardController;
    public boolean isMyTurn = false;

    /**
     * Method that receives a message and check if it is a success or an error.
     * If  a success is received it also waits for the game update, if an error is received error message is printed
     * @return true or false depending on the message received: SUCCESS --> true, ERROR --> false
     */
    @Override
    public boolean isSuccessReceived() {
        NetworkMessages message;

        message = (NetworkMessages) waitAndGetResponse();

        switch(message){
            case ERROR:
                lastErrorMessage = (String) waitAndGetResponse(); //receive the error message
                return false;
            case SUCCESS:
            case TURNEND:
                return true;
            default:
                return false;
        }
    }

    @Override
    protected void notifyEndGame(boolean youWon) {

    }

    @Override
    public synchronized void notifyTurnStarted() {
        while (gameBoardController==null){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        isMyTurn=true;
        gameBoardController.startTurn();
    }

    @Override
    public void notifyTurnEnded() {
        while (gameBoardController==null){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        isMyTurn=false;
        gameBoardController.endTurn();
    }

    public synchronized void registerStage(GameBoardController gameBoardController){
        this.gameBoardController=gameBoardController;
        notifyAll();
    }

    public static GUIView singleton(){
        if(singleton == null){
            singleton = new GUIView();
        }
        return singleton;
    }
}
