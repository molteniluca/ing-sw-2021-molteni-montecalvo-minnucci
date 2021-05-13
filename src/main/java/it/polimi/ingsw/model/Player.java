package it.polimi.ingsw.model;

import it.polimi.ingsw.controller.ClientHandler;
import it.polimi.ingsw.model.board.general.GeneralBoard;
import it.polimi.ingsw.model.board.personal.PersonalBoard;
import it.polimi.ingsw.model.cards.LeaderCard;

import java.util.ArrayList;

public class Player {
    private final String name;
    private final ClientHandler clientHandler;
    private final boolean hasInkwell;
    private final PersonalBoard personalBoard;

    public Player(String name, boolean hasInkwell, GeneralBoard generalBoard, ClientHandler clientHandler, ArrayList<LeaderCard> leaderCards) {
        this.name = name;
        this.hasInkwell = hasInkwell;
        this.personalBoard = new PersonalBoard(generalBoard, leaderCards);
        this.clientHandler = clientHandler;
    }

    public String getName() {
        return name;
    }

    public PersonalBoard getPersonalBoard() {
        return personalBoard;
    }

    public ClientHandler getClientHandler() {
        return clientHandler;
    }

    public boolean isHasInkwell() {
        return hasInkwell;
    }
}
