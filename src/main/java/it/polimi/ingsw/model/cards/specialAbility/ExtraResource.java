package it.polimi.ingsw.model.cards.specialAbility;

import it.polimi.ingsw.model.resources.ResourceTypes;

public class ExtraResource implements SpecialAbility{
    private final ResourceTypes resource;

    public ExtraResource(ResourceTypes resource) {
        this.resource = resource;
    }

    public ResourceTypes getResource() {
        return resource;
    }
}
