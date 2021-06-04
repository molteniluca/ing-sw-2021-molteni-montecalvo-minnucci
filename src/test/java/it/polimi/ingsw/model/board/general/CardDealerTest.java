package it.polimi.ingsw.model.board.general;

import it.polimi.ingsw.model.cards.DevelopmentCard;
import it.polimi.ingsw.model.exceptions.NotEnoughCardException;
import it.polimi.ingsw.model.resources.ResourceTypes;
import it.polimi.ingsw.model.resources.Resources;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.EmptyStackException;

import static org.junit.Assert.*;

public class CardDealerTest {

    private CardDealer cardDealer;
    private DevelopmentCard developmentCard; //the drown card
    private ArrayList<Character> cardType; // array that contains types of DevelopmentCards --> colors

    @Rule
    public final ExpectedException exception = ExpectedException.none();


    @Before
    public void setUp() throws Exception{
        cardDealer = new CardDealer();
        cardType = new ArrayList<>();
        //adding order is important
        cardType.add('g');
        cardType.add('b');
        cardType.add('y');
        cardType.add('p');
    }

    /**
     * Checks if the returned cost is correct
     */
    @Test
    public void testgetCost() {
        Resources cost = new Resources();
        cost.set(ResourceTypes.SHIELD, 2);
        cost.set(ResourceTypes.GOLD, 2);

        assertTrue(cardDealer.getCost(2,0).equals(cost));

        cost = new Resources();
        cost.set(ResourceTypes.SHIELD,3);
        cost.set(ResourceTypes.SERVANT,3);
        assertTrue(cardDealer.getCost(1,3).equals(cost));

    }

    /**
     * Checks if type, level and victory point of every card are correct
     */
    @Test
    public void testdrawCard_Type_Level_VictoryPoint() throws NotEnoughCardException {

        for (char type: cardType) {
            for (int i=12; i>8; i--) {
                developmentCard = cardDealer.drawCard(0, cardType.indexOf(type));
                assertEquals(developmentCard.getLevel(), 3);
                assertEquals(developmentCard.getType(), type);
                assertEquals(developmentCard.getVictoryPoint(), i);
            }

            for (int i=8; i>4; i--){
                developmentCard = cardDealer.drawCard(1, cardType.indexOf(type));
                assertEquals(developmentCard.getLevel(), 2);
                assertEquals(developmentCard.getType(), type);
                assertEquals(developmentCard.getVictoryPoint(), i);
            }

            exception.expect(NotEnoughCardException.class);
            for (int i=4; i>0; i--) {
                developmentCard = cardDealer.drawCard(2, cardType.indexOf(type));
                assertEquals(developmentCard.getLevel(), 1);
                assertEquals(developmentCard.getType(), type);
                assertEquals(developmentCard.getVictoryPoint(), i);
            }

        }
    }


    /**
     * Checks if cost, productionCost and productionPower are correct
     */
    @Test
    public void testdrawCard_Resources() throws NotEnoughCardException {
        Resources cost, productionCost, productionPower; //parameters of development card

        //creates and set the resources to check a particular card
        cost = new Resources();
        productionCost = new Resources();
        productionPower = new Resources();

        cost.set(ResourceTypes.GOLD, 4);
        cost.set(ResourceTypes.SHIELD, 4);

        productionCost.set(ResourceTypes.STONE,1);

        productionPower.set(ResourceTypes.GOLD, 3);
        productionPower.set(ResourceTypes.SHIELD, 1);

        //check first cell
        developmentCard = cardDealer.drawCard(0,0);
        assertTrue(developmentCard.getCost().equals(cost));
        assertTrue(developmentCard.getProductionCost().equals(productionCost));
        assertTrue(developmentCard.getProductionPower().equals(productionPower));

        //check the last cell
        cost = new Resources();
        productionCost = new Resources();
        productionPower = new Resources();


        cost.set(ResourceTypes.SERVANT,2);
        cost.set(ResourceTypes.STONE,2);
        productionCost.set(ResourceTypes.GOLD,1);
        productionCost.set(ResourceTypes.SHIELD,1);
        productionPower.set(ResourceTypes.STONE,2);
        productionPower.set(ResourceTypes.FAITH,1);

        developmentCard = cardDealer.drawCard(2,3);
        assertTrue(developmentCard.getCost().equals(cost));
        assertTrue(developmentCard.getProductionCost().equals(productionCost));
        assertTrue(developmentCard.getProductionPower().equals(productionPower));

        //draw the last cell of the cardMatrix again
        cost = new Resources();
        productionCost = new Resources();
        productionPower = new Resources();

        cost.set(ResourceTypes.SERVANT,3);
        productionCost.set(ResourceTypes.GOLD,2);
        productionPower.set(ResourceTypes.SERVANT,1);
        productionPower.set(ResourceTypes.SHIELD,1);
        productionPower.set(ResourceTypes.STONE,1);

        developmentCard = cardDealer.drawCard(2,3);
        assertTrue(developmentCard.getCost().equals(cost));
        assertTrue(developmentCard.getProductionCost().equals(productionCost));
        assertTrue(developmentCard.getProductionPower().equals(productionPower));

    }


    /**
     * Checks the launched exceptions
     */
    @Test
    public void testdrawCard_Exception() throws NotEnoughCardException {
        //draws more than 4 times the same cell of the matrix in card dealer in order to empty the stack
        exception.expect(EmptyStackException.class);
        for (int i=0; i<6; i++)
            developmentCard = cardDealer.drawCard(0,0);

        //try drawing a card out of bounds
        exception.expect(IndexOutOfBoundsException.class);
        cardDealer.drawCard(5,0);

        exception.expect(IndexOutOfBoundsException.class);
        cardDealer.drawCard(1,6);

        exception.expect(IndexOutOfBoundsException.class);
        cardDealer.drawCard(8,8);

    }


    /**
     * Checks if every type of card is discarded correctly.
     * 2 cards for every type passed; checks the method for every type, in the last cell of every column
     * @throws Exception if a problem occures
     */
    @Test
    public void testdiscardDevelopment() throws Exception{

        for(char type : cardType)
        {
            cardDealer.discardDevelopment(type);
            developmentCard = cardDealer.drawCard(2, cardType.indexOf(type));
            assertEquals(developmentCard.getLevel(), 1);
            assertEquals(developmentCard.getType(), type);
            assertEquals(developmentCard.getVictoryPoint(), 2);
        }

    }


    /**
     * Checks if the method throws NotEnoughCardException.
     * Since discardDevelopment discards a couple of cards and there are 12 cards for every type
     * the method is called more than 6 times in order to generate the exception
     */
    @Test
    public void testdiscardDevelopment_exceptions() throws Exception {
        exception.expect(NotEnoughCardException.class);
        for (char cardType: cardType) {
            for(int i=0; i<6; i++)
                cardDealer.discardDevelopment(cardType);
        }

    }
}