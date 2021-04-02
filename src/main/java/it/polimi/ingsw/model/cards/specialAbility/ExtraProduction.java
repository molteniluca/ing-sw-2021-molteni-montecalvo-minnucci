package it.polimi.ingsw.model.cards.specialAbility;

import it.polimi.ingsw.model.resources.Resources;

public class ExtraProduction implements SpecialAbility{
    private final Resources productionCost;

    public ExtraProduction(Resources productionCost) {
        this.productionCost = productionCost;
    }

    public Resources getProductionCost() {
        return productionCost;
    }
}
