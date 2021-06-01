package it.polimi.ingsw.model.cards;

import java.io.Serializable;

/**
 * Class of DevelopmentCardRequirement. It stands for types and levels of DevelopmentCards required
 * to activate LeaderCard.
 */
public class DevelopmentCardRequirement implements Serializable {
    private static final long serialVersionUID = 6732146736278436280L;
    private final char type;
    private int level;

    /**
     * Constructor of DevelopmentCardRequirement for colors and level requirements
     * @param type color of flags required: b = blue, g = green, p = purple, y = yellow
     * @param level level (numbers of dots in flags) required
     */
    public DevelopmentCardRequirement(char type, int level){
        this.type = type;
        this.level = level;
    }

    /**
     * Constructor of DevelopmentCardRequirement for only colors requirements
     * @param type color of flags required: b = blue, g = green, p = purple, y = yellow
     */
    public DevelopmentCardRequirement(char type){
        this.type = type;
    }

    /**
     * Getter of type (color) of DevelopmentCardRequirement
     * @return the color of flags required: b = blue, g = green, p = purple, y = yellow
     */
    public char getType() {
        return type;
    }

    /**
     * Getter of level of DevelopmentCardRequirement
     * @return the level (number of dots in flag) required
     */
    public int getLevel() {
        return level;
    }
}
