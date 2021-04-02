package it.polimi.ingsw.model.board;

import it.polimi.ingsw.model.resources.Resources;
import jdk.jshell.spi.ExecutionControl;

public class FaithTrack {
    private int[] faithCards;
    private int position;

    public FaithTrack() {
        this.faithCards = new int[]{0,0,0};
        this.position = 1;
    }

    public int getVictoryPoint()throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("NON IMPLEMENTATO");
    }

    public int getPosition()throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("NON IMPLEMENTATO");
    }

    public void checkRelationship(int position)throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("NON IMPLEMENTATO");
    }

    public void incrementPosition(int increment)throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("NON IMPLEMENTATO");
    }

    public int[] getFaithCards()throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("NON IMPLEMENTATO");
    }
}
