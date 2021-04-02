package it.polimi.ingsw.model.exceptions;

public class DuplicateResourceException extends Exception{
    public DuplicateResourceException() {
    }

    public DuplicateResourceException(String message) {
        super(message);
    }
}
