package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.network.NetworkMessages;
import it.polimi.ingsw.network.ObjectUpdate;
import it.polimi.ingsw.view.GUI.Controllers.Board.GameBoardController;
import it.polimi.ingsw.view.GUI.Controllers.DisconnectController;
import it.polimi.ingsw.view.View;

import java.io.IOException;

import static it.polimi.ingsw.network.NetworkMessages.*;

/**
 * Class that represents a view of the server for a GUI
 */
public class GUIView extends View {
    public static GUIView singleton;
    public String lastErrorMessage;
    public GameBoardController gameBoardController;
    public boolean isMyTurn = false;
    public DisconnectController disconnectController;

    /**
     * Private constructor for singleton purposes
     */
    private GUIView(){

    }

    /**
     * Method that handles the server messages and notifies the GUI in case of an error
     * @return True if is success and false if error
     */
    @Override
    public boolean isSuccessReceived() {
        NetworkMessages message;

        message = (NetworkMessages) waitAndGetResponse();

        switch(message){
            case ERROR:
                lastErrorMessage = (String) waitAndGetResponse(); //receive the error message
                gameBoardController.showError(lastErrorMessage);
                return false;
            case SUCCESS:
                return true;
            default:
                return false;
        }
    }

    /**
     * Method that notifies this object of a victory
     * @param youWon True if you won and false if not
     */
    @Override
    protected void notifyEndGame(boolean youWon) {
        gameBoardController.handleGameEnd(youWon);
    }

    /**
     * Method that notifies this object that the turn has started
     */
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

    /**
     * Method that notifies this object that the turn has ended
     */
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

    /**
     * Method that notifies this object that the server has disconnected
     */
    @Override
    public void notifyDisconnection() {
        if(this.gameBoardController!=null)
            this.gameBoardController.handleDisconnect();
        if(this.disconnectController!=null)
            this.disconnectController.notifyDisconnect();
    }

    /**
     * Method used to register the main controller for notification purposes
     * @param gameBoardController The main controller
     */
    public synchronized void registerStage(GameBoardController gameBoardController){
        if(this.disconnectController != null)
            this.disconnectController = null;
        this.gameBoardController=gameBoardController;
        notifyAll();
    }

    /**
     * Singleton method
     * @return The object instance
     */
    public static GUIView singleton(){
        if(singleton == null){
            singleton = new GUIView();
        }
        return singleton;
    }

    /**
     * Method that creates a game and gets the game id
     * @param numberOfPlayers The number of players
     * @return The game id
     * @throws IOException In case there's a problem with the communication
     */
    public String createGameAndGetId(int numberOfPlayers) throws IOException {
        super.createGame(numberOfPlayers);
        if(isSuccessReceived())
            return (String) waitAndGetResponse();
        else
            throw new IOException();
    }

    /**
     * Choose the initial leader cards
     * @param chose The leader selected
     * @return True if successful and false if not
     * @throws IOException In case there's a problem with the communication
     */
    public boolean chooseLeaderAndWaitForStart(Integer[] chose) throws IOException {
        super.chooseLeader(chose);
        if(isSuccessReceived()) {
            return waitAndGetResponse() == GAMESTARTED;
        }
        return false;
    }

    /**
     * Method that waits for a new response from the server
     * @return The response object
     */
    protected Object waitAndGetResponse(){
        Object ret = super.waitAndGetResponse();
        if(ret==TURNBEGIN) //Ignore notification messages (Which are useful for cli)
            return waitAndGetResponse();
        else
            return ret;
    }

    /**
     * Method that notifies this view of a new update
     * @param read The update
     */
    @Override
    public void notifyNewUpdate(ObjectUpdate read) {
        super.notifyNewUpdate(read);
        if(gameBoardController!=null)
            gameBoardController.notifyUpdate();
    }

    /**
     * Method that registers a listener for disconnection
     * @param disconnectController The controller to be notified
     */
    public void registerDisconnectListener(DisconnectController disconnectController){
        this.disconnectController = disconnectController;
    }
}
