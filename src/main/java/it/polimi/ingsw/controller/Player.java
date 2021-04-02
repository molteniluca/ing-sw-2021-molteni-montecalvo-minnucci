package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.board.general.GeneralBoard;

public class Player {
    private final String name;
    private boolean isConnected;
    private final boolean hasInkwell;
    private final PersonalBoard personalBoard;

    public Player(String name, boolean hasInkwell, GeneralBoard generalBoard) {
        this.name = name;
        this.hasInkwell = hasInkwell;
        this.personalBoard = new PersonalBoard(generalBoard);
    }

    public String getName() {
        return name;
    }

    public PersonalBoard getPersonalBoard() {
        return personalBoard;
    }

    public boolean isConnected() {
        return isConnected;
    }
}
