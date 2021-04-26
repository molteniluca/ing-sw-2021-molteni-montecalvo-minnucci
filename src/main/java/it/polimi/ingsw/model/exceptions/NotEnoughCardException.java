package it.polimi.ingsw.model.exceptions;

/**
 * Launched when there are not enough discardable cards in CardDealer
 * */
public class NotEnoughCardException extends Exception{
    public NotEnoughCardException(String s) {
        super(s);
    }
}
