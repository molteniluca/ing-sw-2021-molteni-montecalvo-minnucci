package it.polimi.ingsw.model.board.personal;

import it.polimi.ingsw.model.cards.DevelopmentCard;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.model.cards.specialAbility.Discount;
import it.polimi.ingsw.model.cards.specialAbility.ExtraDeposit;
import it.polimi.ingsw.model.cards.specialAbility.ExtraProduction;
import it.polimi.ingsw.model.cards.specialAbility.ExtraResource;
import it.polimi.ingsw.model.resources.Resources;
import jdk.jshell.spi.ExecutionControl;
import java.util.ArrayList;

/**
 * Class that represents a board containing the leader cards
 */
public class LeaderBoard {
    private final ArrayList<LeaderCard> leaderCards;
    private final ArrayList<LeaderCard> leaderCardsInHand;
    private int victoryPoints = 0;

    public LeaderBoard(ArrayList<LeaderCard> leaderCardsInHand) {
        this.leaderCards = new ArrayList<>();
        this.leaderCardsInHand = leaderCardsInHand;
    }

    /**
     * Check and plays the leader card
     * @param leader The leader card to play
     * @param resources The resources required to play the card
     * @param cards The list of cards required to play the card
     */
    public void playLeader(LeaderCard leader, Resources resources, ArrayList<DevelopmentCard> cards)throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("NON IMPLEMENTATO");
    }

    /**
     * This method discards a leader card
     * @param leader LeaderCard to be discarded
     */
    public void discardLeader (LeaderCard leader){
        leaderCardsInHand.remove(leader);
        victoryPoints++;
    }

    /**
     * Method that gets the ExtraProduction effects
     * @return The list of the effects
     */
    public ArrayList<ExtraProduction> getProductionEffects(){
        ArrayList<ExtraProduction> effects = new ArrayList<>();
        for(LeaderCard card : this.leaderCards){
            if(card.getType()=='p')
                effects.add((ExtraProduction) card.getSpecialAbility());
        }
        return effects;
    }

    /**
     * Method that gets the Discount effects
     * @return The list of the effects
     */
    public ArrayList<Discount> getDiscountEffects(){
        ArrayList<Discount> effects = new ArrayList<>();
        for(LeaderCard card : this.leaderCards){
            if(card.getType() == 'd')
                effects.add((Discount) card.getSpecialAbility());
        }
        return effects;
    }

    /**
     * Method that gets the ExtraDeposit effects
     * @return The list of the effects
     */
    public ArrayList<ExtraDeposit> getExtraDepostEffects(){
        ArrayList<ExtraDeposit> effects = new ArrayList<>();
        for(LeaderCard card : this.leaderCards){
            if(card.getType() == 'e')
                effects.add((ExtraDeposit) card.getSpecialAbility());
        }
        return effects;
    }

    /**
     * Method that gets the ExtraResource effects
     * @return The list of the effects
     */
    public ArrayList<ExtraResource> getExtraResource(){
        ArrayList<ExtraResource> effects = new ArrayList<>();
        for(LeaderCard card : this.leaderCards){
            if(card.getType() == 'r')
                effects.add((ExtraResource) card.getSpecialAbility());
        }
        return effects;
    }

    public void activateLeaderEffect(LeaderCard leader)throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("NON IMPLEMENTATO");
    }
}
