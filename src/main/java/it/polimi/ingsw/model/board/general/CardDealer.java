package it.polimi.ingsw.model.board.general;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import it.polimi.ingsw.model.cards.DevelopmentCard;
import it.polimi.ingsw.model.exceptions.NotEnoughCardException;
import it.polimi.ingsw.model.resources.Resources;

import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.*;

public class CardDealer implements Serializable {
    private static final long serialVersionUID = 6732146736278436296L;
    private final Stack<DevelopmentCard>[][] cardMatrix; //STACK
    private static final int ROWS = 3;
    private static final int COLUMNS = 4;

    /**
     * Creates and populates the CardDealer using DevelopmentCards obtained from the Json file Cards
     */
    @SuppressWarnings("unchecked")
    public CardDealer(){
        //Can't define Arrays of typed lists
        //Read from the JsonFile of cards
        Gson gson = new Gson();
        JsonReader reader = new JsonReader(new InputStreamReader(Objects.requireNonNull(ClassLoader.getSystemResourceAsStream("json/developmentCards.json"))));
        List<DevelopmentCard> data = gson.fromJson(reader,  new TypeToken<ArrayList<DevelopmentCard>>(){}.getType());

        //shuffles the cards before adding them in the stack
        Collections.shuffle(data);


        //creates the matrix and the stack
        cardMatrix = new Stack[ROWS][COLUMNS];
        for (int i=0; i<ROWS; i++) {
            for (int k=0; k<COLUMNS; k++) {
                cardMatrix[i][k] = new Stack<>();
            }
        }

        //fill the stacks with Development cards
        for (DevelopmentCard card: data)
        {
            switch (card.getType()) {
                case 'g':
                    switch (card.getLevel()) {
                        case 1:
                            cardMatrix[2][0].push(card);
                            break;
                        case 2:
                            cardMatrix[1][0].push(card);
                            break;
                        case 3:
                            cardMatrix[0][0].push(card);
                            break;
                    }
                    break;
                case 'b':
                    switch (card.getLevel()) {
                        case 1:
                            cardMatrix[2][1].push(card);
                            break;
                        case 2:
                            cardMatrix[1][1].push(card);
                            break;
                        case 3:
                            cardMatrix[0][1].push(card);
                            break;
                    }
                    break;
                case 'y':
                    switch (card.getLevel()) {
                        case 1:
                            cardMatrix[2][2].push(card);
                            break;
                        case 2:
                            cardMatrix[1][2].push(card);
                            break;
                        case 3:
                            cardMatrix[0][2].push(card);
                            break;
                    }
                    break;
                case 'p':
                    switch (card.getLevel()) {
                        case 1:
                            cardMatrix[2][3].push(card);
                            break;
                        case 2:
                            cardMatrix[1][3].push(card);
                            break;
                        case 3:
                            cardMatrix[0][3].push(card);
                            break;
                    }
                    break;
            }
        }

    }


    /**
     * Method that returns the cost of a specific element of cardMatrix
     * @param row cardMatrix row
     * @param column cardMatrix column
     * @return the cost of the first element in the stack of the chosen cardMatrix cell
     */
    public Resources getCost(int row, int column)
    {
        return cardMatrix[row][column].peek().getCost();
    }


    public Stack<DevelopmentCard>[][] getCardMatrix()
    {
        return cardMatrix;
    }

    /**
     * Pops one of the first cards in cardMatrix
     * @param row cardMatrix row
     * @param column cardMatrix column
     * @return the selected card in cardMatrix
     * @throws IndexOutOfBoundsException in case one between the provided row and column is out of bounds
     * @throws EmptyStackException if the stack is empty
     */
    public DevelopmentCard drawCard(int row, int column) throws IndexOutOfBoundsException, EmptyStackException, NotEnoughCardException {
        DevelopmentCard card = this.cardMatrix[row][column].pop();
        for(int i=0; i<ROWS; i++) {
            if (!this.cardMatrix[i][column].isEmpty()) {
                return card;
            }
        }
        throw new NotEnoughCardException("Not Enough Cards in CardDealer!");
    }


    /**
     * Method that discards a couple of DevelopmentCard, associated with an ActionTokens in SelfPlayingTurn
     * It start discarding cards from the bottom of one column of cardMatrix, associated with the type, and then
     * if the stack is empty goes up on the column
     * @param type the cardType that has to be discarded
     */
    public void discardDevelopment(char type) throws NotEnoughCardException {
        int stillDiscard = 2; //number of cards that must be discarded

        switch (type) {
            case 'g':
                for(int i=2; i>0; i--)
                {
                    while (!cardMatrix[i][0].empty() && stillDiscard > 0)
                    {
                        cardMatrix[i][0].pop();
                        stillDiscard--;

                    }

                }
                if(stillDiscard >0 )
                    throw new NotEnoughCardException("Not Enough Cards in CardDealer");
                break;

            case 'b':
                for(int i=2; i>0; i--)
                {
                    while (!cardMatrix[i][1].empty() && stillDiscard > 0)
                    {
                        cardMatrix[i][1].pop();
                        stillDiscard--;

                    }

                }
                if(stillDiscard >0 )
                    throw new NotEnoughCardException("Not Enough Cards in CardDealer");
                break;

            case 'y':
                for(int i=2; i>0; i--)
                {
                    while (!cardMatrix[i][2].empty() && stillDiscard > 0)
                    {
                        cardMatrix[i][2].pop();
                        stillDiscard--;

                    }

                }
                if(stillDiscard >0 )
                    throw new NotEnoughCardException("Not Enough Cards in CardDealer");
                break;

            case 'p':
                for(int i=2; i>0; i--)
                {
                    while (!cardMatrix[i][3].empty() && stillDiscard > 0)
                    {
                        cardMatrix[i][3].pop();
                        stillDiscard--;

                    }
                }
                if(stillDiscard >0 )
                    throw new NotEnoughCardException("Not Enough Cards in CardDealer");
                break;
        }
    }

}
