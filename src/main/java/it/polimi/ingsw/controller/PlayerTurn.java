package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Player;

import java.io.IOException;
import java.io.Serializable;

import static it.polimi.ingsw.controller.NetworkMessages.*;


/**
 * This class represents a turn, for each player there's a turn
 */
public class PlayerTurn implements Turn, Serializable {
    private final Player player;
    private final transient ClientHandler clientHandler;

    public PlayerTurn(Player player, ClientHandler clientHandler){
        this.player = player;
        this.clientHandler = clientHandler;
    }

    public Player getPlayer() {
        return player;
    }

    /**
     * This method begins a turn for this player
     * @throws IOException In case the client disconnects
     */
    @Override
    public void beginTurn() throws IOException {
        boolean leaderAction = false;
        clientHandler.sendObject(TURNBEGIN);

        clientHandler.refreshClientObjects();

        NetworkMessages action = clientHandler.receiveObject(NetworkMessages.class);
        if(action==ACTIVATELEADER){
            leaderAction=true;
            activateLeader();
            action = clientHandler.receiveObject(NetworkMessages.class);
        }
        switch(action){
            case ACTIVATEPRODUCTION:
                activateProduction();
                break;
            case BUYRESOURCES:
                buyResources();
                break;
            case BUYCARD:
                buyDevelopmentCard();
                break;
        }

        action = clientHandler.receiveObject(NetworkMessages.class);
        if(action==ACTIVATELEADER && !leaderAction){
            activateLeader();
            action = clientHandler.receiveObject(NetworkMessages.class);
        }
        while (action!=TURNEND){
            clientHandler.sendObject(ERROR);
            clientHandler.sendObject("You can only end the turn");
            action = clientHandler.receiveObject(NetworkMessages.class);
        }

        clientHandler.sendObject(TURNEND);
    }

    private void activateLeader(){

    }

    private void activateProduction(){

    }

    private void buyResources(){

    }

    private void buyDevelopmentCard(){

    }

    public ClientHandler getClientHandler() {
        return clientHandler;
    }
}
