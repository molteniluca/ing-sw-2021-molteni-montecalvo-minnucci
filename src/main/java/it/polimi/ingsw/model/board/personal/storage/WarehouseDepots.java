package it.polimi.ingsw.model.board.personal.storage;

import it.polimi.ingsw.model.exceptions.FaithNotAllowedException;
import it.polimi.ingsw.model.exceptions.LevelTooSmallException;
import it.polimi.ingsw.model.exceptions.NegativeResourceValueException;
import it.polimi.ingsw.model.exceptions.TypeNotChangeableException;
import it.polimi.ingsw.model.resources.ResourceTypes;
import it.polimi.ingsw.model.resources.Resources;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class that represents the multilevel storage type
 */
public class WarehouseDepots implements Serializable {
    private final ArrayList<Level> warehouseDepots;
    private Resources swapDeposit;

    public WarehouseDepots(){
        swapDeposit=new Resources();
        warehouseDepots=new ArrayList<>();
        warehouseDepots.add(new Level(1));
        warehouseDepots.add(new Level(2));
        warehouseDepots.add(new Level(3));
    }

    /**
     * Method to add resources to the swap area from where the player can organize the resources
     * @param resource The resources to be added
     */
    public void addResourceSwap(Resources resource) throws FaithNotAllowedException {
        if(resource.getResourceNumber(ResourceTypes.FAITH)!=0)
            throw new FaithNotAllowedException("Faith not allowed in the storage");
        swapDeposit.add(resource);
    }

    /**
     * Method that takes resources contained from the swap area and removes them
     * @return The resources contained in the swap area
     */
    public Resources removeFromSwap(){
        Resources tmp = swapDeposit;
        swapDeposit=new Resources();
        return tmp;
    }

    /**
     * Method to move an entire level to the swap area
     * @param level The level to be moved
     */
    public void moveToSwap(int level){
        swapDeposit=swapDeposit.add(warehouseDepots.get(level).getResourcesAndErase());
    }

    /**
     * Method to move a certain quantity of resources to a level from the swap area
     * @param level The level to which move the resources
     * @param resourceTypes The type of the resources to be moved
     * @param number The number of resources to move
     * @throws TypeNotChangeableException In case the level is a fixed type level
     * @throws LevelTooSmallException In case the level has no space left for this resources
     * @throws NegativeResourceValueException In case the requested resources are not in the swap area
     */
    public void moveToLevel(int level, ResourceTypes resourceTypes, int number) throws TypeNotChangeableException, LevelTooSmallException, NegativeResourceValueException {
        Level l=warehouseDepots.get(level);
        l.addResources(number,resourceTypes);
    }

    /**
     * Method that gets the resources contained in this storage
     * @return The resources contained in this storage
     */
    public Resources getResources(){
        Resources sum=new Resources();
        for(Level level : warehouseDepots){
            sum=sum.add(level.getResources());
        }
        return sum;
    }

    public Resources getSwapDeposit(){
        return swapDeposit;
    }

    /**
     * Method used to add a level to the deposit
     * @param level Level object to be added
     */
    public void addLevel(Level level){
        warehouseDepots.add(level);
    }
}
