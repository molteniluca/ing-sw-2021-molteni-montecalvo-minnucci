package it.polimi.ingsw.model.board.personal;

import it.polimi.ingsw.model.board.general.FaithObserver;
import it.polimi.ingsw.model.board.general.GeneralBoard;
import it.polimi.ingsw.model.cards.DevelopmentCard;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.model.cards.specialAbility.Discount;
import it.polimi.ingsw.model.cards.specialAbility.ExtraResource;
import it.polimi.ingsw.model.exceptions.*;
import it.polimi.ingsw.model.resources.ResourceTypes;
import it.polimi.ingsw.model.resources.Resources;
import jdk.jshell.spi.ExecutionControl;

import java.util.ArrayList;
import java.util.Arrays;

public class PersonalBoard{
    private final FaithTrack faithTrack;
    private final Deposit deposit;
    private final CardBoard cardBoard;
    private final LeaderBoard leaderBoard;
    private final GeneralBoard generalBoard;

    public PersonalBoard(GeneralBoard generalBoard, ArrayList<LeaderCard> cardsInHand) {
        this.faithTrack = new FaithTrack(new FaithObserver());
        this.deposit = new Deposit();
        this.cardBoard = new CardBoard();
        this.leaderBoard = new LeaderBoard(cardsInHand,this);
        this.generalBoard = generalBoard;
    }

    public FaithTrack getFaithTrack() {
        return faithTrack;
    }

    public Deposit getDeposit() {
        return deposit;
    }

    public CardBoard getCardBoard() {
        return cardBoard;
    }

    public GeneralBoard getGeneralBoard() {
        return generalBoard;
    }

    public LeaderBoard getLeaderBoard() {
        return leaderBoard;
    }
    
    /**
     * This method produces resources using a development card
     * @param card The development card
     * @throws UnusableCardException In case the ard is not contained in the visible part of the board
     * @throws FaithOverflowException In case a card returns too much faith
     */
    public void produce(DevelopmentCard card) throws UnusableCardException, FaithOverflowException {
        if(Arrays.asList(cardBoard.getUpperDevelopmentCards()).contains(card)){
            if(checkProduce(card)){
                Resources cost=handleDiscount(card.getProductionCost());

                try {
                    deposit.removeResources(cost);
                } catch (NegativeResourceValueException e) {
                    e.printStackTrace();
                }

                this.faithTrack.incrementPosition(card.getProductionPower().getEraseFaith());

                try {
                    deposit.getChest().addResource(card.getProductionPower());
                } catch (FaithNotAllowedException e) {
                    e.printStackTrace();
                }
            }
        }
        else{
            throw new UnusableCardException("Can't use cards not in the visible area of the board");
        }
    }

    /**
     * Handles the discount given from a leader card
     * @param cost The cost of the production
     * @return The new cost
     */
    private Resources handleDiscount(Resources cost){
        for (Discount r:this.leaderBoard.getDiscountEffects()) {
            if(cost.isSubPositive(new Resources().set(r.getResourceDiscount(),1))){
                try {
                    cost=cost.sub(new Resources().set(r.getResourceDiscount(),1));
                } catch (NegativeResourceValueException e) {
                    e.printStackTrace();
                }
            }
        }
        return cost;
    }

    /**
     * Checks if a card can be utilized for production
     * @param card Card to check
     * @return True if you can produce and false if not
     */
    public boolean checkProduce(DevelopmentCard card){
        return deposit.checkRemoveResource(handleDiscount(card.getProductionCost()));
    }

    /**
     * Produces resources using the default production
     * @param resource1 The first resource in input
     * @param resource2 The second resource in input
     * @param output The resource in output
     * @throws FaithNotAllowedException In case of faith as parameter
     */
    public void produce(ResourceTypes resource1, ResourceTypes resource2, ResourceTypes output) throws FaithNotAllowedException {
        if(deposit.checkRemoveResource(new Resources().set(resource1,1).set(resource2,1))){
            try {
                deposit.removeResources(handleDiscount(new Resources().set(resource1,1).set(resource2,1)));
            } catch (NegativeResourceValueException e) {
                e.printStackTrace();
            }
            deposit.getChest().addResource(new Resources().set(output,1));
        }
    }

    /**
     * Checks if the leader effect is active and produces faith and the specified resource in output
     * @param resource1 The input resource
     * @param output The output resource
     */
    public void produce(ResourceTypes resource1, ResourceTypes output) throws FaithOverflowException {
        if(this.getLeaderBoard().getProductionEffects().stream().anyMatch(extraProduction -> extraProduction.getProductionCost().equals(resource1))){
            if(output==ResourceTypes.FAITH)
                faithTrack.incrementPosition(1);
            else {
                try {
                    deposit.getStorage().addResourceSwap(new Resources().set(output,1));
                } catch (FaithNotAllowedException e) {
                    e.printStackTrace();
                }
            }
            try {
                this.deposit.getChest().addResource(handleDiscount(new Resources().set(resource1,1)));
            } catch (FaithNotAllowedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This method buys a column from the market and adds the resources to the swap area
     * @param column The column to be bought
     */
    public void buyColumn(int column, ExtraResource effect) throws FaithOverflowException {
        Resources res=this.generalBoard.getMarket().buyColumn(column);
        this.faithTrack.incrementPosition(res.getEraseFaith());
        res=handleBlank(res,effect);
        try {
            this.deposit.getStorage().addResourceSwap(res);
        } catch (FaithNotAllowedException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method buys a row from the market and adds the resources to the swap area
     * @param row The row to be bought
     */
    public void buyRow(int row, ExtraResource effect) throws FaithOverflowException {
        Resources res=this.generalBoard.getMarket().buyRow(row);
        this.faithTrack.incrementPosition(res.getEraseFaith());
        res=handleBlank(res,effect);
        try {
            this.deposit.getStorage().addResourceSwap(res);
        } catch (FaithNotAllowedException e) {
            e.printStackTrace();
        }
    }

    /**
     * A method that switches the blank resource
     * @param res Resources to be switched
     * @param effect The switching blank effect
     * @return The resources
     */
    private Resources handleBlank(Resources res, ExtraResource effect){
        return res.switchBlank(effect.getResource());
    }

    /**
     * Draw a card from the card dealer and inserts it in to the dashboard
     * @param row Row of the card dealer
     * @param column Column of the card dealer
     * @param place Where to place the card in the personal dashboard
     */
    public void drawCard(int row, int column, int place)throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("NOT IMPLEMENTED");
        //this.cardBoard.insertCard(this.generalBoard.getCardDealer().drawCard(row,column),place);
    }
}
