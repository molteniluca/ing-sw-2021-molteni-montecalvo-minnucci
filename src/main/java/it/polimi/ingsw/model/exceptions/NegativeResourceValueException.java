package it.polimi.ingsw.model.exceptions;

public class NegativeResourceValueException extends Exception{
    public NegativeResourceValueException() {
    }

    public NegativeResourceValueException(String message) {
        super(message);
    }
}
