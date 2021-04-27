package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.cards.specialAbility.*;
import it.polimi.ingsw.model.resources.Resources;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static it.polimi.ingsw.model.resources.ResourceTypes.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LeaderCardTest {
    private LeaderCard leaderCardRes;
    private LeaderCard leaderCardCol;
    private LeaderCard leaderCardLev;
    private Resources resAvailableEnough;
    private Resources resAvailableScarce; //not enough
    private ArrayList<DevelopmentCard> developmentCardListAvailableEnough;
    private ArrayList<DevelopmentCard> developmentCardListAvailableScarce;

    @Before
    public void setUp(){
        Resources resRequired; //by LeaderCard

        List<DevelopmentCard> cardsAvailableEnough;
        List<DevelopmentCard> cardsAvailableScarce;

        SpecialAbility specialAbility;
        specialAbility = new SpecialAbility() {
            @Override
            public Discount applyDiscount() {
                return null;
            }

            @Override
            public ExtraDeposit applyExtraDeposit() {
                return null;
            }

            @Override
            public ExtraProduction applyExtraProduction() {
                return null;
            }

            @Override
            public ExtraResource applyExtraResource() {
                return null;
            }
        };

        resRequired = new Resources();
        resRequired.set(GOLD, 5);

        resAvailableEnough = new Resources();
        resAvailableScarce = new Resources();

        DevelopmentCardRequirement developmentCardRequirementLevel = new DevelopmentCardRequirement('y', 2);
        DevelopmentCardRequirement developmentCardRequirementColor1 = new DevelopmentCardRequirement('y');
        DevelopmentCardRequirement developmentCardRequirementColor2 = new DevelopmentCardRequirement('y');
        DevelopmentCardRequirement developmentCardRequirementColor3 = new DevelopmentCardRequirement('b');

        ArrayList<DevelopmentCardRequirement> developmentCardRequirementListOnlyColors;
        developmentCardRequirementListOnlyColors = new ArrayList<>();
        developmentCardRequirementListOnlyColors.add(developmentCardRequirementColor1);
        developmentCardRequirementListOnlyColors.add(developmentCardRequirementColor2);
        developmentCardRequirementListOnlyColors.add(developmentCardRequirementColor3);

        leaderCardRes = new LeaderCard(2, "name1", resRequired, specialAbility); //create LeaderCard that required resources
        leaderCardLev = new LeaderCard(2, "name2",developmentCardRequirementLevel, specialAbility); //create LeaderCard that required one card with level and color
        leaderCardCol = new LeaderCard(2, "name3",developmentCardRequirementListOnlyColors, specialAbility); //create LeaderCard that required different DevelopmentCards (different colors)

        resAvailableEnough.set(GOLD, 5);
        resAvailableScarce.set(GOLD, 4);
        resAvailableScarce.set(SHIELD, 6); //for instance, resAvaSca has not enough GOLD and it can have lots of SHIELDS (not needed)

        developmentCardListAvailableEnough = new ArrayList<>();
        developmentCardListAvailableScarce = new ArrayList<>();

        Resources resDevCost, resProdCost, resProdPow;

        resDevCost = new Resources();
        resProdCost = new Resources();
        resProdPow = new Resources();

        resDevCost.set(SHIELD, 2);
        resProdCost.set(GOLD, 2);
        resProdPow.set(FAITH, 1);

        DevelopmentCard developmentCard1 = new DevelopmentCard(1, "name1", resDevCost, 'y', 2, resProdCost, resProdPow);
        DevelopmentCard developmentCard2 = new DevelopmentCard(2, "name2", resDevCost, 'y', 1, resProdCost, resProdPow);
        DevelopmentCard developmentCard3 = new DevelopmentCard(2, "name3", resDevCost, 'b', 3, resProdCost, resProdPow);

        DevelopmentCard developmentCard4 = new DevelopmentCard(1, "name4", resDevCost, 'g', 1, resProdCost, resProdPow);

        developmentCardListAvailableEnough.add(developmentCard1);
        developmentCardListAvailableEnough.add(developmentCard2);
        developmentCardListAvailableEnough.add(developmentCard3);

        developmentCardListAvailableScarce.add(developmentCard2);
        developmentCardListAvailableScarce.add(developmentCard4);

    }

    @Test
    public void testCheckRequirementsResources() {
        assertFalse(leaderCardRes.checkRequirements(resAvailableScarce, developmentCardListAvailableEnough));
        assertTrue(leaderCardRes.checkRequirements(resAvailableEnough, developmentCardListAvailableEnough));
    }

    @Test
    public void testCheckRequirementsDevCardWithLevel(){
        assertFalse(leaderCardLev.checkRequirements(resAvailableEnough, developmentCardListAvailableScarce));
        assertTrue(leaderCardLev.checkRequirements(resAvailableEnough, developmentCardListAvailableEnough));
    }

    @Test
    public void testCheckRequirementsDevCardOnlyColor(){
        assertFalse(leaderCardCol.checkRequirements(resAvailableScarce, developmentCardListAvailableScarce));
        assertTrue(leaderCardCol.checkRequirements(resAvailableScarce, developmentCardListAvailableEnough));
    }
}