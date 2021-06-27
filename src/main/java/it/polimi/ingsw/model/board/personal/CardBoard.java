package it.polimi.ingsw.model.board.personal;

import it.polimi.ingsw.model.cards.DevelopmentCard;
import it.polimi.ingsw.model.exceptions.IncompatibleCardLevelException;
import it.polimi.ingsw.model.exceptions.WinException;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class that represent a production deck of a player
 */
public class CardBoard implements Serializable {
    private static final long serialVersionUID = 6732146736278436286L;
    private final ArrayList<DevelopmentCard>[] productionCards;
    private int numberOfCards=0;

    @SuppressWarnings("unchecked") //can't create array of typed lists
    public CardBoard(){
        productionCards = new ArrayList[3];
        for (int i=0;i<3;i++) {
            productionCards[i]=new ArrayList<>();
        }
    }

    /**
     * Method that inserts a card in a specified place
     * @param card The card to be inserted
     * @param place The place to insert the card
     * @throws IncompatibleCardLevelException In case of an out of rule move
     */
    public void insertCard(DevelopmentCard card, int place) throws IncompatibleCardLevelException, WinException {
        if(productionCards[place].size()==card.getLevel()-1) {
            productionCards[place].add(card);
            numberOfCards++;
        }
        else
            throw new IncompatibleCardLevelException( "Trying to put an incompatible level card on to another");
        if(numberOfCards==7){
            throw new WinException("You bought seven cards!");
        }
    }

    /**
     * Method that returns all the development cards in a board
     * @return The arraylist containing all the development cards
     */
    public ArrayList<DevelopmentCard> getDevelopmentCards(){
        ArrayList<DevelopmentCard> cards = new ArrayList<>();
        for(ArrayList<DevelopmentCard> arr : productionCards) {
            cards.addAll(arr);
        }
        return cards;
    }

    /**
     * Method that returns all the development cards in a board
     * @return The matrix containing all the development cards
     */
    public ArrayList<DevelopmentCard>[] getDevelopmentCardsMatrix(){
        return productionCards;
    }

    /**
     * Method that gets the visible development cards
     * @return The array of size 3 of the visible cards
     */
    public DevelopmentCard[] getUpperDevelopmentCards(){
        DevelopmentCard[] cards = new DevelopmentCard[3];
        for(int i=0;i<3;i++) {
            if(productionCards[i].size()==0)
                cards[i]=null;
            else
                cards[i]=productionCards[i].get(productionCards[i].size()-1);
        }
        return cards;
    }

    /**
     * A method that counts all the victory points given by the Development Cards
     * @return The sum of the victory points
     */
    public int getAllVictoryPoint(){
        int victoryPoints=0;
        for(ArrayList<DevelopmentCard> arr : productionCards) {
            for(DevelopmentCard card : arr){
                victoryPoints+=card.getVictoryPoint();
            }
        }
        return victoryPoints;
    }
}
