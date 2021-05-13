package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Player;

import java.io.IOException;

import static java.lang.Thread.sleep;


/**
 * This class represents a turn, for each player there's a turn
 */
public class PlayerTurn implements Turn{
    private final Player player;

    public PlayerTurn(Player player){
        this.player = player;
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
        player.getClientHandler().sendObject("TurnBegin");
        try {
            sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        player.getClientHandler().sendObject("TurnEnd");
    }


}
