package it.polimi.ingsw.model.board.general;

import it.polimi.ingsw.model.resources.ResourceTypes;
import it.polimi.ingsw.model.resources.Resources;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

public class MarketTest {

    private Market market;
    private Resources bought; //the resource map obtained after buying a column or a row
    private ResourceTypes[][] marketMatrix; //attribute used to access marketMatrix

    @Rule  //used to check if a method throws a particular exception without stopping the test
    public final ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        market = new Market();
        bought = new Resources();
        ResourceTypes[][] marketMatrix = market.getMarketMatrix();
        market.setExternalResource(ResourceTypes.SHIELDS);

        //set marketMatrix because it is filled with casual resources but tests cannot be casuals
        marketMatrix[0][0] = ResourceTypes.SERVANTS;
        marketMatrix[0][1] = ResourceTypes.SHIELDS;
        marketMatrix[0][2] = ResourceTypes.SERVANTS;
        marketMatrix[0][3] = ResourceTypes.STONES;
        marketMatrix[1][0] = ResourceTypes.BLANK;
        marketMatrix[1][1] = ResourceTypes.FAITH;
        marketMatrix[1][2] = ResourceTypes.BLANK;
        marketMatrix[1][3] = ResourceTypes.BLANK;
        marketMatrix[2][0] = ResourceTypes.BLANK;
        marketMatrix[2][1] = ResourceTypes.STONES;
        marketMatrix[2][2] = ResourceTypes.GOLD;
        marketMatrix[2][3] = ResourceTypes.GOLD;

    }

    @Test
    public void testbuyColumn() {

        /*test all possible legit values of column*/

        bought = market.buyColumn(0);
        //check the external resource and the map of Resources returned by the method
        assertEquals(market.getExternalResource(), ResourceTypes.SERVANTS);
        assertEquals(bought.getResourceNumber(ResourceTypes.GOLD), 0);
        assertEquals(bought.getResourceNumber(ResourceTypes.STONES), 0);
        assertEquals(bought.getResourceNumber(ResourceTypes.SHIELDS), 0);
        assertEquals(bought.getResourceNumber(ResourceTypes.SERVANTS), 1);
        assertEquals(bought.getResourceNumber(ResourceTypes.FAITH), 0);
        assertEquals(bought.getResourceNumber(ResourceTypes.BLANK), 2);

        //check if external resource is added successfully and if pushColumn worked
        marketMatrix = market.getMarketMatrix();
        assertEquals(marketMatrix[0][0], ResourceTypes.BLANK);
        assertEquals(marketMatrix[2][0], ResourceTypes.SHIELDS);


        bought = market.buyColumn(1);
        assertEquals(market.getExternalResource(), ResourceTypes.SHIELDS);
        assertEquals(bought.getResourceNumber(ResourceTypes.GOLD), 0);
        assertEquals(bought.getResourceNumber(ResourceTypes.STONES), 1);
        assertEquals(bought.getResourceNumber(ResourceTypes.SHIELDS), 1);
        assertEquals(bought.getResourceNumber(ResourceTypes.SERVANTS), 0);
        assertEquals(bought.getResourceNumber(ResourceTypes.FAITH), 1);
        assertEquals(bought.getResourceNumber(ResourceTypes.BLANK), 0);

        assertEquals(marketMatrix[0][1], ResourceTypes.FAITH);
        assertEquals(marketMatrix[2][1], ResourceTypes.SERVANTS);


        market.buyColumn(2);
        assertEquals(market.getExternalResource(), ResourceTypes.SERVANTS);

        market.buyColumn(3);
        assertEquals(market.getExternalResource(), ResourceTypes.STONES);


        exception.expect(IndexOutOfBoundsException.class);
        market.buyColumn(4);

    }

    @Test
    public void testbuyRow() {

        /*test all possible legit values of row*/

        bought = market.buyRow(0);
        assertEquals(market.getExternalResource(), ResourceTypes.SERVANTS);
        assertEquals(bought.getResourceNumber(ResourceTypes.GOLD), 0);
        assertEquals(bought.getResourceNumber(ResourceTypes.STONES), 1);
        assertEquals(bought.getResourceNumber(ResourceTypes.SHIELDS), 1);
        assertEquals(bought.getResourceNumber(ResourceTypes.SERVANTS), 2);
        assertEquals(bought.getResourceNumber(ResourceTypes.FAITH), 0);
        assertEquals(bought.getResourceNumber(ResourceTypes.BLANK), 0);

        marketMatrix = market.getMarketMatrix();
        assertEquals(marketMatrix[0][0], ResourceTypes.SHIELDS);
        assertEquals(marketMatrix[0][3], ResourceTypes.SHIELDS);

        bought = market.buyRow(1);
        assertEquals(market.getExternalResource(), ResourceTypes.BLANK);
        assertEquals(bought.getResourceNumber(ResourceTypes.GOLD), 0);
        assertEquals(bought.getResourceNumber(ResourceTypes.STONES), 0);
        assertEquals(bought.getResourceNumber(ResourceTypes.SHIELDS), 0);
        assertEquals(bought.getResourceNumber(ResourceTypes.SERVANTS), 0);
        assertEquals(bought.getResourceNumber(ResourceTypes.FAITH), 1);
        assertEquals(bought.getResourceNumber(ResourceTypes.BLANK), 3);

        assertEquals(marketMatrix[1][0],ResourceTypes.FAITH);
        assertEquals(marketMatrix[1][3], ResourceTypes.SERVANTS); //because externalResource changed after buyRow(0)

        market.buyRow(2);
        assertEquals(market.getExternalResource(), ResourceTypes.BLANK);

        exception.expect(IndexOutOfBoundsException.class);
        market.buyRow(5);

    }

    @Test
    public void testbuyColumn_buyRow() {

        market.buyColumn(0);
        market.buyRow(0);

        marketMatrix = market.getMarketMatrix();

        assertEquals(market.getExternalResource(), ResourceTypes.BLANK);
        assertEquals(marketMatrix[0][0], ResourceTypes.SHIELDS);
        assertEquals(marketMatrix[1][0], ResourceTypes.BLANK);
        assertEquals(marketMatrix[2][0], ResourceTypes.SHIELDS);
        assertEquals(marketMatrix[0][1], ResourceTypes.SERVANTS);
        assertEquals(marketMatrix[0][2], ResourceTypes.STONES);
        assertEquals(marketMatrix[0][3], ResourceTypes.SERVANTS);

    }

    @Test
    public void testgetExternalResource()
    {
        assertEquals(market.getExternalResource(), ResourceTypes.SHIELDS);
    }
}