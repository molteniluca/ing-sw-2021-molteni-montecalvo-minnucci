package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.exceptions.FaithOverflowException;
import it.polimi.ingsw.model.exceptions.NotEnoughCardException;
import it.polimi.ingsw.model.exceptions.WinException;

import java.io.IOException;
import java.io.Serializable;

public interface Turn extends Serializable {
    long serialVersionUID = 6732146736278436271L;

    void beginTurn() throws IOException, FaithOverflowException, WinException, NotEnoughCardException;

    void startGame() throws IOException;

    int getVictoryPoints();

    void endGame(boolean winner) throws IOException;
}
