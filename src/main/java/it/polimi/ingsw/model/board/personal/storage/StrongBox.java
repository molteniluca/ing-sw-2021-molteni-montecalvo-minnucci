package it.polimi.ingsw.model.board.personal.storage;

import it.polimi.ingsw.model.exceptions.FaithNotAllowedException;
import it.polimi.ingsw.model.exceptions.NegativeResourceValueException;
import it.polimi.ingsw.model.resources.ResourceTypes;
import it.polimi.ingsw.model.resources.Resources;

import java.io.Serializable;

/**
 * Class that represents the storage box
 */
public class StrongBox implements Serializable {
    private static final long serialVersionUID = 6732146736278436292L;
    private Resources resources;

    public StrongBox(){
        resources=new Resources().set(ResourceTypes.SHIELD,100000).set(ResourceTypes.STONE,100000).set(ResourceTypes.GOLD,100000).set(ResourceTypes.SERVANT,100000);
    }

    public Resources getResources(){
        return resources;
    }

    /**
     * Adds resources to the storage
     * @param resource The resources to be added
     */
    public void addResource(Resources resource) throws FaithNotAllowedException {
        if(resource.getResourceNumber(ResourceTypes.FAITH)!=0)
            throw new FaithNotAllowedException("Faith not allowed in the storage");
        this.resources=this.resources.add(resource);
    }

    /**
     * Removes a certain quantity of resources from the strongbox
     * @param resources The resources to be subtracted
     * @throws NegativeResourceValueException If there aren't enough resources
     */
    public void removeResource(Resources resources) throws NegativeResourceValueException {
        this.resources=this.resources.sub(resources);
    }
}
