package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.exceptions.FaithOverflowException;

import java.io.IOException;

public interface Turn {
    void beginTurn() throws IOException, FaithOverflowException;
}
