package it.polimi.ingsw.model.board.personal;

import it.polimi.ingsw.model.board.general.GeneralBoard;
import it.polimi.ingsw.model.cards.DevelopmentCard;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.model.cards.specialAbility.Discount;
import it.polimi.ingsw.model.cards.specialAbility.ExtraProduction;
import it.polimi.ingsw.model.cards.specialAbility.ExtraResource;
import it.polimi.ingsw.model.exceptions.*;
import it.polimi.ingsw.model.resources.ResourceTypes;
import it.polimi.ingsw.model.resources.Resources;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EmptyStackException;

/**
 * Class that represents the personal board
 */
public class PersonalBoard implements Serializable {
    private static final long serialVersionUID = 6732146736278436290L;
    private final FaithTrack faithTrack;
    private final Deposit deposit;
    private final CardBoard cardBoard;
    private final LeaderBoard leaderBoard;
    private final GeneralBoard generalBoard;

    /**
     * Constructor of the class
     * @param generalBoard The general board associated with this player
     * @param cardsInHand The leader cards at the beginning of the game
     */
    public PersonalBoard(GeneralBoard generalBoard, ArrayList<LeaderCard> cardsInHand) {
        this.faithTrack = new FaithTrack(generalBoard.getFaithObserver());
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
     * This method gets all the victory points of a player
     * @return The victory points
     */
    public int getVictoryPoints(){
        int points=0;
        points+=faithTrack.getVictoryPoint();
        points+=deposit.getVictoryPoint();
        points+=cardBoard.getAllVictoryPoint();
        points+=leaderBoard.getVictoryPoint();
        return points;
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
                Resources cost=card.getProductionCost();

                try {
                    deposit.removeResources(cost);
                } catch (NegativeResourceValueException e) {
                    e.printStackTrace();
                }

                this.faithTrack.incrementPosition(card.getProductionPower().getResourceNumber(ResourceTypes.FAITH));

                try {
                    deposit.getStrongBox().addResource(card.getProductionPower().eraseFaith());
                } catch (FaithNotAllowedException e) {
                    e.printStackTrace();
                }
            }
            else{
                throw new UnusableCardException("Can't use the card, there are not enough resources");
            }
        }
        else{
            throw new UnusableCardException("Can't use cards not in the visible area of the board");
        }
    }

    /**
     * Checks if a card can be utilized for production
     * @param card Card to check
     * @return True if you can produce and false if not
     */
    public boolean checkProduce(DevelopmentCard card){
        return deposit.checkRemoveResource(card.getProductionCost());
    }

    /**
     * This method produces resources using a development card
     * @param cardIndex The development card
     * @throws UnusableCardException In case the ard is not contained in the visible part of the board
     * @throws FaithOverflowException In case a card returns too much faith
     */
    public void produce(int cardIndex) throws UnusableCardException, FaithOverflowException {
        DevelopmentCard card = cardBoard.getUpperDevelopmentCards()[cardIndex];
        produce(card);
    }

    /**
     * Handles the discount given from a leader card
     * @param cost The cost of the production
     * @return The new cost
     */
    public Resources handleDiscount(Resources cost){
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
     * This method checks whether you can produce using this production
     * @param resource1 The first resource in input
     * @param resource2 The second resource in input
     * @return True if possible and false if not
     */
    public boolean checkProduce(ResourceTypes resource1, ResourceTypes resource2){
        return deposit.checkRemoveResource(new Resources().set(resource1,1).add(new Resources().set(resource2,1)));
    }

    /**
     * Produces resources using the default production
     * @param resource1 The first resource in input
     * @param resource2 The second resource in input
     * @param output The resource in output
     */
    public void produce(ResourceTypes resource1, ResourceTypes resource2, ResourceTypes output) throws NegativeResourceValueException, FaithOverflowException {
        if(checkProduce(resource1,resource2)){
            try {
                deposit.removeResources(new Resources().set(resource1,1).add(new Resources().set(resource2,1)));
            } catch (NegativeResourceValueException e) {
                e.printStackTrace();
            }
            if(output==ResourceTypes.FAITH){
                this.faithTrack.incrementPosition(1);
            }else{
                try {
                    deposit.getStrongBox().addResource(new Resources().set(output,1));
                } catch (FaithNotAllowedException e) {
                    e.printStackTrace();
                }
            }
        }
        else{
            throw new NegativeResourceValueException("There are not enough resources to make the production");
        }
    }

    /**
     * Checks if the leader effect is active and if you have enough resources to make this production
     * @param resource1 The input resource
     * @return True if possible and false if not
     */
    public boolean checkProduce(ResourceTypes resource1){
        return this.getLeaderBoard().getProductionEffects().stream().anyMatch(extraProduction -> extraProduction.getProductionCost()==resource1) &&
                deposit.checkRemoveResource(new Resources().set(resource1,1));
    }

    /**
     * Checks if the leader effect is active and produces faith and the specified resource in output
     * @param resource1 The input resource
     * @param output The output resource
     */
    public void produce(ResourceTypes resource1, ResourceTypes output) throws FaithOverflowException, NegativeResourceValueException, UnusableCardException {
        if(this.getLeaderBoard().getProductionEffects().stream().anyMatch(extraProduction -> extraProduction.getProductionCost()==resource1)){
            if(deposit.checkRemoveResource(new Resources().set(resource1,1))){
                this.deposit.removeResources(new Resources().set(resource1,1));
                faithTrack.incrementPosition(1);

                if(output==ResourceTypes.FAITH)
                    faithTrack.incrementPosition(1);
                else {
                    try {
                        deposit.getStrongBox().addResource(new Resources().set(output,1));
                    } catch (FaithNotAllowedException e) {
                        e.printStackTrace();
                    }
                }
            }else {
                throw new NegativeResourceValueException("Not enough resources to complete the production!");
            }
        }
        else {
            throw new UnusableCardException("There aren't leaders with this effect active");
        }
    }

    /**
     * This method buys a column from the market and adds the resources to the swap area
     * @param column The column to be bought
     * @param effect The effect to be used, null for none
     */
    public void buyColumn(int column, ExtraResource effect) throws FaithOverflowException {
        Resources res=this.generalBoard.getMarket().buyColumn(column);
        handleFaithAndStore(effect, res);
    }

    /**
     * This method buys a column from the market and adds the resources to the swap area
     * @param column The column to be bought
     * @param effectIndex The effect index (-1 for none)
     */
    public void buyColumn(int column, int effectIndex) throws FaithOverflowException, IndexOutOfBoundsException, YouMustPlayLeaderException {
        if(effectIndex==-1){
            if(leaderBoard.getExtraResource().isEmpty())
                buyColumn(column,null);
            else
                throw new YouMustPlayLeaderException("You must apply the leader effect if you have one");
        }else{
            buyColumn(column,leaderBoard.getExtraResource().get(effectIndex));
        }
    }

    /**
     * This method buys a row from the market and adds the resources to the swap area
     * @param row The row to be bought
     */
    public void buyRow(int row, ExtraResource effect) throws FaithOverflowException {
        Resources res=this.generalBoard.getMarket().buyRow(row);
        handleFaithAndStore(effect, res);
    }

    /**
     * This method buys a column from the market and adds the resources to the swap area
     * @param row The column to be bought
     * @param effectIndex The effect index (-1 for none)
     */
    public void buyRow(int row, int effectIndex) throws FaithOverflowException, IndexOutOfBoundsException {
        if(effectIndex==-1){
            buyRow(row,null);
        }else{
            buyRow(row,leaderBoard.getExtraResource().get(effectIndex));
        }
    }

    private void handleFaithAndStore(ExtraResource effect, Resources res) throws FaithOverflowException {
        if(effect==null){
            effect=new ExtraResource(ResourceTypes.BLANK);
        }
        res=handleBlank(res,effect);
        this.faithTrack.incrementPosition(res.getResourceNumber(ResourceTypes.FAITH));
        res=res.eraseFaith();
        try {
            this.deposit.getWarehouseDepots().addResourceSwap(res);
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
    public void drawCard(int row, int column, int place, boolean singlePlayer) throws IncompatibleCardLevelException, NegativeResourceValueException, WinException, EmptyStackException, CardsOfSameColorFinishedException {
        if(checkDrawCard(row,column)){
            this.cardBoard.insertCard(this.generalBoard.getCardDealer().drawCard(row, column,singlePlayer),place);
            this.deposit.removeResources(handleDiscount(generalBoard.getCardDealer().getCost(row,column)));
        }else{
            throw new NegativeResourceValueException("Can't draw the development card, not enough resources");
        }
    }

    /**
     * This method checks whether a card is drawable with the current resource values
     * @param row Row of the card dealer
     * @param column Column of the card dealer
     */
    public boolean checkDrawCard(int row, int column){
        return deposit.checkRemoveResource(handleDiscount(generalBoard.getCardDealer().getCost(row,column)));
    }

    /**
     * This method checks whether a leader card is playable with the current resource values
     * @param leaderCard The leader card to be played
     * @return True if it's playable and false if it's not
     */
    public boolean checkPlayLeader(LeaderCard leaderCard){
        return this.leaderBoard.checkPlayLeader(leaderCard,this.deposit.getTotalResources(),this.cardBoard.getDevelopmentCards());
    }

    /**
     * This method plays a leader card
     * @param leaderCard The leader card to be played
     * @throws UnusableCardException In case the card is not playable
     */
    public void playLeader(int leaderCard) throws UnusableCardException {
        playLeader(leaderBoard.getLeaderCardsInHand().get(leaderCard));
    }

    /**
     * This method plays a leader card
     * @param leaderCard The leader card to be played
     * @throws UnusableCardException In case the card is not playable
     */
    public void playLeader(LeaderCard leaderCard) throws UnusableCardException {
        if(checkPlayLeader(leaderCard)){
            leaderBoard.playLeader(leaderCard,this.deposit.getTotalResources(),this.cardBoard.getDevelopmentCards());
        }else
            throw new UnusableCardException("There aren't enough resources to play this card");
    }

    /**
     * This method drops the resources contained int the swap area and notifies the others faith tracks
     */
    public void dropResources() throws FaithOverflowException {
        this.faithTrack.dropResources(this.deposit.getWarehouseDepots().removeFromSwap());
    }





    /* This section handles the same turn production */

    private Resources availableResources;
    private ArrayList<ExtraProduction> availableProductions;
    private boolean prod1;
    private boolean[] prod2;

    public ArrayList<ExtraProduction> getAvailableProductions() {
        return availableProductions;
    }

    public boolean isProd1() {
        return prod1;
    }

    public boolean[] getProd2() {
        return prod2;
    }

    public Resources getAvailableResources()
    {
        return availableResources;
    }

    /**
     * This method initializes the production
     */
    public void initProduce() {
        setUpAvailableProductions();
        availableResources = deposit.getTotalResources();
    }

    /**
     * Method that sets up the booleans representing the available productions
     */
    public void setUpAvailableProductions(){
        availableProductions = this.getLeaderBoard().getProductionEffects();
        prod1 = true;
        prod2 = new boolean[3];
        for(int i=0; i<3; i++) {
            prod2[i] = cardBoard.getUpperDevelopmentCards()[i]!=null;
        }
    }

    /**
     * This method ends the production
     */
    public void endProduce(){
        availableProductions = null;
        availableResources = null;
        prod1 = true;
        prod2 = null;
        setUpAvailableProductions();
    }

    /**
     * Checks if the leader effect is active and produces faith and the specified resource in output
     * @param resource1 The input resource
     * @param output The output resource
     */
    public void enqueueProduce(ResourceTypes resource1, ResourceTypes output) throws UnusableCardException, NegativeResourceValueException, FaithOverflowException {
        if(checkProduce(resource1)){
            ExtraProduction match = availableProductions.stream().filter(extraProduction -> extraProduction.getProductionCost()==resource1).findFirst().orElse(null);
            if(match!=null){
                availableProductions.remove(match);
                try {
                    availableResources = availableResources.sub(new Resources().set(resource1,1));
                    produce(resource1,output);
                } catch (NegativeResourceValueException e) {
                    throw new NegativeResourceValueException("Can't produce, not enough resources");
                }
            }else
                throw new UnusableCardException("Can't produce using this card");
        }else
            throw new UnusableCardException("Can't produce using this card");
    }

    /**
     * Produces resources using the default production
     * @param resource1 The first resource in input
     * @param resource2 The second resource in input
     * @param output The resource in output
     */
    public void enqueueProduce(ResourceTypes resource1, ResourceTypes resource2, ResourceTypes output) throws NegativeResourceValueException, FaithOverflowException {
        if(availableResources.isSubPositive(new Resources().set(resource1,1).add(new Resources().set(resource2,1)))&& prod1){
            try {
                availableResources = availableResources.sub(new Resources().set(resource1,1).add(new Resources().set(resource2,1)));
            } catch (NegativeResourceValueException e) {
                e.printStackTrace();
            }
            produce(resource1,resource2,output);
            prod1=false;
        }
        else{
            throw new NegativeResourceValueException("There are not enough resources to make the production");
        }
    }

    /**
     * This method produces resources using a development card
     * @param cardIndex The development card
     * @throws UnusableCardException In case the ard is not contained in the visible part of the board
     * @throws FaithOverflowException In case a card returns too much faith
     */
    public void enqueueProduce(int cardIndex) throws UnusableCardException, FaithOverflowException, NegativeResourceValueException {
        if(prod2[cardIndex]) {
            DevelopmentCard card = cardBoard.getUpperDevelopmentCards()[cardIndex];
            if (checkProduce(card)) {
                Resources cost = card.getProductionCost();
                if (availableResources.isSubPositive(cost)) {
                    try {
                        availableResources = availableResources.sub(cost);
                    } catch (NegativeResourceValueException e) {
                        e.printStackTrace();
                    }
                    produce(card);
                    prod2[cardIndex]=false;
                }
                else
                    throw new NegativeResourceValueException("Can't produce, not enough resources");
            }
            else
                throw new NegativeResourceValueException("Can't produce, not enough resources");
        }
        else
            throw new UnusableCardException("Can't produce, card already used");
    }
}
