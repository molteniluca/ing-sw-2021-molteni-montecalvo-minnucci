package it.polimi.ingsw.model.cards.specialAbility;

import it.polimi.ingsw.model.resources.ResourceTypes;

import java.io.Serializable;

/**
 *  A specialAbility that gives a resource instead of white marble in the market when you get one of them
 */
public class ExtraResource implements SpecialAbility, Serializable {
    private static final long serialVersionUID = 6732146736278436284L;
    private final ResourceTypes resource;

    /**
     * Constructor of ExtraResources
     * @param resource resource that replace white marble in market
     */
    public ExtraResource(ResourceTypes resource) {
        this.resource = resource;
    }

    /**
     * Getter of resource for ExtraResource
     * @return the resource that replace white marble in market
     */
    public ResourceTypes getResource() {
        return resource;
    }

    public ExtraResource applyExtraResource(){
        return this;
    }
}
