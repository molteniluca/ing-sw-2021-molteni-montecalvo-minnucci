package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.exceptions.*;
import it.polimi.ingsw.model.resources.ResourceTypes;
import it.polimi.ingsw.model.resources.Resources;
import it.polimi.ingsw.network.ClientHandler;
import it.polimi.ingsw.network.NetworkMessages;
import it.polimi.ingsw.network.exceptions.WrongObjectException;

import java.io.IOException;
import java.io.Serializable;
import java.util.EmptyStackException;

import static it.polimi.ingsw.network.NetworkMessages.*;


/**
 * This class represents a turn, for each player there's a turn
 */
public class PlayerTurn implements Turn, Serializable {
    private static final long serialVersionUID = 6732146736278436272L;
    private final Player player;
    private final transient ClientHandler clientHandler;
    private final transient int playerNum;
    private boolean leaderAction = true;
    private boolean alreadyDone = false;
    private boolean isProducing = false;
    private boolean isHandlingSwap = false;
    private boolean waitingForAction = false;

    public PlayerTurn(Player player, ClientHandler clientHandler, int playerNum){
        this.player = player;
        this.clientHandler = clientHandler;
        this.playerNum = playerNum;
    }

    public Player getPlayer() {
        return player;
    }

    /**
     * This method begins a turn for this player
     * @throws IOException In case the client disconnects
     */
    @Override
    public void beginTurn() throws IOException, FaithOverflowException, WinException, NotEnoughCardException {
        boolean error = true;
        NetworkMessages action;
        player.getPersonalBoard().setUpAvailableProductions();
        clientHandler.sendObject(TURNBEGIN);

        leaderAction = true;

        while(error){
            alreadyDone = false;
            isProducing = false;
            isHandlingSwap = false;
            waitingForAction = true;

            clientHandler.sendGame(playerNum);

            action = clientHandler.receiveMessage();

            while((action == DISCARDLEADER || action == ACTIVATELEADER)){
                if(action == ACTIVATELEADER)
                    error&=!activateLeader();
                else
                    error&=!discardLeader();
                if(!error)
                    leaderAction=false;
                action = clientHandler.receiveMessage();
            }

            switch(action){
                case PRODUCTION:
                    error = activateProduction();
                    break;
                case BUYCOLUMN:
                    error = buyColumn();
                    handleSwap();
                    break;
                case BUYROW:
                    error = buyRow();
                    handleSwap();
                    break;
                case BUYCARD:
                    error = buyDevelopmentCard();
                    break;
                case TURNEND:
                    clientHandler.sendObject(ERROR);
                    clientHandler.sendObject("You must complete at lest one action");
            }
            if(!error){
                alreadyDone=true;
                waitingForAction = false;
                clientHandler.sendGame(playerNum);
                break;
            }
        }
        action = clientHandler.receiveMessage();

        while((action == DISCARDLEADER || action == ACTIVATELEADER) && leaderAction){
            if(action == ACTIVATELEADER)
                activateLeader();
            else
                discardLeader();
            action = clientHandler.receiveMessage();
        }

        while (action!=TURNEND){
            clientHandler.sendObject(ERROR);
            clientHandler.sendObject("You can only end the turn");
            action = clientHandler.receiveMessage();
        }
        clientHandler.sendObject(SUCCESS);
    }

    /**
     * Method that handles the swap area
     * @throws IOException In case the client disconnects
     * @throws FaithOverflowException In case a discard swap triggers a win of another player
     */
    private void handleSwap() throws IOException, FaithOverflowException {
        while(player.getPersonalBoard().getDeposit().getWarehouseDepots().getSwapDeposit().getTotalResourceNumber()!=0){
            isHandlingSwap = true;
            clientHandler.sendGame(playerNum);
            switch (clientHandler.receiveMessage()){
                case MOVETOLEVEL:
                    try {
                        player.getPersonalBoard().getDeposit().getWarehouseDepots().moveToLevel(clientHandler.receiveObject(Integer.class),
                                clientHandler.receiveObject(ResourceTypes.class),
                                clientHandler.receiveObject(Integer.class)
                        );
                        clientHandler.sendObject(SUCCESS);
                        clientHandler.sendGame(playerNum);
                    } catch (TypeNotChangeableException | LevelTooSmallException | NegativeResourceValueException | IndexOutOfBoundsException | ResourceTypeAlreadyPresentException | WrongObjectException e) {
                        clientHandler.sendObject(ERROR);
                        if(e.getMessage()==null)
                            clientHandler.sendObject("Index not valid!");
                        else
                            clientHandler.sendObject(e.getMessage());
                    }
                    break;
                case MOVETOSWAP:
                    try {
                        player.getPersonalBoard().getDeposit().getWarehouseDepots().moveToSwap(clientHandler.receiveObject(Integer.class));
                        clientHandler.sendObject(SUCCESS);
                        clientHandler.sendGame(playerNum);
                    } catch (IndexOutOfBoundsException | WrongObjectException e){
                        clientHandler.sendObject(ERROR);
                        if(e.getMessage()==null)
                            clientHandler.sendObject("Index not valid!");
                        else
                            clientHandler.sendObject(e.getMessage());
                    }
                    break;
                case DROPRESOURCES:
                    player.getPersonalBoard().dropResources();
                    clientHandler.sendObject(SUCCESS);
                    clientHandler.sendGame(playerNum);
                    break;
            }
        }
        isHandlingSwap = false;
        clientHandler.sendGame(playerNum);
    }

    /**
     * This method discards a leader
     * @return true if error and false if not
     * @throws IOException in case of connection problems
     */
    public boolean discardLeader() throws IOException, FaithOverflowException {
        try {
            player.getPersonalBoard().getLeaderBoard().discardLeader(clientHandler.receiveObject(Integer.class));
            clientHandler.sendObject(SUCCESS);
            clientHandler.sendGame(playerNum);
            return true;
        }catch (IndexOutOfBoundsException | WrongObjectException e){
            clientHandler.sendObject(ERROR);
            if(e.getMessage()==null)
                clientHandler.sendObject("Index not valid!");
            else
                clientHandler.sendObject(e.getMessage());
            return false;
        }
    }

    /**
     * This method activates a leader card
     * Expects leaderIndex->int
     * @return true if error and false if not
     * @throws IOException in case of connection problems
     */
    private boolean activateLeader() throws IOException {
        try {
            player.getPersonalBoard().playLeader(clientHandler.receiveObject(Integer.class));
            player.getPersonalBoard().setUpAvailableProductions();
            clientHandler.sendObject(SUCCESS);
            clientHandler.sendGame(playerNum);
            return false;
        } catch (UnusableCardException e) {
            clientHandler.sendObject(ERROR);
            clientHandler.sendObject("This card is not playable");
            return true;
        } catch (IndexOutOfBoundsException e) {
            clientHandler.sendObject(ERROR);
            clientHandler.sendObject("Card not valid");
            return true;
        } catch (WrongObjectException e) {
            clientHandler.sendObject(ERROR);
            clientHandler.sendObject(e.getMessage());
            return true;
        }
    }

    /**
     * This method activates a production
     * Expects productionType->networkMessage
     * @return true if error and false if not
     * @throws IOException in case of connection problems
     */
    private boolean activateProduction() throws IOException {
        player.getPersonalBoard().initProduce();
        boolean error = true;
        while(true) {
            isProducing=true;
            NetworkMessages message = clientHandler.receiveMessage();
            switch (message) {
                case PROD1:
                    try {
                        player.getPersonalBoard().enqueueProduce(clientHandler.receiveObject(ResourceTypes.class),
                                clientHandler.receiveObject(ResourceTypes.class),
                                clientHandler.receiveObject(ResourceTypes.class)
                        );
                        clientHandler.sendObject(SUCCESS);
                        clientHandler.sendGame(playerNum);
                        error = false;
                    } catch (NegativeResourceValueException | FaithOverflowException | NullPointerException | WrongObjectException e) {
                        clientHandler.sendObject(ERROR);
                        if(e.getMessage()==null)
                            clientHandler.sendObject("Index not valid!");
                        else
                            clientHandler.sendObject(e.getMessage());
                    }
                    break;
                case PROD2:
                    try {
                        player.getPersonalBoard().enqueueProduce(clientHandler.receiveObject(Integer.class));
                        clientHandler.sendObject(SUCCESS);
                        clientHandler.sendGame(playerNum);
                        error = false;
                    } catch (UnusableCardException | FaithOverflowException | NegativeResourceValueException | IndexOutOfBoundsException | NullPointerException | WrongObjectException e) {
                        clientHandler.sendObject(ERROR);
                        if(e.getMessage()==null)
                            clientHandler.sendObject("Index not valid!");
                        else
                            clientHandler.sendObject(e.getMessage());
                    }
                    break;
                case PROD3:
                    try {
                        player.getPersonalBoard().enqueueProduce(clientHandler.receiveObject(ResourceTypes.class),
                                clientHandler.receiveObject(ResourceTypes.class)
                        );
                        clientHandler.sendObject(SUCCESS);
                        clientHandler.sendGame(playerNum);
                        error = false;
                    } catch (FaithOverflowException | NegativeResourceValueException | UnusableCardException | NullPointerException | WrongObjectException e) {
                        clientHandler.sendObject(ERROR);
                        if(e.getMessage()==null)
                            clientHandler.sendObject("Index not valid!");
                        else
                            clientHandler.sendObject(e.getMessage());
                    }
                    break;
                case ENDPRODUCTION:
                    isProducing=false;
                    player.getPersonalBoard().endProduce();
                    clientHandler.sendObject(SUCCESS);
                    return error;
                case PRODUCTION:
                    continue;
                default:
                    isProducing=false;
                    clientHandler.sendObject(ERROR);
                    clientHandler.sendObject("Expecting a production action");
                    return true;
            }
        }
    }

    /**
     * This method buys a column
     * Expects columnIndex->int
     * @return true if error and false if not
     * @throws IOException in case of connection problems
     */
    private boolean buyColumn() throws IOException, FaithOverflowException {
        try {
            player.getPersonalBoard().buyColumn(clientHandler.receiveObject(Integer.class), clientHandler.receiveObject(Integer.class));
            clientHandler.sendObject(SUCCESS);
            clientHandler.sendGame(playerNum);
            return false;
        } catch (IndexOutOfBoundsException | YouMustPlayLeaderException | WrongObjectException e) {
            clientHandler.sendObject(ERROR);
            if(e.getMessage()==null)
                clientHandler.sendObject("Index not valid!");
            else
                clientHandler.sendObject(e.getMessage());
            return true;
        }
    }

    /**
     * This method buys a row
     * Expects rowIndex->int
     * @return true if error and false if not
     * @throws IOException in case of connection problems
     */
    private boolean buyRow() throws IOException, FaithOverflowException {
        try {
            player.getPersonalBoard().buyRow(clientHandler.receiveObject(Integer.class), clientHandler.receiveObject(Integer.class));
            clientHandler.sendObject(SUCCESS);
            clientHandler.sendGame(playerNum);
            return false;
        } catch (IndexOutOfBoundsException | WrongObjectException e) {
            clientHandler.sendObject(ERROR);
            if(e.getMessage()==null)
                clientHandler.sendObject("Index not valid!");
            else
                clientHandler.sendObject(e.getMessage());
            return true;
        }
    }

    /**
     * This method buys a development card
     * Expects cardRow->int, cardColumn->int, placeToInsert->int
     * @return true if error and false if not
     * @throws IOException in case of connection problems
     */
    private boolean buyDevelopmentCard() throws IOException, WinException, NotEnoughCardException {
        try {
            player.getPersonalBoard().drawCard(clientHandler.receiveObject(Integer.class),clientHandler.receiveObject(Integer.class),clientHandler.receiveObject(Integer.class));
            clientHandler.sendObject(SUCCESS);
            clientHandler.sendGame(playerNum);
            return false;
        } catch (IncompatibleCardLevelException | NegativeResourceValueException | IndexOutOfBoundsException | EmptyStackException | WrongObjectException e) {
            clientHandler.sendObject(ERROR);
            if(e.getMessage()==null)
                clientHandler.sendObject("Index not valid!");
            else
                clientHandler.sendObject(e.getMessage());
            return true;
        }
    }

    /**
     * Game setup for this player
     * @throws IOException In case the communication with the client goes wrong
     */
    public void startGame() throws IOException {
        clientHandler.sendGame(playerNum);
        clientHandler.sendObject(playerNum);
        switch (playerNum){
            case 1:
                while(true) {
                    try {
                        ResourceTypes res1 = clientHandler.receiveObject(ResourceTypes.class);
                        player.getPersonalBoard().getDeposit().getWarehouseDepots().addResourceSwap(new Resources().set(res1, 1));
                        player.getPersonalBoard().getDeposit().getWarehouseDepots().moveToLevel(1,res1,1);
                        clientHandler.sendObject(SUCCESS);
                        clientHandler.sendGame(playerNum);
                        break;
                    } catch (FaithNotAllowedException | WrongObjectException e) {
                        clientHandler.sendObject(ERROR);
                        clientHandler.sendObject(e.getMessage());
                    } catch (LevelTooSmallException | NegativeResourceValueException | TypeNotChangeableException e) {
                        e.printStackTrace();
                    } catch (ResourceTypeAlreadyPresentException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case 2:
                while(true) {
                    try {
                        ResourceTypes res1 = clientHandler.receiveObject(ResourceTypes.class);
                        player.getPersonalBoard().getDeposit().getWarehouseDepots().addResourceSwap(new Resources().set(res1, 1));
                        player.getPersonalBoard().getDeposit().getWarehouseDepots().moveToLevel(1,res1,1);
                        player.getPersonalBoard().getFaithTrack().incrementPosition(1);
                        clientHandler.sendObject(SUCCESS);
                        clientHandler.sendGame(playerNum);
                        break;
                    } catch (FaithNotAllowedException | WrongObjectException e) {
                        clientHandler.sendObject(ERROR);
                        clientHandler.sendObject(e.getMessage());
                    } catch (FaithOverflowException | LevelTooSmallException | NegativeResourceValueException | TypeNotChangeableException e) {
                        e.printStackTrace();
                    } catch (ResourceTypeAlreadyPresentException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case 3:
                while(true) {
                    try {
                        ResourceTypes res1 = clientHandler.receiveObject(ResourceTypes.class);
                        ResourceTypes res2 = clientHandler.receiveObject(ResourceTypes.class);
                        player.getPersonalBoard().getDeposit().getWarehouseDepots().addResourceSwap(new Resources().set(res1, 1).add(new Resources().set(res2, 1)));
                        if(res1==res2){
                            player.getPersonalBoard().getDeposit().getWarehouseDepots().moveToLevel(1, res2, 2);
                        }else {
                            player.getPersonalBoard().getDeposit().getWarehouseDepots().moveToLevel(0, res1, 1);
                            player.getPersonalBoard().getDeposit().getWarehouseDepots().moveToLevel(1, res2, 1);
                        }
                        player.getPersonalBoard().getFaithTrack().incrementPosition(1);
                        clientHandler.sendObject(SUCCESS);
                        clientHandler.sendGame(playerNum);
                        break;
                    } catch (FaithNotAllowedException | WrongObjectException e) {
                        clientHandler.sendObject(ERROR);
                        clientHandler.sendObject(e.getMessage());
                    } catch (FaithOverflowException | LevelTooSmallException | NegativeResourceValueException | TypeNotChangeableException e) {
                        e.printStackTrace();
                    } catch (ResourceTypeAlreadyPresentException e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }

        try {
            player.getPersonalBoard().getLeaderBoard().selectLeaders(clientHandler.receiveObject(Integer[].class));
            clientHandler.sendObject(SUCCESS);
            clientHandler.sendGame(playerNum);
        }catch (IndexOutOfBoundsException | WrongObjectException e){
            clientHandler.sendObject(ERROR);
            if(e.getMessage()==null)
                clientHandler.sendObject("Index not valid!");
            else
                clientHandler.sendObject(e.getMessage());
        }

        clientHandler.sendObject(GAMESTARTED);
    }

    /**
     * Gets all the victory points of a player
     * @return The victory points
     */
    public int getVictoryPoints(){
        return player.getPersonalBoard().getVictoryPoints();
    }

    /**
     * Ends the game for this player
     * @param winner True if this player is winner and false if not
     * @throws IOException In case the communication with the client goes wrong
     */
    public void endGame(boolean winner) throws IOException {
        clientHandler.sendGame(playerNum);
        if(winner){
            clientHandler.sendObject(YOUWON);
        }else{
            clientHandler.sendObject(YOULOST);
        }
    }

    public boolean isAlreadyDone() {
        return alreadyDone;
    }

    public boolean isLeaderAction() {
        return leaderAction;
    }

    public boolean isHandlingSwap() {
        return isHandlingSwap;
    }

    public boolean isProducing() {
        return isProducing;
    }

    public boolean isWaitingForAction() {
        return waitingForAction;
    }

    @Override
    public void endTurn() throws IOException {
        clientHandler.sendObject(ENDTURN);
    }
}
