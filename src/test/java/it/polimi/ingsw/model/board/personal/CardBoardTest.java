package it.polimi.ingsw.model.board.personal;

import it.polimi.ingsw.model.cards.DevelopmentCard;
import it.polimi.ingsw.model.exceptions.IncompatibleCardLevelException;
import it.polimi.ingsw.model.exceptions.WinException;
import it.polimi.ingsw.model.resources.Resources;
import junit.framework.TestCase;

public class CardBoardTest extends TestCase {

    public void testGetDevelopmentCards() {
        CardBoard cb=new CardBoard();
        try {
            cb.insertCard(new DevelopmentCard(13, "name1", new Resources(),'b',1,new Resources(),new Resources()),0);
            cb.insertCard(new DevelopmentCard(13, "name2", new Resources(),'b',2,new Resources(),new Resources()),0);
            cb.insertCard(new DevelopmentCard(13, "name3", new Resources(),'b',1,new Resources(),new Resources()),1);
            cb.insertCard(new DevelopmentCard(13, "name4", new Resources(),'b',1,new Resources(),new Resources()),2);
            cb.insertCard(new DevelopmentCard(13, "name5", new Resources(),'b',3,new Resources(),new Resources()),2);
        } catch (IncompatibleCardLevelException | WinException e) {
            assert true;
        }

        assert cb.getDevelopmentCards().size()==4;
    }

    public void testGetUpperDevelopmentCards() {
        CardBoard cb=new CardBoard();
        try {
            cb.insertCard(new DevelopmentCard(13, "name1", new Resources(),'b',1,new Resources(),new Resources()),0);
            cb.insertCard(new DevelopmentCard(13, "name2", new Resources(),'b',2,new Resources(),new Resources()),0);
            cb.insertCard(new DevelopmentCard(13, "name3", new Resources(),'b',1,new Resources(),new Resources()),1);
        } catch (IncompatibleCardLevelException | WinException e) {
            e.printStackTrace();
        }

        DevelopmentCard[] cards= cb.getUpperDevelopmentCards();

        assert cards[2]==null;
        assert cards.length==3;
    }

    public void testGetAllVictoryPoint() {
        CardBoard cb=new CardBoard();
        try {
            cb.insertCard(new DevelopmentCard(13, "name1", new Resources(),'b',1,new Resources(),new Resources()),0);
            cb.insertCard(new DevelopmentCard(13, "name2", new Resources(),'b',2,new Resources(),new Resources()),0);
            cb.insertCard(new DevelopmentCard(13, "name3", new Resources(),'b',1,new Resources(),new Resources()),1);
        } catch (IncompatibleCardLevelException | WinException e) {
            e.printStackTrace();
        }

        assert cb.getAllVictoryPoint()==39;
    }
}