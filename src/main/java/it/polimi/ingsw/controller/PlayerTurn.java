package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.exceptions.*;
import it.polimi.ingsw.model.resources.ResourceTypes;

import java.io.IOException;
import java.io.Serializable;
import java.util.EmptyStackException;

import static it.polimi.ingsw.controller.NetworkMessages.*;


/**
 * This class represents a turn, for each player there's a turn
 */
public class PlayerTurn implements Turn, Serializable {
    private final Player player;
    private final transient ClientHandler clientHandler;
    private final transient WaitingRoom waitingRoom;

    public PlayerTurn(Player player, ClientHandler clientHandler, WaitingRoom waitingRoom){
        this.waitingRoom = waitingRoom;
        this.player = player;
        this.clientHandler = clientHandler;
    }

    public Player getPlayer() {
        return player;
    }

    /**
     * This method begins a turn for this player
     * @throws IOException In case the client disconnects
     */
    @Override
    public void beginTurn() throws IOException, FaithOverflowException, WinException, EmptyStackException {
        boolean leaderAction = true;
        boolean error = true;
        clientHandler.sendObject(TURNBEGIN);

        clientHandler.sendGame();

        NetworkMessages action = clientHandler.receiveObject(NetworkMessages.class);

        while((action == DISCARDLEADER || action == ACTIVATELEADER)  && error){
            if(action == ACTIVATELEADER)
                error = activateLeader();
            else
                error = discardLeader();
            if(!error)
                leaderAction=false;
            action = clientHandler.receiveObject(NetworkMessages.class);
        }

        error=true;
        while(error){
            switch(action){
                case PROD1:
                case PROD2:
                case PROD3:
                    error = activateProduction(action);
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
            }
            action = clientHandler.receiveObject(NetworkMessages.class);
        }

        error=true;
        while((action== ACTIVATELEADER || action==DISCARDLEADER) && error && leaderAction){
            if(action == ACTIVATELEADER)
                error = activateLeader();
            else
                error = discardLeader();
            if(!error)
                leaderAction=false;
            action = clientHandler.receiveObject(NetworkMessages.class);
        }

        while (action!=TURNEND){
            clientHandler.sendObject(ERROR);
            clientHandler.sendObject("You can only end the turn");
            action = clientHandler.receiveObject(NetworkMessages.class);
        }

        clientHandler.sendObject(TURNEND);
    }


    private void handleSwap() throws IOException, FaithOverflowException {
        while(player.getPersonalBoard().getDeposit().getStorage().getSwapDeposit().getTotalResourceNumber()!=0){
            switch (clientHandler.receiveMessage()){
                case MOVETOSWAP:
                    try {
                        player.getPersonalBoard().getDeposit().getStorage().moveToLevel(clientHandler.receiveObject(int.class),
                                clientHandler.receiveObject(ResourceTypes.class),
                                clientHandler.receiveObject(int.class)
                        );
                    } catch (TypeNotChangeableException | LevelTooSmallException | NegativeResourceValueException e) {
                        clientHandler.sendObject(ERROR);
                        clientHandler.sendObject(e.toString());
                    }
                    break;
                case MOVETOLEVEL:
                    player.getPersonalBoard().getDeposit().getStorage().moveToSwap(clientHandler.receiveObject(int.class));
                    break;
                case DROPRESOURCES:
                    player.getPersonalBoard().dropResources();
                    break;
            }
        }
        clientHandler.sendObject(SUCCESS);
    }

    /**
     * This method discards a leader
     * @return true if error and false if not
     * @throws IOException in case of connection problems
     */
    public boolean discardLeader() throws IOException{
        try {
            player.getPersonalBoard().getLeaderBoard().discardLeader(clientHandler.receiveObject(int.class));
            return true;
        }catch (IndexOutOfBoundsException e){
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
            player.getPersonalBoard().playLeader(clientHandler.receiveObject(int.class));
            clientHandler.sendObject(SUCCESS);
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
    private boolean activateProduction(NetworkMessages message) throws IOException {
        boolean error=true;
        switch (message) {
            case PROD1:
                try {
                    player.getPersonalBoard().produce(clientHandler.receiveObject(ResourceTypes.class),
                            clientHandler.receiveObject(ResourceTypes.class),
                            clientHandler.receiveObject(ResourceTypes.class)
                    );
                    error=false;
                } catch (NegativeResourceValueException | FaithOverflowException e) {
                    clientHandler.sendObject(ERROR);
                    clientHandler.sendObject(e.toString());
                }
                break;
            case PROD2:
                try {
                    player.getPersonalBoard().produce(clientHandler.receiveObject(int.class));
                    error=false;
                } catch (UnusableCardException | FaithOverflowException e) {
                    clientHandler.sendObject(ERROR);
                    clientHandler.sendObject(e.toString());
                }
                break;
            case PROD3:
                try {
                    player.getPersonalBoard().produce(clientHandler.receiveObject(ResourceTypes.class),
                            clientHandler.receiveObject(ResourceTypes.class)
                    );
                    error=false;
                } catch (FaithOverflowException | NegativeResourceValueException | UnusableCardException e) {
                    clientHandler.sendObject(ERROR);
                    clientHandler.sendObject(e.toString());
                }
                break;
            default:
                clientHandler.sendObject(ERROR);
                clientHandler.sendObject("Expecting a production action");
                error=true;
        }
        if(!error)
            clientHandler.sendObject(SUCCESS);
        return error;
    }

    /**
     * This method buys a column
     * Expects columnIndex->int
     * @return true if error and false if not
     * @throws IOException in case of connection problems
     */
    private boolean buyColumn() throws IOException, FaithOverflowException {
        try {
            player.getPersonalBoard().buyColumn(clientHandler.receiveObject(int.class), clientHandler.receiveObject(int.class));
            clientHandler.sendObject(SUCCESS);
            return false;
        } catch (IndexOutOfBoundsException e) {
            clientHandler.sendObject(ERROR);
            clientHandler.sendObject(e.toString());
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
            player.getPersonalBoard().buyRow(clientHandler.receiveObject(int.class), clientHandler.receiveObject(int.class));
            clientHandler.sendObject(SUCCESS);
            return false;
        } catch (IndexOutOfBoundsException e) {
            clientHandler.sendObject(ERROR);
            clientHandler.sendObject(e.toString());
            return true;
        }
    }

    /**
     * This method buys a development card
     * Expects cardRow->int, cardColumn->int, placeToInsert->int
     * @return true if error and false if not
     * @throws IOException in case of connection problems
     */
    private boolean buyDevelopmentCard() throws IOException, WinException, EmptyStackException {
        try {
            player.getPersonalBoard().drawCard(clientHandler.receiveObject(int.class),clientHandler.receiveObject(int.class),clientHandler.receiveObject(int.class));
            clientHandler.sendObject(SUCCESS);
            return false;
        } catch (IncompatibleCardLevelException | NegativeResourceValueException e) {
            clientHandler.sendObject(ERROR);
            clientHandler.sendObject(e.toString());
            return true;
        }
    }

    public ClientHandler getClientHandler() {
        return clientHandler;
    }

    /**
     * Game setup for this player
     * @throws IOException In case the communication with the client goes wrong
     */
    public void startGame() throws IOException {
        clientHandler.sendObject(GAMESTARTED);
        clientHandler.sendGame();
    }

    /**
     * Gets all the victory points of a player
     * @return The victory points
     */
    public int getVictoryPoints(){
        return player.getPersonalBoard().getVictoryPoints();
    }

    /**
     * Ends the game for this plaer
     * @param winner True if this player is winner and false if not
     * @throws IOException In case the communication with the client goes wrong
     */
    public void endGame(boolean winner) throws IOException {
        clientHandler.sendObject(GAMEENDED);
        if(winner){
            clientHandler.sendObject(YOUWON);
        }else{
            clientHandler.sendObject(YOULOST);
        }
    }

}
