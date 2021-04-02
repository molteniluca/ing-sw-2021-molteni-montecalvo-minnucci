package it.polimi.ingsw.model.cards;

public abstract class Card {

    private final int victoryPoint;
    private boolean isSelected;

    public Card(int victoryPoint) {
        this.victoryPoint = victoryPoint;
        this.isSelected = false;
    }

    public int getVictoryPoint() {
        return victoryPoint;
    }
}
