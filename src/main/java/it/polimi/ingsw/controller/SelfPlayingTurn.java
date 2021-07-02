package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Lorenzo;
import it.polimi.ingsw.model.board.general.GeneralBoard;
import it.polimi.ingsw.model.exceptions.FaithOverflowException;
import it.polimi.ingsw.model.exceptions.CardsOfSameColorFinishedException;

import java.io.Serializable;

/**
 * This class represents a Lorenzo The Magnificent turn
 */
public class SelfPlayingTurn implements Turn, Serializable {
    private static final long serialVersionUID = 6732146736278436273L;
    private final Lorenzo lorenzo;

    /**
     * Constructor of the class
     * @param generalBoard The general board of the game in witch lorenzo is playing
     */
    public SelfPlayingTurn(GeneralBoard generalBoard){
        lorenzo = new Lorenzo(generalBoard);
    }

    /**
     * Begins the turn for lorenzo
     * @throws FaithOverflowException In case lorenzo has reached the end of the faith track
     * @throws CardsOfSameColorFinishedException In case lorenzo has drawn all the cards of a same color
     */
    public void beginTurn() throws FaithOverflowException, CardsOfSameColorFinishedException {
        lorenzo.play();
    }

    /**
     * Method that starts the game for lorenzo
     */
    public void startGame(){

    }

    /**
     * Method that returns the victory points for lorenzo
     * @return The victory points
     */
    public int getVictoryPoints(){
        return lorenzo.getVictoryPoints();
    }

    /**
     * Method that ends the game for lorenzo
     * @param winner True if this player wins and false if not
     */
    public void endGame(boolean winner) {

    }

    /**
     * Method that forces a turn end for lorenzo
     */
    @Override
    public void endTurn() {

    }

    /**
     * Method that returns lorenzo
     * @return Lorenzo's object
     */
    public Lorenzo getLorenzo(){return lorenzo;}
}
