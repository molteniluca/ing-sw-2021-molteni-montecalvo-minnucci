package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.resources.Resources;

/**
 * DevelopmentCard is a subclass of Card, its aim is to produce new resources.
 * It has a cost and rules for its production
 */
public class DevelopmentCard extends Card{
    private final Resources cost;   //in the top of card, number and type of resource required to obtain DevelopmentCard
    private final char type;    //b = blue, g = green, p = purple, y = yellow
    private final int level;    //number of dots in the flags
    private final Resources productionCost;    //resources in the left page of book, to give
    private final Resources productionPower;   //resources in the right page of book, to receive

    /**
     * Constructor of DevelopmentCard class
     * @param victoryPoint Victory Point of Card
     * @param image name of image file associated
     * @param cost cost in term of resources to buy the Card
     * @param type color of flag's Card, b = blue, g = green, p = purple, y = yellow
     * @param level number of dots in flag's Card (if none, level = 1)
     * @param productionCost resources needed for production
     * @param productionPower resources obtained from production
     */
    public DevelopmentCard(int victoryPoint, String image, Resources cost, char type, int level, Resources productionCost, Resources productionPower) {
        super(victoryPoint, image);
        this.cost = cost;
        this.type = type;
        this.level = level;
        this.productionCost = productionCost;
        this.productionPower = productionPower;
    }

    /**
     * Getter of type
     * @return type (color) of DevelopmentCard
     */
    public char getType() {
        return type;
    }

    /**
     * Getter of Level
     * @return level (number of dots in flags, if none --> level = 1) of DevelopmentCard
     */
    public int getLevel() {
        return level;
    }

    /**
     * Getter of production cost
     * @return resources needed for production
     */
    public Resources getProductionCost() {
        return productionCost;
    }

    /**
     * Getter of production power
     * @return resources obtain from production
     */
    public Resources getProductionPower() {
        return productionPower;
    }

    /**
     * Check if is it possible to use the production of the DevelopmentCard
     * @param resource resources available for the player to produce, NB: resource must be only resources available at all: not used for any other production!
     * @return boolean true or false possibility to use that one DevelopmentCard with that resources available
     */
    public Boolean checkAvailability(Resources resource) {
        return resource.isSubPositive(getProductionCost());
    }
}
