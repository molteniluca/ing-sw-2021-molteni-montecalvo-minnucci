package it.polimi.ingsw.model.cards;

/**
 * Abstract Class of Card, DevelopmentCard and LeaderCard are subclasses of Card
 */
public abstract class Card {

    private final int victoryPoint;
    private boolean isSelected;

    /**
     * Getter of IsSelected of card
     * @return boolean true or false answering if it's selected or not
     */
    public boolean getIsSelected() {return isSelected;}

    /**
     * Setter of IsSelected of card
     * @param selected boolean true or false to select or not the card
     */

    public void setIsSelected(boolean selected) {isSelected = selected;}

    /**
     * Constructor of Card
     * @param victoryPoint Victory Point of Card, any card has them
     */
    public Card(int victoryPoint) {
        this.victoryPoint = victoryPoint;
        this.isSelected = false;
    }

    /**
     * Getter of VictoryPoint's Card
     * @return Victory Points of Card
     */
    public int getVictoryPoint() {
        return victoryPoint;
    }


}
