package it.polimi.ingsw.model.cards.specialAbility;

import it.polimi.ingsw.model.resources.ResourceTypes;
import it.polimi.ingsw.model.resources.Resources;

/**
 * A specialAbility that allows player to produce more (one more production
 * available, player can decide to use it or not)
 */
public class ExtraProduction implements SpecialAbility{
    private final ResourceTypes productionCost;

    /**
     * Constructor of ExtraProduction
     * @param productionCost the resource required to produce with this specialEffect
     */
    public ExtraProduction(ResourceTypes productionCost) {
        this.productionCost = productionCost;
    }

    /**
     * Getter of productionCost for ExtraProduction
     * @return the resource required to produce with this specialEffect
     */
    public ResourceTypes getProductionCost() {
        return productionCost;
    }

    public ExtraProduction applyExtraProduction(){
        return this;
    }

}
