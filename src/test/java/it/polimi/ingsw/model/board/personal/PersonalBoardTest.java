package it.polimi.ingsw.model.board.personal;

import it.polimi.ingsw.model.board.general.GeneralBoard;
import it.polimi.ingsw.model.cards.DevelopmentCard;
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

public class PersonalBoardTest {
    PersonalBoard personalBoard;

    @Before
    public void setUp() {

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
    public void produce() {
        try {
            personalBoard.getCardBoard().insertCard(new DevelopmentCard(1,"a",new Resources(),'c',1,new Resources().set(ResourceTypes.SHIELD,2).set(ResourceTypes.SERVANT,1),new Resources().set(ResourceTypes.FAITH,1).set(ResourceTypes.STONE,1)),0);
        } catch (IncompatibleCardLevelException e) {
            e.printStackTrace();
        } catch (WinException e) {
            e.printStackTrace();
        }
        try {
            personalBoard.produce(0);
            assert false;
        } catch (UnusableCardException e) {
            assert true;
        } catch (FaithOverflowException e) {
            assert false;
        }

        try {
            personalBoard.getDeposit().getStrongBox().addResource(new Resources().set(ResourceTypes.SERVANT,1).set(ResourceTypes.SHIELD,1));
        } catch (FaithNotAllowedException e) {
            e.printStackTrace();
        }

        try {
            personalBoard.produce(0);
            assert false;
        } catch (UnusableCardException e) {
            assert true;
        } catch (FaithOverflowException e) {
            assert false;
        }
        assert personalBoard.getDeposit().getTotalResources().equals(new Resources().set(ResourceTypes.SERVANT,1).set(ResourceTypes.SHIELD,1));

        try {
            personalBoard.playLeader(personalBoard.getLeaderBoard().getLeaderCardsInHand().get(2));
        } catch (UnusableCardException e) {
            e.printStackTrace();
        }

        try {
            personalBoard.produce(0);
            assert false;
        } catch (UnusableCardException | FaithOverflowException e) {
            assert true;
        }

        assert personalBoard.getDeposit().getTotalResources().equals(new Resources().set(ResourceTypes.SERVANT,1).set(ResourceTypes.SHIELD,1));
        assert personalBoard.getFaithTrack().getPosition()==0;
    }

    @Test
    public void testProduce() {
        try {
            personalBoard.getDeposit().getStrongBox().addResource(new Resources().set(ResourceTypes.SERVANT,1));
        } catch (FaithNotAllowedException e) {
            e.printStackTrace();
        }

        try {
            personalBoard.produce(ResourceTypes.SERVANT,ResourceTypes.SHIELD,ResourceTypes.FAITH);
            assert false;
        } catch (NegativeResourceValueException | FaithOverflowException e) {
            assert true;
        }


        try {
            personalBoard.playLeader(personalBoard.getLeaderBoard().getLeaderCardsInHand().get(2));
        } catch (UnusableCardException e) {
            e.printStackTrace();
        }

        assert personalBoard.handleDiscount(new Resources().set(ResourceTypes.SHIELD,2).set(ResourceTypes.STONE,1)).equals(new Resources().set(ResourceTypes.SHIELD,1).set(ResourceTypes.STONE,1));
    }

    @Test
    public void testProduce1() {
        try {
            personalBoard.produce(ResourceTypes.STONE,ResourceTypes.SHIELD);
            assert false;
        } catch (FaithOverflowException | NegativeResourceValueException | UnusableCardException e) {
            assert true;
        }

        try {
            personalBoard.playLeader(personalBoard.getLeaderBoard().getLeaderCardsInHand().get(2));
        } catch (UnusableCardException e) {
            assert false;
        }

        try {
            personalBoard.produce(ResourceTypes.SHIELD,ResourceTypes.SHIELD);
            assert false;
        } catch (FaithOverflowException | NegativeResourceValueException | UnusableCardException e) {
            assert true;
        }

        try {
            personalBoard.playLeader(personalBoard.getLeaderBoard().getLeaderCardsInHand().get(1));
        } catch (UnusableCardException e) {
            assert false;
        }
        Resources res=personalBoard.getDeposit().getTotalResources();

        assert personalBoard.getDeposit().getTotalResources().equals(new Resources());
    }

    @Test
    public void buyColumn() {
        try {
            personalBoard.buyColumn(1,-1);
        } catch (FaithOverflowException | YouMustPlayLeaderException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void buyRow() {
        try {
            personalBoard.buyRow(1,-1);
        } catch (FaithOverflowException | YouMustPlayLeaderException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testInitProduce() {
        personalBoard.initProduce();
        assert personalBoard.isProd1();

        for (int i=0;i<3;i++){
            assert personalBoard.getProd2()[i]==false;
        }

        assert personalBoard.getAvailableProductions().size()==0;
    }

    @Test
    public void testEndProduce() {
        personalBoard.endProduce();
        assert personalBoard.isProd1();

        for (int i=0;i<3;i++){
            assert personalBoard.getProd2()[i]==false;
        }

        assert personalBoard.getAvailableProductions().size()==0;
    }

    @Test
    public void testEnqueueProduce() {
        try {
            personalBoard.enqueueProduce(ResourceTypes.SHIELD,ResourceTypes.FAITH);
            assert false;
        } catch (UnusableCardException e) {
            assert true;
        } catch (NegativeResourceValueException e) {
            assert false;
        } catch (FaithOverflowException e) {
            assert false;
        }
    }

    @Test
    public void testEnqueueProduce1() throws FaithNotAllowedException {
        personalBoard.initProduce();
        try {
            personalBoard.playLeader(personalBoard.getLeaderBoard().getLeaderCardsInHand().get(2));
        } catch (UnusableCardException e) {
            assert false;
        }

        try {
            personalBoard.enqueueProduce(ResourceTypes.SHIELD,ResourceTypes.SHIELD);
            assert false;
        } catch (FaithOverflowException | NegativeResourceValueException | UnusableCardException e) {
            assert true;
        }

        try {
            personalBoard.playLeader(personalBoard.getLeaderBoard().getLeaderCardsInHand().get(1));

        } catch (UnusableCardException e) {
            assert false;
        }

        try {
            personalBoard.enqueueProduce(ResourceTypes.SHIELD,ResourceTypes.STONE);
            assert false;
        } catch (FaithOverflowException | NegativeResourceValueException | UnusableCardException e) {
            assert true;
        }

        personalBoard.getDeposit().getStrongBox().addResource(new Resources().set(ResourceTypes.SHIELD,1));

        personalBoard.initProduce();

        try {
            personalBoard.enqueueProduce(ResourceTypes.SHIELD,ResourceTypes.STONE);
            assert true;
        } catch (FaithOverflowException | NegativeResourceValueException | UnusableCardException e) {
            e.printStackTrace();
            assert false;
        }

        Resources res=personalBoard.getAvailableResources();
        assert personalBoard.getDeposit().getTotalResources().equals(new Resources().set(ResourceTypes.STONE,1));

    }

    @Test
    public void testEnqueueProduce2() {
    }
}