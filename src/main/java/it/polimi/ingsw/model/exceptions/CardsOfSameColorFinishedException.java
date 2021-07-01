package it.polimi.ingsw.model.exceptions;

/**
 * Launched when there are not enough cards that can be discarded in CardDealer
 * */
public class CardsOfSameColorFinishedException extends Exception{
    public CardsOfSameColorFinishedException(String s) {
        super(s);
    }
}
