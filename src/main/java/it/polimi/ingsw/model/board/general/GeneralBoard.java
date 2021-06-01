package it.polimi.ingsw.model.board.general;

import java.io.Serializable;

public class GeneralBoard implements Serializable {
    private static final long serialVersionUID = 6732146736278436298L;
    private final Market market;
    private final CardDealer cardDealer;
    private transient final FaithObserver faithObserver;


    /**
     * Creates the general board and every element in it
     */
    public GeneralBoard(){
        CardDealer temp;
        market = new Market();
        temp = new CardDealer();
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
