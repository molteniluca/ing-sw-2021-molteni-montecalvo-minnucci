package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.board.general.GeneralBoard;
import it.polimi.ingsw.model.cards.LeaderCard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Game{
    private GeneralBoard generalBoard;
    private ArrayList<PlayerTurn> playerTurns = new ArrayList<>();

    public Game(int numPlayers, ArrayList<ClientHandler> clients, ArrayList<String> clientsNames) throws IOException {
        Player[] players;
        players = new Player[numPlayers];
        generalBoard = new GeneralBoard();

        Random rand = new Random();
        int inkwellPlayer = (int) rand.nextFloat()*numPlayers;

        ArrayList<LeaderCard>[] leaderCardsInHand = getStartingLeaders();

        for(int i=0; i<numPlayers; i++){
            players[i] = new Player(clientsNames.get(i),i==inkwellPlayer,generalBoard,clients.get(i), leaderCardsInHand[i]);
            playerTurns.add(new PlayerTurn(players[i]));
        }

        startGame(inkwellPlayer);
    }

    private ArrayList<LeaderCard>[] getStartingLeaders(){
        ArrayList<LeaderCard> []array = new ArrayList[4];
        for(int i=0;i<4;i++){
            array[i]=new ArrayList<>();
        }
        return array;
    }

    private void startGame(int startingPlayer) throws IOException {

        for(int i=startingPlayer; i<playerTurns.size(); i++){
            playerTurns.get(i).beginTurn();
        }

        while(true){
            for(PlayerTurn p: playerTurns){
                p.beginTurn();
            }
        }
    }
}
