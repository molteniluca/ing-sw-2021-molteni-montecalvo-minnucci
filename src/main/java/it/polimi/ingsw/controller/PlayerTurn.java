package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Player;

import java.io.IOException;

public class PlayerTurn implements Turn{
    private final Player player;

    public PlayerTurn(Player player){
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public void beginTurn() throws IOException {
        player.getClientHandler().sendObject("StartingTurn");
    }


}
