package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.exceptions.FaithOverflowException;
import it.polimi.ingsw.model.exceptions.WinException;

import java.io.IOException;

public interface Turn {
    void beginTurn() throws IOException, FaithOverflowException, WinException;
}
