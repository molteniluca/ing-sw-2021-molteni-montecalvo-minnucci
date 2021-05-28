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
import it.polimi.ingsw.model.exceptions.FaithOverflowException;
import it.polimi.ingsw.model.exceptions.WinException;

import java.io.*;
import java.time.LocalTime;
import java.util.*;

/**
 * Class that represents an entire game
 */
public class Game implements Serializable {
    private final ArrayList<Turn> turns = new ArrayList<>();
    private final String id;
    private final int  numPlayers;
    private transient boolean gameEnded = false;
    private int currentPlayer;

    /**
     * Constructor of the class
     * @param numPlayers The number of players of this game
     * @param clients The client handlers for each player
     * @param playerNames The name of each player
     * @param id The game room id for debugging reasons
     */
    public Game(int numPlayers, ArrayList<ClientHandler> clients, ArrayList<String> playerNames, String id, WaitingRoom waitingRoom) throws IOException {
        this.numPlayers=numPlayers;
        this.id = id;
        Player[] players;
        players = new Player[numPlayers];
        GeneralBoard generalBoard = new GeneralBoard();


        Random rand = new Random();
        int inkwellPlayer = Math.round(rand.nextFloat()*numPlayers);

        ArrayList<LeaderCard>[] leaderCardsInHand = getStartingLeaders();

        for(int i=inkwellPlayer; i<numPlayers; i++){
            clients.get(i).sendObject(i);
            players[i] = new Player(playerNames.get(i),i==inkwellPlayer,generalBoard, leaderCardsInHand[i]);
            turns.add(new PlayerTurn(players[i],clients.get(i),waitingRoom));
        }

        for(int i=0; i<inkwellPlayer; i++){
            clients.get(i).sendObject(i);
            players[i] = new Player(playerNames.get(i),false,generalBoard, leaderCardsInHand[i]);
            turns.add(new PlayerTurn(players[i],clients.get(i),waitingRoom));
        }

        if(numPlayers==1){
            turns.add(new SelfPlayingTurn());
        }
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public void setGameEnded(boolean gameEnded) {
        this.gameEnded = gameEnded;
    }

    /**
     * This method gets and splits the leader cards in 5 for each player
     * @return An array of lists of leader cards
     */
    @SuppressWarnings("unchecked")
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
     * @throws IOException In case a client disconnects
     */
    public void startGame() throws IOException {
        printDebug("Game started");
        for(Turn turn : turns){
            turn.startGame();
        }

        while(!gameEnded){
            for(currentPlayer = 0; currentPlayer < numPlayers; currentPlayer++){
                try {
                    turns.get(currentPlayer).beginTurn();
                } catch (FaithOverflowException | WinException | EmptyStackException e) {
                    gameEnded=true;
                }
            }
        }

        ArrayList<Integer> victoryPoints=getAllVictoryPoints();
        int maxValue = Collections.max(victoryPoints);

        for(int i=0; i<numPlayers; i++){
            turns.get(i).endGame(maxValue == victoryPoints.get(i));
        }
    }

    private ArrayList<Integer> getAllVictoryPoints(){
        ArrayList<Integer> points= new ArrayList<>();
        for (Turn turn : turns){
            points.add(turn.getVictoryPoints());
        }
        return points;
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
        reader = new JsonReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream("json/leaderCards.json")));


        return gson.fromJson(reader,  new TypeToken<ArrayList<LeaderCard>>(){}.getType());
    }


    /**
     * Debug method that prints in the server's stdout debug messages regarding this game
     * @param s The message
     */
    private void printDebug(String s){
        System.out.println(LocalTime.now().toString() + "\t\tGame[ID:" + id +"] -> "+s);
    }


    public PlayerTurn getTurn(int i)
    {
        return (PlayerTurn) turns.get(i);
    }
}
