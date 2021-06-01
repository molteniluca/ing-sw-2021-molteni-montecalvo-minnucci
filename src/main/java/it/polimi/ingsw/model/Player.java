package it.polimi.ingsw.model;

import it.polimi.ingsw.model.board.general.GeneralBoard;
import it.polimi.ingsw.model.board.personal.PersonalBoard;
import it.polimi.ingsw.model.cards.LeaderCard;

import java.io.Serializable;
import java.util.ArrayList;

public class Player implements Serializable {
    private static final long serialVersionUID = 6732146736278436277L;
    private final String name;
    private final boolean hasInkwell;
    private final PersonalBoard personalBoard;

    public Player(String name, boolean hasInkwell, GeneralBoard generalBoard, ArrayList<LeaderCard> leaderCards) {
        this.name = name;
        this.hasInkwell = hasInkwell;
        this.personalBoard = new PersonalBoard(generalBoard, leaderCards);
    }

    public String getName() {
        return name;
    }

    public PersonalBoard getPersonalBoard() {
        return personalBoard;
    }

    public boolean isHasInkwell() {
        return hasInkwell;
    }
}
