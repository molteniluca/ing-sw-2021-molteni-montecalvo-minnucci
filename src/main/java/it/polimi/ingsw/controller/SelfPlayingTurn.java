package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Lorenzo;
import it.polimi.ingsw.model.board.general.GeneralBoard;
import it.polimi.ingsw.model.exceptions.FaithOverflowException;
import it.polimi.ingsw.model.exceptions.NotEnoughCardException;

import java.io.Serializable;

/**
 * This class represents a Lorenzo The Magnificent turn
 */
public class SelfPlayingTurn implements Turn, Serializable {
    private static final long serialVersionUID = 6732146736278436273L;
    private final Lorenzo lorenzo;

    public SelfPlayingTurn(GeneralBoard generalBoard){
        lorenzo = new Lorenzo(generalBoard);
    }

    public void beginTurn() throws FaithOverflowException, NotEnoughCardException {
        lorenzo.play();
    }

    public void startGame(){
    }


    public int getVictoryPoints(){
        return lorenzo.getVictoryPoints();
    }

    public void endGame(boolean winner) {
    }
}
