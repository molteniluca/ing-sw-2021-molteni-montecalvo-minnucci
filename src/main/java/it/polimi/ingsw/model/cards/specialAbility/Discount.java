package it.polimi.ingsw.model.cards.specialAbility;

import it.polimi.ingsw.model.resources.ResourceTypes;

/**
 * A specialAbility that discounts the cost of purchasable DevelopmentCard
 */
public class Discount implements SpecialAbility{
    private final ResourceTypes resourceDiscount;

    /**
     * Constructor of Discount
     * @param resourceDiscount resource already paid with discount
     */
    public Discount(ResourceTypes resourceDiscount) {
        this.resourceDiscount = resourceDiscount;
    }

    /**
     * Getter of resourceDiscount
     * @return resource connected to discount
     */
    public ResourceTypes getResourceDiscount() {
        return resourceDiscount;
    }

}
