package it.polimi.ingsw.model.board.personal;

import it.polimi.ingsw.model.board.personal.storage.StrongBox;
import it.polimi.ingsw.model.board.personal.storage.WarehouseDepots;
import it.polimi.ingsw.model.exceptions.NegativeResourceValueException;
import it.polimi.ingsw.model.resources.Resources;

/**
 * Class that represents the resources storing area
 */
public class Deposit {
    private final WarehouseDepots storage;
    private final StrongBox chest;

    public Deposit() {
        this.storage = new WarehouseDepots();
        this.chest = new StrongBox();
    }

    public WarehouseDepots getStorage() {
        return storage;
    }

    public StrongBox getChest() {
        return chest;
    }

    public Resources getTotalResources(){
        return storage.getResources().add(chest.getResources());
    }

    public boolean checkRemoveResource(Resources requirements){
        return this.getTotalResources().isSubPositive(requirements);
    }

    /**
     * Removes resources from the storage giving priority to what's contained in the swap area
     * @param resources The resources to be removed
     * @throws NegativeResourceValueException In case in the swap area there are more resources than the ones to be removed
     */
    public void removeResources(Resources resources) throws NegativeResourceValueException {
        Resources diff = resources.sub(storage.removeFromSwap());
        chest.removeResource(diff);
    }
}
