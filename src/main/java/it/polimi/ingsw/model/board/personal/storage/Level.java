package it.polimi.ingsw.model.board.personal.storage;

import it.polimi.ingsw.model.exceptions.LevelTooSmallException;
import it.polimi.ingsw.model.exceptions.NegativeResourceValueException;
import it.polimi.ingsw.model.exceptions.TypeNotChangeableException;
import it.polimi.ingsw.model.resources.ResourceTypes;
import it.polimi.ingsw.model.resources.Resources;

import java.io.Serializable;

/**
 * Class that represents a level of storage
 */
public class Level implements Serializable {
    private static final long serialVersionUID = 6732146736278436291L;
    private ResourceTypes resourceType;
    private int resourceNumber;
    private final boolean fixedResource;
    private final int maxResourceNumber;

    /**
     * Constructor of the class in case the level has no fixed resource type
     * @param maxResourceNumber The maximum number of resources that this level can contain
     */
    public Level(int maxResourceNumber) {
        this.fixedResource = false;
        this.maxResourceNumber= maxResourceNumber;
    }

    /**
     * Constructor of the class in case has fixed resource type
     * @param resourceType Fixed resource type
     * @param maxResourceNumber The maximum number of resources that this level can contain
     */
    public Level(ResourceTypes resourceType,int  maxResourceNumber) {
        this.fixedResource = true;
        this.resourceType = resourceType;
        this.maxResourceNumber= maxResourceNumber;
    }

    public int getResourceNumber() {
        return resourceNumber;
    }

    public ResourceTypes getResourceType() {
        if(resourceNumber==0)
            return null;
        else
            return resourceType;
    }

    /**
     * Method that returns the resources contained in the level
     * @return The resources contained in this level
     */
    public Resources getResources() {
        Resources res =new Resources();
        res.set(resourceType,resourceNumber);
        return res;
    }

    /**
     * Method that returns the resources contained in the level and erases the content
     * @return The resources contained in this level
     */
    public Resources getResourcesAndErase() {
        Resources res =new Resources();
        res.set(resourceType,resourceNumber);
        resourceNumber=0;
        resourceType=null;
        return res;
    }

    private void setResourceType(ResourceTypes resourceType) throws TypeNotChangeableException {
        if(resourceType!=this.resourceType)
            if(!fixedResource)
                if(resourceNumber==0)
                    this.resourceType = resourceType;
                else
                    throw new TypeNotChangeableException("Trying to change type to a nonempty level.");
            else
                throw new TypeNotChangeableException("Trying to change a fixed resource type level.");
    }

    private void setResourceNumber(int resourceNumber) throws LevelTooSmallException, NegativeResourceValueException {
        if(resourceNumber<0){
            throw new NegativeResourceValueException("Trying to assign a negative value to a level");
        }
        if(resourceNumber<=maxResourceNumber)
            this.resourceNumber = resourceNumber;
        else
            throw new LevelTooSmallException("Trying to put too much resources in a level");
    }

    public void addResources(int resourceNumber, ResourceTypes resourceType) throws LevelTooSmallException, NegativeResourceValueException, TypeNotChangeableException {
        this.setResourceType(resourceType);
        this.setResourceNumber(this.resourceNumber + resourceNumber);
    }

    public void removeResources(int resourceNumber, ResourceTypes resourceType) throws NegativeResourceValueException, LevelTooSmallException, TypeNotChangeableException {
        this.setResourceType(resourceType);
        this.setResourceNumber(this.resourceNumber - resourceNumber);
    }

    public boolean isFixedResource() {
        return fixedResource;
    }

    public int getMaxResourceNumber() {
        return maxResourceNumber;
    }
}
