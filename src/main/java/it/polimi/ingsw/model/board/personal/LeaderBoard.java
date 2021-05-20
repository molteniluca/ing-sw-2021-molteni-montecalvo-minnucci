package it.polimi.ingsw.model.board.personal;

import it.polimi.ingsw.model.board.personal.storage.Level;
import it.polimi.ingsw.model.cards.DevelopmentCard;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.model.cards.specialAbility.Discount;
import it.polimi.ingsw.model.cards.specialAbility.ExtraDeposit;
import it.polimi.ingsw.model.cards.specialAbility.ExtraProduction;
import it.polimi.ingsw.model.cards.specialAbility.ExtraResource;
import it.polimi.ingsw.model.exceptions.UnusableCardException;
import it.polimi.ingsw.model.resources.Resources;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class that represents a board containing the leader cards
 */
public class LeaderBoard implements Serializable {
    private final ArrayList<LeaderCard> leaderCards;
    private final ArrayList<LeaderCard> leaderCardsInHand;
    private transient final PersonalBoard personalBoard;
    private int victoryPoints = 0;

    public LeaderBoard(ArrayList<LeaderCard> leaderCardsInHand, PersonalBoard personalBoard) {
        this.leaderCards = new ArrayList<>();
        this.leaderCardsInHand = leaderCardsInHand;
        this.personalBoard = personalBoard;
    }

    /**
     * Check and plays the leader card
     * @param leader The leader card to play
     * @param resources The resources required to play the card
     * @param cards The list of cards required to play the card
     */
    public void playLeader(LeaderCard leader, Resources resources, ArrayList<DevelopmentCard> cards) throws UnusableCardException {
        if(checkPlayLeader(leader,resources,cards)){
            leaderCardsInHand.remove(leader);
            leaderCards.add(leader);
            activateLeaderEffect(leader);
        }else{
            throw new UnusableCardException("The leader card is not playable");
        }
    }

    /**
     * This method checks whether a leader card is playable with the current resources and cards
     * @param leader The leader to be played
     * @param resources The resources to check
     * @param cards The card list to check
     * @return True if is playable and false if not
     */
    public boolean checkPlayLeader(LeaderCard leader, Resources resources, ArrayList<DevelopmentCard> cards){
        return leaderCardsInHand.contains(leader) && leader.checkRequirements(resources,cards);
    }

    public ArrayList<LeaderCard> getLeaderCardsInHand() {
        return leaderCardsInHand;
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
     * This method discards a leader card
     * @param leaderIndex LeaderCard index to be discarded
     */
    public void discardLeader (int leaderIndex) throws IndexOutOfBoundsException{
        discardLeader(leaderCardsInHand.get(leaderIndex));
    }

    /**
     * Method that gets the ExtraProduction effects
     * @return The list of the effects
     */
    public ArrayList<ExtraProduction> getProductionEffects(){
        ArrayList<ExtraProduction> effects = new ArrayList<>();
        for(LeaderCard card : this.leaderCards){
            if(card.getSpecialAbility().applyExtraProduction()!=null)
                effects.add(card.getSpecialAbility().applyExtraProduction());
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
            if(card.getSpecialAbility().applyDiscount()!=null)
                effects.add(card.getSpecialAbility().applyDiscount());
        }
        return effects;
    }

    /**
     * Method that gets the ExtraDeposit effects
     * @return The list of the effects
     */
    public ArrayList<ExtraDeposit> getExtraDepositEffects(){
        ArrayList<ExtraDeposit> effects = new ArrayList<>();
        for(LeaderCard card : this.leaderCards){
            if(card.getSpecialAbility().applyExtraDeposit()!=null)
                effects.add(card.getSpecialAbility().applyExtraDeposit());
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
            if(card.getSpecialAbility().applyExtraResource()!=null)
                effects.add(card.getSpecialAbility().applyExtraResource());
        }
        return effects;
    }

    private void activateLeaderEffect(LeaderCard leader) {
        if(leader.getSpecialAbility().applyExtraDeposit()!=null){
            personalBoard.getDeposit().getStorage().addLevel(new Level(leader.getSpecialAbility().applyExtraDeposit().getResourceType(),2));
        }
    }

    public int getVictoryPoint() {
        for(LeaderCard l : leaderCards){
            victoryPoints+=l.getVictoryPoint();
        }
        return victoryPoints;
    }
}
