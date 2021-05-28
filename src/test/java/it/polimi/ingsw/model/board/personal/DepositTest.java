package it.polimi.ingsw.model.board.personal;

import it.polimi.ingsw.model.exceptions.FaithNotAllowedException;
import it.polimi.ingsw.model.exceptions.LevelTooSmallException;
import it.polimi.ingsw.model.exceptions.NegativeResourceValueException;
import it.polimi.ingsw.model.exceptions.TypeNotChangeableException;
import it.polimi.ingsw.model.resources.Resources;
import org.junit.Test;

import static it.polimi.ingsw.model.resources.ResourceTypes.SERVANT;
import static it.polimi.ingsw.model.resources.ResourceTypes.SHIELD;
import static org.junit.Assert.*;

public class DepositTest {

    @Test
    public void removeResources() throws FaithNotAllowedException, LevelTooSmallException, NegativeResourceValueException, TypeNotChangeableException {
        Deposit d= new Deposit();

        d.getStrongBox().addResource(new Resources().set(SHIELD, 1));
        d.getWarehouseDepots().addResourceSwap(new Resources().set(SHIELD,4).set(SERVANT,2));

        try {
            d.removeResources(new Resources().set(SHIELD,5).set(SERVANT,2));
            assert false;
        } catch (NegativeResourceValueException e) {
            assert true;
        }

        d.getWarehouseDepots().moveToLevel(2,SHIELD,3);
        d.getWarehouseDepots().moveToLevel(0,SHIELD,1);
        d.getWarehouseDepots().moveToLevel(1,SERVANT,2);

        try {
            d.removeResources(new Resources().set(SHIELD,4).set(SERVANT,2));
            assert true;
        } catch (NegativeResourceValueException e) {
            assert false;
        }

        assert d.getTotalResources().equals(new Resources().set(SHIELD,1));

        try {
            d.removeResources(new Resources().set(SHIELD,5).set(SERVANT,2));
            assert false;
        } catch (NegativeResourceValueException e) {
            assert true;
        }
    }
}