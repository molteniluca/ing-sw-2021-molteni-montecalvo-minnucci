package it.polimi.ingsw.model.board.personal.storage;

import it.polimi.ingsw.model.exceptions.LevelTooSmallException;
import it.polimi.ingsw.model.exceptions.NegativeResourceValueException;
import it.polimi.ingsw.model.exceptions.TypeNotChangeableException;
import it.polimi.ingsw.model.resources.ResourceTypes;
import it.polimi.ingsw.model.resources.Resources;
import junit.framework.TestCase;

public class LevelTest extends TestCase {

    public void testGetResourcesAndErase() {
        Level l1 = new Level(3);

        try {
            l1.addResources(3,ResourceTypes.GOLD);
        } catch (LevelTooSmallException | NegativeResourceValueException | TypeNotChangeableException e) {
            e.printStackTrace();
        }

        Resources r = new Resources();
        r.set(ResourceTypes.GOLD,3);
        assert l1.getResourcesAndErase().equals(r);
        r.set(ResourceTypes.GOLD,0);
        assert l1.getResourcesAndErase().equals(r);
    }

    public void testAddRemoveResources() {
        Level l1 = new Level(3);

        try {
            l1.addResources(4,ResourceTypes.GOLD);
            assert false;
        } catch (LevelTooSmallException e) {
            assert true;
        } catch (NegativeResourceValueException | TypeNotChangeableException e) {
            assert false;
        }

        try {
            l1.addResources(1,ResourceTypes.GOLD);
            assert true;
        } catch (LevelTooSmallException | NegativeResourceValueException | TypeNotChangeableException e) {
            assert false;
        }

        try {
            l1.addResources(4,ResourceTypes.GOLD);
            assert false;
        } catch (LevelTooSmallException e) {
            assert true;
        } catch (NegativeResourceValueException | TypeNotChangeableException e) {
            assert false;
        }

        try {
            l1.addResources(2,ResourceTypes.GOLD);
            assert true;
        } catch (LevelTooSmallException | NegativeResourceValueException | TypeNotChangeableException e) {
            assert false;
        }

        try {
            l1.removeResources(2,ResourceTypes.GOLD);
            assert true;
        } catch (LevelTooSmallException | NegativeResourceValueException | TypeNotChangeableException e) {
            assert false;
        }


        assertEquals(l1.getResourceType(),ResourceTypes.GOLD);
        assertEquals(l1.getResourceNumber(),1);

        try {
            l1.removeResources(1,ResourceTypes.STONES);
            assert false;
        } catch (LevelTooSmallException | NegativeResourceValueException e) {
            assert false;
        } catch (TypeNotChangeableException e) {
            assert true;
        }

        try {
            l1.removeResources(2,ResourceTypes.GOLD);
            assert false;
        } catch (LevelTooSmallException | TypeNotChangeableException e) {
            assert false;
        } catch (NegativeResourceValueException e) {
            assert true;
        }

        l1 = new Level(ResourceTypes.GOLD,3);

        try {
            l1.addResources(3,ResourceTypes.STONES);
            assert false;
        } catch (LevelTooSmallException | NegativeResourceValueException e) {
            assert false;
        } catch (TypeNotChangeableException e) {
            assert true;
        }

        try {
            l1.addResources(3,ResourceTypes.GOLD);
            assert true;
        } catch (LevelTooSmallException | NegativeResourceValueException | TypeNotChangeableException e) {
            assert false;
        }
    }
}