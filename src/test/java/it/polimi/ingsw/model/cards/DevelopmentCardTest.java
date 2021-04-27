package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.resources.Resources;
import org.junit.Test;

import static it.polimi.ingsw.model.resources.ResourceTypes.*;
import static org.junit.Assert.*;

public class DevelopmentCardTest {

    @Test
    public void testCheckAvailability() {
        //SETUP
        Resources resAvailable, resAvailable2, resDevCost, resProdCost, resProdPow, resReq;

        resAvailable = new Resources();
        resAvailable2 = new Resources();

        resDevCost = new Resources();
        resProdCost = new Resources();
        resProdPow = new Resources();
        resReq = new Resources();

        resDevCost.set(SHIELD, 2);
        resProdCost.set(GOLD, 2);
        resProdPow.set(FAITH, 1);

        resReq.set(SHIELD, 1);

        DevelopmentCard devCard = new DevelopmentCard(1, "name1", resDevCost, 'y', 1, resProdCost, resProdPow);

        //resAv IS EMPTY, resProdCost has 1 res required
        assertFalse(devCard.checkAvailability(resAvailable));

        resAvailable.set(GOLD, 3);

        //resAv >= resProdCost with 1 res defined
        assertTrue(devCard.checkAvailability(resAvailable));

        resAvailable2.set(GOLD, 1);

        //resAv < resProdCost with 1 res
        assertFalse(devCard.checkAvailability(resAvailable2));

        resAvailable.set(SERVANT, 2);
        resProdCost.set(SERVANT, 1);

        //resAv >= resProdCost with 2 res defined
        assertTrue(devCard.checkAvailability(resAvailable));

        resAvailable2.set(SERVANT, 1);

        //resAv < resProdCost with 2 res defined, GOLD KO, SERVANTS OK
        assertFalse(devCard.checkAvailability(resAvailable2));

        resAvailable.set(SHIELD, 2);
        resAvailable.set(STONE, 2);
        resAvailable.set(FAITH, 2);
        resProdCost.set(SHIELD, 2);
        resProdCost.set(STONE, 1);
        resProdCost.set(FAITH, 2);

        //resAv >= resProdCost with all res defined
        assertTrue(devCard.checkAvailability(resAvailable));

        //resAv < resProdCost because resProdCost had defined more res required
        assertFalse(devCard.checkAvailability(resAvailable2));

        resAvailable2.set(GOLD, 3);
        resAvailable2.set(SERVANT, 3);
        resAvailable2.set(SHIELD, 1);
        resAvailable2.set(STONE, 3);
        resAvailable2.set(FAITH, 3);

        //resAv < resProdCost due to only one num of res, all res defined
        assertFalse(devCard.checkAvailability(resAvailable2));

        resAvailable2.set(SHIELD, 2);

        //Again, with every res defined and every num of res needed
        assertTrue(devCard.checkAvailability(resAvailable2));

    }
}