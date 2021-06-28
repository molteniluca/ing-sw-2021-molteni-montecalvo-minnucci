package it.polimi.ingsw.model.board.personal.storage;

import it.polimi.ingsw.model.exceptions.*;
import it.polimi.ingsw.model.resources.ResourceTypes;
import it.polimi.ingsw.model.resources.Resources;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class that represents the multilevel storage type
 */
public class WarehouseDepots implements Serializable {
    private static final long serialVersionUID = 6732146736278436295L;
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
        swapDeposit = swapDeposit.add(resource);
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
     * @throws ResourceTypeAlreadyPresentException In case there is already a level with this type
     */
    public void moveToLevel(int level, ResourceTypes resourceTypes, int number) throws TypeNotChangeableException, LevelTooSmallException, NegativeResourceValueException, ResourceTypeAlreadyPresentException {
        if(level<3) {
            for (int i = 0; i < 3; i++) {
                if (warehouseDepots.get(i).getResourceType() == resourceTypes && i!=level) {
                    throw new ResourceTypeAlreadyPresentException("Can't use a resource type on multiple levels");
                }
            }
        }

        if(number>0) {
            Level l = warehouseDepots.get(level);
            l.addResources(number, resourceTypes);
            swapDeposit = swapDeposit.sub(new Resources().set(resourceTypes, number));
        }else
            throw new NegativeResourceValueException("Trying to move " + number + " resources to a level");
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

    /**
     * Method that gets the resources contained in one specific warehouse level
     * @param level The level from where you get resources
     * @return New Resources with resources contained in the warehouse level required.
     */
    public Resources getResources(int level){
        Resources resources = new Resources();
        resources.add(warehouseDepots.get(level).getResources());
        return resources;
    }

    public int getNumberLevels(){
        return warehouseDepots.size();
    }

    public ResourceTypes getResourceTypeLevel(int level){
        return warehouseDepots.get(level).getResourceType();
    }

    public int getResourcesNumber(int level){
        return warehouseDepots.get(level).getResourceNumber();
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

    /**
     * This method removes resources from the warehouse depots levels
     * @param sub The resources to be removed
     * @throws NegativeResourceValueException In case the operation goes wrong
     */
    public void removeResources(Resources sub) throws NegativeResourceValueException {
        for(Level l : warehouseDepots){
            if(l.getResourceType()!=null) {
                int num = sub.getResourceNumber(l.getResourceType());
                num = Math.min(num, l.getResourceNumber());

                try {
                    l.removeResources(num, l.getResourceType());
                } catch (LevelTooSmallException | TypeNotChangeableException e) {
                    e.printStackTrace();
                }
                sub = sub.sub(new Resources().set(l.getResourceType(), num));
            }
        }
    }

    /**
     * Method that get the level requested
     * @param level the number of level
     * @return the level needed
     */
    public Level getLevel(int level){
        return warehouseDepots.get(level);
    }
}
