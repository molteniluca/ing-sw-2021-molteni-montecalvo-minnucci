package it.polimi.ingsw.model.exceptions;

public class NegativeResourceValueException extends Exception{
    public NegativeResourceValueException() {
        super();
    }

    public NegativeResourceValueException(String message) {
        super(message);
    }
}
