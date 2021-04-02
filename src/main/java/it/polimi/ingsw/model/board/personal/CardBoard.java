package it.polimi.ingsw.model.board.personal;

import it.polimi.ingsw.model.cards.DevelopmentCard;
import jdk.jshell.spi.ExecutionControl;

import java.util.ArrayList;

public class CardBoard {
    private ArrayList<DevelopmentCard>[] productionCards;

    public ArrayList<DevelopmentCard> getDevelopmentCards()throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("NON IMPLEMENTATO");
    }

    public int getAllVictoryPoint()throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("NON IMPLEMENTATO");
    }
}
