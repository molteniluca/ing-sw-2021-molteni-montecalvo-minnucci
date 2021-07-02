package it.polimi.ingsw.model.board.personal;

import it.polimi.ingsw.model.board.personal.storage.StrongBox;
import it.polimi.ingsw.model.board.personal.storage.WarehouseDepots;
import it.polimi.ingsw.model.exceptions.NegativeResourceValueException;
import it.polimi.ingsw.model.resources.Resources;

import java.io.Serializable;

/**
 * Class that represents the resources storing area
 */
public class Deposit implements Serializable {
    private static final long serialVersionUID = 6732146736278436287L;
    private final WarehouseDepots warehouseDepots;
    private final StrongBox strongBox;

    public Deposit() {
        this.warehouseDepots = new WarehouseDepots();
        this.strongBox = new StrongBox();
    }

    public WarehouseDepots getWarehouseDepots() {
        return warehouseDepots;
    }

    public StrongBox getStrongBox() {
        return strongBox;
    }

    public Resources getTotalResources(){
        return warehouseDepots.getResources().add(strongBox.getResources());
    }

    /**
     * Method that checks weather you can remove resources from this level
     * @param requirements The resources to check
     * @return True if there are enough resources and false if not
     */
    public boolean checkRemoveResource(Resources requirements){
        return this.getTotalResources().isSubPositive(requirements);
    }

    /**
     * Removes resources from the storage giving priority to what's contained in the swap area
     * @param resources The resources to be removed
     * @throws NegativeResourceValueException In case in the swap area there are more resources than the ones to be removed
     */
    public void removeResources(Resources resources) throws NegativeResourceValueException {
        Resources neg = warehouseDepots.getResources().negativeSubValues(resources);
        warehouseDepots.removeResources(resources.sub(neg));
        strongBox.removeResource(neg);
    }

    /**
     * This method gets the victory points associated with this resource
     * @return The victory points
     */
    public int getVictoryPoint() {
        int points=0;
        points+=Math.floor(warehouseDepots.getResources().getTotalResourceNumber()/5.0);
        points+=Math.floor(strongBox.getResources().getTotalResourceNumber()/5.0);
        return points;
    }
}
