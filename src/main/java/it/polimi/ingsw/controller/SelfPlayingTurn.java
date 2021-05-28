package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.board.general.ActionTokens;
import it.polimi.ingsw.model.Lorenzo;
import jdk.jshell.spi.ExecutionControl;

import java.io.Serializable;

public class SelfPlayingTurn implements Turn, Serializable {
    private Lorenzo lorenzo;

    public void beginTurn(){

    }

    private ActionTokens drawToken()throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("NON IMPLEMENTATO");
    }

    private void discardDevelopment(ActionTokens actionTokens)throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("NON IMPLEMENTATO");
    }

    private void incrementFaithPosition(int increment)throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("NON IMPLEMENTATO");
    }

    private void shuffle()throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("NON IMPLEMENTATO");
    }

    @Override
    public void startGame(){

    }

    @Override
    public int getVictoryPoints(){
        return 0;
    }

    @Override
    public void endGame(boolean winner) {

    }
}
