package it.polimi.ingsw.model.board.personal.storage;

import it.polimi.ingsw.model.exceptions.FaithNotAllowedException;
import it.polimi.ingsw.model.exceptions.LevelTooSmallException;
import it.polimi.ingsw.model.exceptions.NegativeResourceValueException;
import it.polimi.ingsw.model.exceptions.TypeNotChangeableException;
import it.polimi.ingsw.model.resources.ResourceTypes;
import it.polimi.ingsw.model.resources.Resources;
import junit.framework.TestCase;

public class WarehouseDepotsTest extends TestCase {

    public void testMoveToSwap() {
        WarehouseDepots wd=new WarehouseDepots();

        wd.addLevel(new Level(ResourceTypes.GOLD,2));

        Resources res = new Resources();
        res.set(ResourceTypes.GOLD,2);
        res.set(ResourceTypes.STONE,2);
        try {
            wd.addResourceSwap(res);
        } catch (FaithNotAllowedException e) {
            e.printStackTrace();
        }

        try {
            wd.moveToLevel(3,ResourceTypes.GOLD,2);
        } catch (TypeNotChangeableException | LevelTooSmallException | NegativeResourceValueException e) {
            e.printStackTrace();
        }

        assert wd.getResources().getResourceNumber(ResourceTypes.GOLD)==2;
        if (wd.removeFromSwap().getResourceNumber(ResourceTypes.GOLD) != 0) throw new AssertionError();

        wd.moveToSwap(3);

        if (wd.removeFromSwap().getResourceNumber(ResourceTypes.GOLD) != 2) throw new AssertionError();
    }

    public void testGetResources() {
        WarehouseDepots wd=new WarehouseDepots();

        wd.addLevel(new Level(ResourceTypes.GOLD,2));

        Resources res = new Resources();
        res.set(ResourceTypes.GOLD,2);
        res.set(ResourceTypes.STONE,2);
        res.set(ResourceTypes.SHIELD,4);
        try {
            wd.addResourceSwap(res);
        } catch (FaithNotAllowedException e) {
            e.printStackTrace();
        }

        try {
            wd.moveToLevel(3,ResourceTypes.GOLD,2);
            wd.moveToLevel(2,ResourceTypes.STONE,2);
            wd.moveToLevel(3,ResourceTypes.SHIELD,2);
        } catch (TypeNotChangeableException e) {
            assert true;
        } catch (LevelTooSmallException | NegativeResourceValueException e) {
            e.printStackTrace();
        }

        try {
            wd.moveToLevel(1,ResourceTypes.SHIELD,4);
        } catch (TypeNotChangeableException | NegativeResourceValueException e) {
            e.printStackTrace();
        } catch (LevelTooSmallException e) {
            assert true;
        }



        assert wd.getResources().equals(new Resources().set(ResourceTypes.GOLD,2).set(ResourceTypes.STONE,2));


    }
}