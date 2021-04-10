package it.polimi.ingsw.model.cards;

public class DevelopmentCardRequirement {
    private final char type;
    private final int level;

    /**
     * Constructor of DevelopmentCardRequirement
     * @param type color of flags required
     * @param level level (numbers of dots in flags) required
     */
    public DevelopmentCardRequirement(char type, int level){
        this.type = type;
        this.level = level;
    }

    /**
     * Getter of type (color) of DevelopmentCardRequirement
     * @return the color of flags required
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
