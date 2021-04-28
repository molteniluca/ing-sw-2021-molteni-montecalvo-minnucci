package it.polimi.ingsw.model.cards.specialAbility;

import it.polimi.ingsw.model.resources.ResourceTypes;

/**
 * A specialAbility that gives a new special 2-slot depot that must contains the resource indicated.
 * Player can fill it with resources that he already have in other level of depot.
 */
public class ExtraDeposit implements SpecialAbility{
    private final ResourceTypes resourceType;

    /**
     * Constructor of ExtraDeposit
     * @param resourceType type of resource that can be store in the extra 2-slot of depot
     */
    public ExtraDeposit(ResourceTypes resourceType) {
        this.resourceType = resourceType;
    }

    /**
     * Getter of resourceType for ExtraDeposit
     * @return the type of resource that can be store in the extra 2-slot of depot
     */
    public ResourceTypes getResourceType() {
        return resourceType;
    }

    public ExtraDeposit applyExtraDeposit(){
        return this;
    }
}
