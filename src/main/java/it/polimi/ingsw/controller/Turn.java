package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.exceptions.FaithOverflowException;
import it.polimi.ingsw.model.exceptions.CardsOfSameColorFinishedException;
import it.polimi.ingsw.model.exceptions.WinException;
import it.polimi.ingsw.model.resources.Resources;

import java.io.IOException;
import java.io.Serializable;

public interface Turn extends Serializable {
    long serialVersionUID = 6732146736278436271L;

    /**
     * This method begins this turn
     */
    void beginTurn() throws IOException, FaithOverflowException, WinException, CardsOfSameColorFinishedException;

    /**
     * This method is used to initialize the game for this turn
     */
    void startGame() throws IOException;

    /**
     * This method calculates the victory points
     * @return The victory points associated with the player
     */
    int getVictoryPoints();

    /**
     * This method ends the game for this turn
     * @param winner True if this player wins and false if not
     */
    void endGame(boolean winner) throws IOException;

    /**
     * This method is used to force the end of a turn caused by a win
     */
    void endTurn() throws IOException;

    /**
     * Method that returns the total resources of a player
     * @return The total resources of this player (0 for lorenzo)
     */
    Resources getTotalResources();
}
