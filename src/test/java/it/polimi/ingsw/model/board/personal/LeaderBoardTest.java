package it.polimi.ingsw.model.board.personal;

import it.polimi.ingsw.model.board.general.GeneralBoard;
import it.polimi.ingsw.model.cards.DevelopmentCardRequirement;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.model.cards.specialAbility.Discount;
import it.polimi.ingsw.model.cards.specialAbility.ExtraDeposit;
import it.polimi.ingsw.model.cards.specialAbility.ExtraProduction;
import it.polimi.ingsw.model.cards.specialAbility.ExtraResource;
import it.polimi.ingsw.model.exceptions.*;
import it.polimi.ingsw.model.resources.ResourceTypes;
import it.polimi.ingsw.model.resources.Resources;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class LeaderBoardTest {

    PersonalBoard personalBoard;

    @Before
    public void setUp() throws Exception {

        ArrayList<DevelopmentCardRequirement> developmentCardRequirement;
        developmentCardRequirement= new ArrayList<>();

        ArrayList<LeaderCard> leaderCardsInHands=new ArrayList<>();
        leaderCardsInHands.add(new LeaderCard(2, "a", developmentCardRequirement, new ExtraDeposit(ResourceTypes.STONE)));
        leaderCardsInHands.add(new LeaderCard(3, "b", developmentCardRequirement, new ExtraProduction(ResourceTypes.SHIELD)));
        leaderCardsInHands.add(new LeaderCard(2, "a", developmentCardRequirement, new Discount(ResourceTypes.SHIELD)));
        leaderCardsInHands.add(new LeaderCard(3, "b", developmentCardRequirement, new ExtraResource(ResourceTypes.SHIELD)));

        personalBoard = new PersonalBoard(new GeneralBoard(),leaderCardsInHands);

    }

    @Test
    public void playLeader() {
        try {
            personalBoard.playLeader(personalBoard.getLeaderBoard().getLeaderCardsInHand().get(0));
            assert true;
        } catch (UnusableCardException e) {
            assert false;
        }

        personalBoard.getLeaderBoard().getExtraDepositEffects();
        assertEquals(1, personalBoard.getLeaderBoard().getExtraDepositEffects().size());
    }

    @Test
    public void getProductionEffects() {
        try {
            personalBoard.playLeader(personalBoard.getLeaderBoard().getLeaderCardsInHand().get(1));
            personalBoard.playLeader(personalBoard.getLeaderBoard().getLeaderCardsInHand().get(0));
            assert true;
        } catch (UnusableCardException e) {
            assert false;
        }

        personalBoard.getLeaderBoard().getProductionEffects();
        assertEquals(1, personalBoard.getLeaderBoard().getProductionEffects().size());
    }

    @Test
    public void activateLeaderEffect() throws FaithNotAllowedException, LevelTooSmallException, NegativeResourceValueException, TypeNotChangeableException, UnusableCardException {
        personalBoard.playLeader(personalBoard.getLeaderBoard().getLeaderCardsInHand().get(0));
        personalBoard.getDeposit().getWarehouseDepots().addResourceSwap(new Resources().set(ResourceTypes.STONE,1));
        personalBoard.getDeposit().getWarehouseDepots().moveToLevel(3,ResourceTypes.STONE,1);
    }
}