package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Player;

import java.io.IOException;
import java.io.Serializable;

import static it.polimi.ingsw.controller.NetworkMessages.*;
import static java.lang.Thread.sleep;


/**
 * This class represents a turn, for each player there's a turn
 */
public class PlayerTurn implements Turn, Serializable {
    private final Player player;
    private final ClientHandler clientHandler;

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
        clientHandler.sendObject(TURNBEGIN);
        try {
            //player.getClientHandler().refreshClientObjects();
            sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        clientHandler.sendObject(TURNEND);
    }


    public ClientHandler getClientHandler() {
        return clientHandler;
    }
}
