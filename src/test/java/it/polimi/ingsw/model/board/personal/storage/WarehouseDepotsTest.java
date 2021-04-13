package it.polimi.ingsw.model.board.personal.storage;

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
        res.set(ResourceTypes.STONES,2);
        wd.addResourceSwap(res);

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
        res.set(ResourceTypes.STONES,2);
        res.set(ResourceTypes.SHIELDS,4);
        wd.addResourceSwap(res);

        try {
            wd.moveToLevel(3,ResourceTypes.GOLD,2);
            wd.moveToLevel(2,ResourceTypes.STONES,2);
            wd.moveToLevel(3,ResourceTypes.SHIELDS,2);
        } catch (TypeNotChangeableException e) {
            assert true;
        } catch (LevelTooSmallException | NegativeResourceValueException e) {
            e.printStackTrace();
        }

        try {
            wd.moveToLevel(1,ResourceTypes.SHIELDS,4);
        } catch (TypeNotChangeableException | NegativeResourceValueException e) {
            e.printStackTrace();
        } catch (LevelTooSmallException e) {
            assert true;
        }

        try {
            wd.moveToLevel(1,ResourceTypes.SERVANTS,1);
        } catch (TypeNotChangeableException | NegativeResourceValueException e) {
            e.printStackTrace();
        } catch (LevelTooSmallException e) {
            assert true;
        }


        assert wd.getResources().equals(res);


    }
}