package it.polimi.ingsw.model.board.general;

import it.polimi.ingsw.model.resources.Resources;

import java.io.FileNotFoundException;
import java.io.Serializable;

public class GeneralBoard implements Serializable {
    private final Market market;
    private final CardDealer cardDealer;
    private transient final FaithObserver faithObserver;


    /**
     * Creates the general board and every element in it
     */
    public GeneralBoard(){
        CardDealer temp;
        market = new Market();
        try {
            temp = new CardDealer();
        } catch (FileNotFoundException e) {
            temp = null;
            e.printStackTrace();
            System.exit(-1);
        }
        cardDealer = temp;
        this.faithObserver = new FaithObserver();
    }

    public Market getMarket() {
        return market;
    }

    public CardDealer getCardDealer() {
        return cardDealer;
    }

    public FaithObserver getFaithObserver() {
        return faithObserver;
    }
}
