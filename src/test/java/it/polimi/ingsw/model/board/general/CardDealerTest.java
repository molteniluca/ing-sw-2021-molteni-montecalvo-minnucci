package it.polimi.ingsw.model.board.general;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import it.polimi.ingsw.model.cards.DevelopmentCard;
import it.polimi.ingsw.model.exceptions.CardsOfSameColorFinishedException;
import it.polimi.ingsw.model.resources.ResourceTypes;
import it.polimi.ingsw.model.resources.Resources;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.InputStreamReader;
import java.util.*;

import static org.junit.Assert.*;

public class CardDealerTest {

    private CardDealer cardDealer;
    private static final int ROWS = 3;
    private static final int COLUMNS = 4;
    private Stack<DevelopmentCard>[][] cardMatrix;
    private DevelopmentCard developmentCard; //the drown card
    private ArrayList<Character> cardType; // array that contains types of DevelopmentCards --> colors

    @Rule
    public final ExpectedException exception = ExpectedException.none();


    /**
     * The test is done assuming that the card dealer is static, not shuffled
     * It is created and than reinitialized
     */
    @Before
    public void setUp(){
        cardDealer = new CardDealer();

        cardMatrix = cardDealer.getCardMatrix();

        Gson gson = new Gson();
        JsonReader reader = new JsonReader(new InputStreamReader(Objects.requireNonNull(ClassLoader.getSystemResourceAsStream("json/developmentCards.json"))));
        List<DevelopmentCard> data = gson.fromJson(reader,  new TypeToken<ArrayList<DevelopmentCard>>(){}.getType());


        //initializes the card matrix again
        for (int i=0; i<ROWS; i++) {
            for (int k=0; k<COLUMNS; k++) {
                cardMatrix[i][k] = new Stack<>();
            }
        }

        //fill the stacks with Development cards
        for (DevelopmentCard card: data)
        {
            switch (card.getType()) {
                case 'g':
                    switch (card.getLevel()) {
                        case 1:
                            cardMatrix[2][0].push(card);
                            break;
                        case 2:
                            cardMatrix[1][0].push(card);
                            break;
                        case 3:
                            cardMatrix[0][0].push(card);
                            break;
                    }
                    break;
                case 'b':
                    switch (card.getLevel()) {
                        case 1:
                            cardMatrix[2][1].push(card);
                            break;
                        case 2:
                            cardMatrix[1][1].push(card);
                            break;
                        case 3:
                            cardMatrix[0][1].push(card);
                            break;
                    }
                    break;
                case 'y':
                    switch (card.getLevel()) {
                        case 1:
                            cardMatrix[2][2].push(card);
                            break;
                        case 2:
                            cardMatrix[1][2].push(card);
                            break;
                        case 3:
                            cardMatrix[0][2].push(card);
                            break;
                    }
                    break;
                case 'p':
                    switch (card.getLevel()) {
                        case 1:
                            cardMatrix[2][3].push(card);
                            break;
                        case 2:
                            cardMatrix[1][3].push(card);
                            break;
                        case 3:
                            cardMatrix[0][3].push(card);
                            break;
                    }
                    break;
            }
        }

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
    public void testGetCost() {
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
    public void testDrawCard_Type_Level_VictoryPoint() throws CardsOfSameColorFinishedException {

        for (char type: cardType) {
            for (int i=12; i>8; i--) {
                developmentCard = cardDealer.drawCard(0, cardType.indexOf(type),true);
                assertEquals(developmentCard.getLevel(), 3);
                assertEquals(developmentCard.getType(), type);
                assertEquals(developmentCard.getVictoryPoint(), i);
            }

            for (int i=8; i>4; i--){
                developmentCard = cardDealer.drawCard(1, cardType.indexOf(type),true);
                assertEquals(developmentCard.getLevel(), 2);
                assertEquals(developmentCard.getType(), type);
                assertEquals(developmentCard.getVictoryPoint(), i);
            }

            exception.expect(CardsOfSameColorFinishedException.class);
            for (int i=4; i>0; i--) {
                developmentCard = cardDealer.drawCard(2, cardType.indexOf(type),true);
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
    public void testDrawCard_Resources() throws CardsOfSameColorFinishedException {
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
        developmentCard = cardDealer.drawCard(0,0,true);
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

        developmentCard = cardDealer.drawCard(2,3, true);
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

        developmentCard = cardDealer.drawCard(2,3,true);
        assertTrue(developmentCard.getCost().equals(cost));
        assertTrue(developmentCard.getProductionCost().equals(productionCost));
        assertTrue(developmentCard.getProductionPower().equals(productionPower));

    }


    /**
     * Checks the launched exceptions
     */
    @Test
    public void testDrawCard_Exception() throws CardsOfSameColorFinishedException {
        //draws more than 4 times the same cell of the matrix in card dealer in order to empty the stack
        exception.expect(EmptyStackException.class);
        for (int i=0; i<6; i++)
            developmentCard = cardDealer.drawCard(0,0, true);

        //try drawing a card out of bounds
        exception.expect(IndexOutOfBoundsException.class);
        cardDealer.drawCard(5,0, true);

        exception.expect(IndexOutOfBoundsException.class);
        cardDealer.drawCard(1,6, true);

        exception.expect(IndexOutOfBoundsException.class);
        cardDealer.drawCard(8,8, true);

    }


    /**
     * Checks if every type of card is discarded correctly.
     * 2 cards for every type passed; checks the method for every type, in the last cell of every column
     * @throws Exception if a problem occures
     */
    @Test
    public void testDiscardDevelopment() throws Exception{

        for(char type : cardType)
        {
            cardDealer.discardDevelopment(type);
            developmentCard = cardDealer.drawCard(2, cardType.indexOf(type), true);
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
    public void testDiscardDevelopment_exceptions() throws Exception {
        exception.expect(CardsOfSameColorFinishedException.class);
        for (char cardType: cardType) {
            for(int i=0; i<6; i++)
                cardDealer.discardDevelopment(cardType);
        }

    }
}