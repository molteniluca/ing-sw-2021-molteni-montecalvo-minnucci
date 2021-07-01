package it.polimi.ingsw.model;

import it.polimi.ingsw.model.board.general.ActionTokens;
import it.polimi.ingsw.model.board.general.CardDealer;
import it.polimi.ingsw.model.board.general.GeneralBoard;
import it.polimi.ingsw.model.exceptions.FaithOverflowException;
import it.polimi.ingsw.model.exceptions.CardsOfSameColorFinishedException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

public class LorenzoTest {

    private Lorenzo lorenzo;
    private GeneralBoard generalBoard;

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        generalBoard = new GeneralBoard();
        lorenzo = new Lorenzo(generalBoard);

    }

    @Test
    public void testPlay() throws CardsOfSameColorFinishedException, FaithOverflowException {
        int positionAfter;
        int positionBefore;
        int[] previousSize = new int[4]; //size of a group of cards before the turn
        int[] afterSize = new int[4];
        CardDealer cardDealer = generalBoard.getCardDealer();
        ActionTokens lastAction;

        for(int k=0; k<10; k++)
        {
            for (int i = 0; i < previousSize.length; i++) {
                previousSize = new int[4];
                for (int j = 0; j < 3; j++) {
                    previousSize[i] = previousSize[i] + (cardDealer.getCardMatrix())[j][i].size();
                }
            }

            positionBefore = lorenzo.getFaithTrack().getPosition();
            lorenzo.play();
            lastAction = lorenzo.getLastAction();

            for (int i = 0; i < afterSize.length; i++) {
                afterSize = new int[4];
                for (int j = 0; j < 3; j++) {
                    afterSize[i] = afterSize[i] + (cardDealer.getCardMatrix())[j][i].size();
                }
            }

            switch (lastAction) {
                case SHUFFLEFAITH:
                    positionAfter = lorenzo.getFaithTrack().getPosition();
                    assertEquals(positionBefore, positionAfter - 1);
                    break;
                case DOUBLEFAITH:
                    positionAfter = lorenzo.getFaithTrack().getPosition();
                    assertEquals(positionBefore, positionAfter - 2);
                    break;
                case GREEN:
                    if(previousSize[0] != 0)
                        assertEquals(previousSize[0], afterSize[0] + 2);
                    else
                        assertEquals(previousSize[0], afterSize[0]);
                    break;
                case BLUE:
                    if(previousSize[1] != 0)
                        assertEquals(previousSize[1], afterSize[1] + 2);
                    else
                        assertEquals(previousSize[1], afterSize[1]);
                    break;
                case YELLOW:
                    if(previousSize[2] != 0)
                        assertEquals(previousSize[2], afterSize[2] + 2);
                    else
                        assertEquals(previousSize[2], afterSize[2]);
                    break;
                case PURPLE:
                    if(previousSize[3] != 0)
                        assertEquals(previousSize[3], afterSize[3] + 2);
                    else
                        assertEquals(previousSize[3], afterSize[3]);
                    break;
            }
        }

    }


    @Test
    public void testGetLastAction() {
    }

    @Test
    public void testGetVictoryPoints() {

    }
}