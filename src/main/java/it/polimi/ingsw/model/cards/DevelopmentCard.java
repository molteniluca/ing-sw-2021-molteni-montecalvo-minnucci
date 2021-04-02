package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.resources.Resources;
import jdk.jshell.spi.ExecutionControl.NotImplementedException;

public class DevelopmentCard extends Card{
    private final Resources cost;
    private final char type;
    private final int level;
    private final Resources productionCost;
    private final Resources productionPower;

    public DevelopmentCard(int victoryPoint, Resources cost, char type, int level, Resources productionCost, Resources productionPower) {
        super(victoryPoint);
        this.cost = cost;
        this.type = type;
        this.level = level;
        this.productionCost = productionCost;
        this.productionPower = productionPower;
    }

    public Resources getProductionCost() {
        return productionCost;
    }

    public Resources getProductionPower() {
        return productionPower;
    }

    public Boolean checkAvailability(Resources resource) throws NotImplementedException {
        throw new NotImplementedException("NON IMPLEMENTATO");
    }
}
