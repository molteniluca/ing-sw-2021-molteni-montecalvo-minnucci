package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.model.board.personal.FaithTrack;
import it.polimi.ingsw.model.board.personal.storage.WarehouseDepots;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.network.NetworkMessages;
import it.polimi.ingsw.network.ObjectUpdate;
import it.polimi.ingsw.view.NetworkHandler;
import it.polimi.ingsw.view.View;

import java.io.IOException;
import java.util.ArrayList;

import static it.polimi.ingsw.network.NetworkMessages.CREATEGAME;
import static it.polimi.ingsw.network.NetworkMessages.SUCCESS;
import static it.polimi.ingsw.view.CLI.ColorCLI.*;

public class GUIView extends View {
    public static GUIView singleton;
    public Game game;
    NetworkHandler networkHandler;
    public int playerNumber;
    
    @Override
    public void initializeView() {
        try {
            networkHandler=new NetworkHandler("127.0.0.1",10000,this);
            networkHandler.start();
            networkHandler.sendObject(CREATEGAME);
            networkHandler.sendObject(1);
            networkHandler.sendObject("Example");


            NetworkMessages command = (NetworkMessages) waitAndGetResponse();

            if(command == SUCCESS) {
                String roomId = (String) waitAndGetResponse();
                System.out.println("\nYou created a game successfully, your room id is " + ANSI_PURPLE + roomId);
            }
            else
                System.out.println(ANSI_RED+"Something went wrong, exiting");
            waitForUpdatedGame();

            //System.out.println(game.getTurn(0).getPlayer().getName()); // prints the name of a player
            playerNumber = (int) waitAndGetResponse();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void welcomeInfo() {

    }

    @Override
    public void askCreateOrJoin() {

    }

    @Override
    public void askServerInfo() {

    }

    @Override
    public void askNickname() {

    }

    @Override
    public void showHomepage() {

    }

    @Override
    public void showFaithTrack(FaithTrack faithTrack) {

    }

    @Override
    public void showWarehouse(WarehouseDepots warehouseDepots) {

    }

    @Override
    public void showStrongbox(int showPlayer) {

    }

    @Override
    protected boolean isSuccessReceived() {
        return false;
    }

    @Override
    public void updateObjects(Game game) {
        this.game=game;
    }

    @Override
    protected void notifyEndGame(boolean youWon) {

    }

    @Override
    public void notifyNewUpdate(ObjectUpdate read) {
        switch (read.getUpdateType()){
            case LEADERCARDS:
                game.getPlayerTurn(read.getPlayer()).getPlayer().getPersonalBoard().getLeaderBoard()
                        .setLeaderCardsInHand((ArrayList<LeaderCard>) read.getObject());
                break;
        }
        gameUpdated =true;
        synchronized (this) {
            this.notify();
        }
    }

    public static GUIView singleton(){
        if(singleton == null){
            singleton = new GUIView();
        }
        return singleton;
    }
}
