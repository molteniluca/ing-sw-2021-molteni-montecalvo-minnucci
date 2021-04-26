package it.polimi.ingsw.model.board.general;

import it.polimi.ingsw.model.resources.Resources;

import java.io.FileNotFoundException;

public class GeneralBoard {
    private final Market market;
    private final Lorenzo lorenzo;
    private final CardDealer cardDealer;
    private final FaithObserver faithObserver;


    /**
     * Creates the general board and every element in it
     * @throws FileNotFoundException if the constructor of CardDealer does not found the file
     */
    public GeneralBoard() throws FileNotFoundException {
        market = new Market();
        cardDealer = new CardDealer();
        this.lorenzo = new Lorenzo();
        this.faithObserver = new FaithObserver();
    }

    public Market getMarket() {
        return market;
    }

    public Lorenzo getLorenzo() {
        return lorenzo;
    }

    public CardDealer getCardDealer() {
        return cardDealer;
    }

    public FaithObserver getFaithObserver() {
        return faithObserver;
    }
}
