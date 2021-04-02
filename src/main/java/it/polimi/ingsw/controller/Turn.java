package it.polimi.ingsw.controller;

import jdk.jshell.spi.ExecutionControl;

public class Turn {
    private Player[] players;
    private int currentPlayer;

    public Player getPlayers(int number) {
        return players[number];
    }

    public void beginTurn()throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("NON IMPLEMENTATO");
    }

    public void endTurn()throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("NON IMPLEMENTATO");
    }

}
