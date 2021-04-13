package it.polimi.ingsw.model.board;

import it.polimi.ingsw.model.board.general.FaithObserver;
import it.polimi.ingsw.model.exceptions.FaithOverflowException;

/**
 * Class that represents the faith of a player
 */
public class FaithTrack {
    private final int[] faithCards;
    private int position;
    private final FaithObserver faithObserver;

    public FaithTrack(FaithObserver faithObserver) {
        this.faithCards = new int[]{0,0,0}; /* 0: card turned upside down; 1: card in place; 2: card discarded*/
        this.position = 0;
        this.faithObserver=faithObserver;
    }

    /**
     * Gets all victory points of this section
     * @return The victory points
     */
    public int getVictoryPoint(){
        int points=0;
        if(faithCards[0]==1)
            points+=2;
        if(faithCards[1]==1)
            points+=3;
        if(faithCards[2]==1)
            points+=4;
        switch (position){
            case 3:
                points+=1;
                break;
            case 6:
                points+=2;
                break;
            case 9:
                points+=4;
                break;
            case 12:
                points+=6;
                break;
            case 15:
                points+=9;
                break;
            case 18:
                points+=12;
                break;
            case 21:
                points+=16;
                break;
            case 24:
                points+=20;
                break;
        }
        return points;
    }

    public int getPosition(){
        return this.position;
    }

    /**
     * Method that is invoked when other players get at the end of a faith zone
     * @param zone The triggered faith zone
     */
    public void checkRelationship(int zone){
        switch (zone){
            case 0:
                if(position>4 && position<9)
                    faithCards[0]=1;
                else
                    faithCards[0]=2;
                break;
            case 1:
                if(position>11 && position<17)
                    faithCards[1]=1;
                else
                    faithCards[1]=2;
                break;
            case 2:
                if(position>18)
                    faithCards[2]=1;
                else
                    faithCards[2]=2;
                break;
        }
    }

    /**
     * Increment the position of the player
     * @param increment The faith points to be added
     * @throws FaithOverflowException In case the faith goes out of bounds
     */
    public void incrementPosition(int increment) throws FaithOverflowException {
        if(position+increment>24)
            throw new FaithOverflowException("Trying to exceed faith boundaries");
        this.position+=increment;
        switch(this.position){
            case 8:
                if(this.faithCards[0]==0)
                    this.faithObserver.notify(0);
                break;
            case 16:
                if(this.faithCards[1]==0)
                    this.faithObserver.notify(1);
                break;
            case 24:
                if(this.faithCards[2]==0)
                    this.faithObserver.notify(2);
                break;
        }
    }

    public int[] getFaithCards(){
        return faithCards;
    }
}
