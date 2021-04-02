package it.polimi.ingsw.model.cards.specialAbility;

import it.polimi.ingsw.model.resources.ResourceTypes;

public class Discount implements SpecialAbility{
    private final ResourceTypes resourceDiscount;

    public Discount(ResourceTypes resourceDiscount) {
        this.resourceDiscount = resourceDiscount;
    }

    public ResourceTypes getResourceDiscount() {
        return resourceDiscount;
    }
}
