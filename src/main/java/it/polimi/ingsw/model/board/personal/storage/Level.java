package it.polimi.ingsw.model.board.personal.storage;

import it.polimi.ingsw.model.resources.ResourceTypes;

public class Level {
    private ResourceTypes resourceType;
    private int resourceNumber;
    private final boolean fixedResource;

    public Level(boolean fixedResource) {
        this.fixedResource = fixedResource;
    }

    public ResourceTypes getResourceType() {
        return resourceType;
    }

    public void setResourceType(ResourceTypes resourceType) {
        this.resourceType = resourceType;
    }

    public int getResourceNumber() {
        return resourceNumber;
    }
}
