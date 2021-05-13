package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.board.general.ActionTokens;
import it.polimi.ingsw.model.board.general.Lorenzo;
import jdk.jshell.spi.ExecutionControl;

public class SelfPlayingTurn implements Turn{
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

}
