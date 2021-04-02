package it.polimi.ingsw.model.board.general;

import it.polimi.ingsw.model.resources.Resources;

public class GeneralBoard {
    private final Market market;
    private final Lorenzo lorenzo;
    private final CardDealer cardDealer;

    public GeneralBoard(CardDealer cardDealer, Market market, Lorenzo lorenzo) {
        this.market = market;
        this.lorenzo = lorenzo;
        this.cardDealer = cardDealer;
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
}
