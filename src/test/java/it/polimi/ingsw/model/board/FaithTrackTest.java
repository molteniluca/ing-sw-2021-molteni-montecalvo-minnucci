package it.polimi.ingsw.model.board;

import it.polimi.ingsw.model.board.general.FaithObserver;
import it.polimi.ingsw.model.board.personal.FaithTrack;
import it.polimi.ingsw.model.exceptions.FaithOverflowException;
import junit.framework.TestCase;

public class FaithTrackTest extends TestCase {

    public void testGetVictoryPoint() {
        FaithTrack ft= new FaithTrack(new FaithObserver());
        assert ft.getVictoryPoint()==0;

        try {
            ft.incrementPosition(3);
        } catch (FaithOverflowException e) {
            e.printStackTrace();
        }

        assert ft.getVictoryPoint()==1;


        try {
            ft.incrementPosition(3);
        } catch (FaithOverflowException e) {
            e.printStackTrace();
        }

        ft.checkRelationship(0);

        assert ft.getVictoryPoint()==4;
    }

    public void testIncrementPosition() {
        FaithObserver fo= new FaithObserver();
        FaithTrack ft= new FaithTrack(fo);
        FaithTrack ft2= new FaithTrack(fo);

        try {
            ft.incrementPosition(6);
        } catch (FaithOverflowException e) {
            e.printStackTrace();
        }


        try {
            ft2.incrementPosition(8);
        } catch (FaithOverflowException e) {
            e.printStackTrace();
        }

        try {
            ft2.incrementPosition(8);
        } catch (FaithOverflowException e) {
            e.printStackTrace();
        }

        assert ft.getFaithCards()[0] ==1;
        assert ft2.getFaithCards()[0] ==1;
        assert ft.getFaithCards()[1] ==2;
        assert ft2.getFaithCards()[1] ==1;
    }
}