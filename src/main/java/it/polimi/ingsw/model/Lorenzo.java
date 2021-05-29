package it.polimi.ingsw.model;

import it.polimi.ingsw.model.board.general.ActionTokens;
import it.polimi.ingsw.model.board.general.GeneralBoard;
import it.polimi.ingsw.model.board.personal.FaithTrack;
import it.polimi.ingsw.model.exceptions.FaithOverflowException;
import it.polimi.ingsw.model.exceptions.NotEnoughCardException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Class that represents lorenzo (the single player game opponent)
 */
public class Lorenzo implements Serializable {
    private final FaithTrack faithTrack;
    private int positionToken;
    private final ArrayList<ActionTokens> actionTokens;
    private final GeneralBoard generalBoard;

    public Lorenzo(GeneralBoard generalBoard) {
        faithTrack = new FaithTrack(generalBoard.getFaithObserver());
        this.generalBoard = generalBoard;
        positionToken = 0;
        actionTokens = new ArrayList<>();
        this.actionTokens.addAll(Arrays.asList(ActionTokens.values()));
        this.actionTokens.add(ActionTokens.DOUBLEFAITH);
        Collections.shuffle(this.actionTokens);
    }

    public FaithTrack getFaithTrack() {
        return faithTrack;
    }

    /**
     * This method plays lorenzo's turn
     * @throws FaithOverflowException In case the game has ended
     * @throws NotEnoughCardException In case the game has ended
     */
    public void play() throws FaithOverflowException, NotEnoughCardException {
        switch (actionTokens.get(positionToken++)){
            case DOUBLEFAITH:
                faithTrack.incrementPosition(2);
                break;
            case BLUE:
                generalBoard.getCardDealer().discardDevelopment('b');
                break;
            case GREEN:
                generalBoard.getCardDealer().discardDevelopment('g');
                break;
            case PURPLE:
                generalBoard.getCardDealer().discardDevelopment('p');
                break;
            case YELLOW:
                generalBoard.getCardDealer().discardDevelopment('y');
                break;
            case SHUFFLEFAITH:
                faithTrack.incrementPosition(1);
                Collections.shuffle(this.actionTokens);
                positionToken=0;
                break;
        }
    }

    /**
     * This method gets the victory points form this player
     * @return The victory points
     */
    public int getVictoryPoints() {
        return faithTrack.getVictoryPoint();
    }
}
