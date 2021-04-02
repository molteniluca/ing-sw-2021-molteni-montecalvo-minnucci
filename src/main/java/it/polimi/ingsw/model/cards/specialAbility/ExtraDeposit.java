package it.polimi.ingsw.model.cards.specialAbility;

import it.polimi.ingsw.model.resources.ResourceTypes;

public class ExtraDeposit implements SpecialAbility{
    private final ResourceTypes resourceType;

    public ExtraDeposit(ResourceTypes resourceType) {
        this.resourceType = resourceType;
    }

    public ResourceTypes getResourceType() {
        return resourceType;
    }
}
