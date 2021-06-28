package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.exceptions.*;
import it.polimi.ingsw.model.resources.ResourceTypes;
import it.polimi.ingsw.model.resources.Resources;
import it.polimi.ingsw.network.ClientHandler;
import it.polimi.ingsw.network.NetworkMessages;

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
        boolean leaderAction = true;
        boolean error = true;
        clientHandler.sendObject(TURNBEGIN);

        clientHandler.sendGame(playerNum);

        NetworkMessages action = clientHandler.receiveObject(NetworkMessages.class);

        while((action == DISCARDLEADER || action == ACTIVATELEADER)){
            if(action == ACTIVATELEADER)
                error&=activateLeader();
            else
                error&=discardLeader();
            if(!error)
                leaderAction=false;
            action = clientHandler.receiveObject(NetworkMessages.class);
        }

        error=true;
        while(error){
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
                default:
                    clientHandler.sendObject(ERROR);
                    clientHandler.sendObject("You must complete at lest one action");
            }
            action = clientHandler.receiveObject(NetworkMessages.class);
        }

        while((action == DISCARDLEADER || action == ACTIVATELEADER) && leaderAction){
            if(action == ACTIVATELEADER)
                activateLeader();
            else
                discardLeader();
            action = clientHandler.receiveObject(NetworkMessages.class);
        }

        while (action!=TURNEND){
            clientHandler.sendObject(ERROR);
            clientHandler.sendObject("You can only end the turn");
            action = clientHandler.receiveObject(NetworkMessages.class);
        }
        clientHandler.sendObject(SUCCESS);

        clientHandler.sendObject(ENDTURN);
    }


    private void handleSwap() throws IOException, FaithOverflowException {
        while(player.getPersonalBoard().getDeposit().getWarehouseDepots().getSwapDeposit().getTotalResourceNumber()!=0){
            switch (clientHandler.receiveMessage()){
                case MOVETOLEVEL:
                    try {
                        player.getPersonalBoard().getDeposit().getWarehouseDepots().moveToLevel(clientHandler.receiveObject(Integer.class),
                                clientHandler.receiveObject(ResourceTypes.class),
                                clientHandler.receiveObject(Integer.class)
                        );
                    } catch (TypeNotChangeableException | LevelTooSmallException | NegativeResourceValueException | IndexOutOfBoundsException | ResourceTypeAlreadyPresentException e) {
                        clientHandler.sendObject(ERROR);
                        clientHandler.sendObject(e.getMessage());
                    }
                    break;
                case MOVETOSWAP:
                    try {
                        player.getPersonalBoard().getDeposit().getWarehouseDepots().moveToSwap(clientHandler.receiveObject(Integer.class));
                    } catch (IndexOutOfBoundsException e){
                        clientHandler.sendObject(ERROR);
                        clientHandler.sendObject(e.getMessage());
                    }
                    break;
                case DROPRESOURCES:
                    player.getPersonalBoard().dropResources();
                    break;
            }
            clientHandler.sendObject(SUCCESS);
            clientHandler.sendGame(playerNum);
        }
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
        }catch (IndexOutOfBoundsException e){
            clientHandler.sendObject(ERROR);
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
            clientHandler.sendObject(SUCCESS);
            clientHandler.sendGame(playerNum);
            return false;
        } catch (UnusableCardException e) {
            clientHandler.sendObject(ERROR);
            clientHandler.sendObject("This card is not playable");
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
                    } catch (NegativeResourceValueException | FaithOverflowException | NullPointerException e) {
                        clientHandler.sendObject(ERROR);
                        clientHandler.sendObject(e.getMessage());
                    }
                    break;
                case PROD2:
                    try {
                        player.getPersonalBoard().enqueueProduce(clientHandler.receiveObject(Integer.class));
                        clientHandler.sendObject(SUCCESS);
                        clientHandler.sendGame(playerNum);
                        error = false;
                    } catch (UnusableCardException | FaithOverflowException | NegativeResourceValueException | IndexOutOfBoundsException | NullPointerException e) {
                        clientHandler.sendObject(ERROR);
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
                    } catch (FaithOverflowException | NegativeResourceValueException | UnusableCardException | NullPointerException e) {
                        clientHandler.sendObject(ERROR);
                        clientHandler.sendObject(e.getMessage());
                    }
                    break;
                case ENDPRODUCTION:
                    clientHandler.sendObject(SUCCESS);
                    return error;
                case PRODUCTION:
                    continue;
                default:
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
        } catch (IndexOutOfBoundsException | YouMustPlayLeaderException e) {
            clientHandler.sendObject(ERROR);
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
        } catch (IndexOutOfBoundsException e) {
            clientHandler.sendObject(ERROR);
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
        } catch (IncompatibleCardLevelException | NegativeResourceValueException | IndexOutOfBoundsException | EmptyStackException e) {
            clientHandler.sendObject(ERROR);
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
                    } catch (FaithNotAllowedException e) {
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
                    } catch (FaithNotAllowedException e) {
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
                    } catch (FaithNotAllowedException e) {
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
        }catch (IndexOutOfBoundsException e){
            clientHandler.sendObject(ERROR);
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

    @Override
    public void endTurn() throws IOException {
        clientHandler.sendObject(TURNEND);
    }
}
