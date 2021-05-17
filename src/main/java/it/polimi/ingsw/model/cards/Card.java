package it.polimi.ingsw.model.cards;

import java.io.Serializable;

/**
 * Abstract Class of Card, DevelopmentCard and LeaderCard are subclasses of Card
 */
public abstract class Card implements Serializable {

    private final int victoryPoint;
    private boolean isSelected;
    private final String image;

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
     * @param image file name associated
     */
    public Card(int victoryPoint, String image) {
        this.victoryPoint = victoryPoint;
        this.image = image;
        this.isSelected = false;
    }

    /**
     * Getter of VictoryPoint's Card
     * @return Victory Points of Card
     */
    public int getVictoryPoint() {
        return victoryPoint;
    }

    /**
     * Getter of String image
     * @return the name of image file associated
     */
    public String getImage() {
        return image;
    }
}
