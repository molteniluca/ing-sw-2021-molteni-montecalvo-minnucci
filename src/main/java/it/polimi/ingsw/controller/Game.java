package it.polimi.ingsw.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.board.general.GeneralBoard;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.model.cards.specialAbility.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Class that represents an entire game
 */
public class Game{
    private GeneralBoard generalBoard;
    private ArrayList<PlayerTurn> playerTurns = new ArrayList<>();

    /**
     * Constructor of the class
     * @param numPlayers The number of players of this game
     * @param clients The client handlers for each player
     * @param playerNames The name of each player
     * @throws IOException In case there's a problem communicating with the client
     */
    public Game(int numPlayers, ArrayList<ClientHandler> clients, ArrayList<String> playerNames) throws IOException {
        Player[] players;
        players = new Player[numPlayers];
        generalBoard = new GeneralBoard();

        Random rand = new Random();
        int inkwellPlayer = (int) rand.nextFloat()*numPlayers;

        ArrayList<LeaderCard>[] leaderCardsInHand = getStartingLeaders();

        for(int i=0; i<numPlayers; i++){
            players[i] = new Player(playerNames.get(i),i==inkwellPlayer,generalBoard,clients.get(i), leaderCardsInHand[i]);
            playerTurns.add(new PlayerTurn(players[i]));
        }

        startGame(inkwellPlayer);
    }

    /**
     * This method gets and splits the leader cards in 5 for each player
     * @return An array of lists of leader cards
     */
    private ArrayList<LeaderCard>[] getStartingLeaders(){
        ArrayList<LeaderCard> []array = new ArrayList[4];
        ArrayList<LeaderCard> cards = getLeadersFromFile();
        Collections.shuffle(cards);

        for(int i=0;i<4;i++){
            array[i]=new ArrayList<>();

            for(int k=0; k<4; k++) {
                array[i].add(cards.remove(cards.size()-1));
            }
        }
        return array;
    }

    /**
     * Starts the game
     * @param startingPlayer The first player to begin
     * @throws IOException In case a client disconnects
     */
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

    /**
     * Gets the leader cards form the local json resource
     * @return The leader cards
     */
    private ArrayList<LeaderCard> getLeadersFromFile(){
        RuntimeTypeAdapterFactory<SpecialAbility> abilityAdapterFactory
                = RuntimeTypeAdapterFactory.of(SpecialAbility.class, "type");

        abilityAdapterFactory.registerSubtype(Discount.class, "Discount");
        abilityAdapterFactory.registerSubtype(ExtraDeposit.class, "ExtraDeposit");
        abilityAdapterFactory.registerSubtype(ExtraResource.class, "ExtraResource");
        abilityAdapterFactory.registerSubtype(ExtraProduction.class, "ExtraProduction");

        Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(abilityAdapterFactory)
                .create();

        JsonReader reader = null;
        try {
            reader = new JsonReader(new FileReader(ClassLoader.getSystemResource("json/leaderCards.json").getPath()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        return gson.fromJson(reader,  new TypeToken<ArrayList<LeaderCard>>(){}.getType());
    }
}
