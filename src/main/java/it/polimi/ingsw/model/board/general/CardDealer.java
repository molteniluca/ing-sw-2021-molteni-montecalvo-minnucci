package it.polimi.ingsw.model.board.general;

import it.polimi.ingsw.model.cards.DevelopmentCard;

import java.util.LinkedList;

public class CardDealer {
    private LinkedList<DevelopmentCard>[][] cardMatrix;

    public CardDealer() {
        this.cardMatrix = new LinkedList[3][4]; // Can't define Arrays of typed lists

        for (int i=0; i<3; i++) {
            for (int k=0; k<4; k++) {
                this.cardMatrix[i][k]=new LinkedList<>();
            }
        }
    }

    public DevelopmentCard drawCard(int row, int column){
        return this.cardMatrix[row][column].pop();
    }


}
