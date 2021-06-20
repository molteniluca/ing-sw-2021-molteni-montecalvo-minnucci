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
import it.polimi.ingsw.model.exceptions.NotEnoughCardException;
import it.polimi.ingsw.model.exceptions.WinException;
import it.polimi.ingsw.network.ClientHandler;

import java.io.*;
import java.time.LocalTime;
import java.util.*;

/**
 * Class that represents an entire game
 */
public class Game implements Serializable {
    public static final long serialVersionUID = 6732146736278436274L;
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
    public Game(int numPlayers, ArrayList<ClientHandler> clients, ArrayList<String> playerNames, String id) {
        int playerCount=0;
        this.numPlayers=numPlayers;
        this.id = id;
        Player[] players;
        players = new Player[numPlayers];
        GeneralBoard generalBoard = new GeneralBoard();


        Random rand = new Random();
        int inkwellPlayer = Math.round(rand.nextFloat()*numPlayers);

        ArrayList<LeaderCard>[] leaderCardsInHand = getStartingLeaders();

        for(int i=inkwellPlayer; i<numPlayers; i++){
            players[i] = new Player(playerNames.get(i),i==inkwellPlayer,generalBoard, leaderCardsInHand[i]);
            turns.add(new PlayerTurn(players[i],clients.get(i), playerCount));
            playerCount++;
        }

        for(int i=0; i<inkwellPlayer; i++){
            players[i] = new Player(playerNames.get(i),false,generalBoard, leaderCardsInHand[i]);
            turns.add(new PlayerTurn(players[i],clients.get(i), playerCount));
            playerCount++;
        }

        if(numPlayers==1){
            turns.add(new SelfPlayingTurn(generalBoard));
        }

        printDebug("Game created");
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

        if(numPlayers!=1)
            multiplePlayerGame();
        else
            singlePlayerGame();
    }

    /**
     * Executes a single player game
     * @throws IOException In case a client disconnects
     */
    private void singlePlayerGame() throws IOException {
        while(!gameEnded){
            for(currentPlayer = 0; currentPlayer < 2; currentPlayer++){
                try {
                    turns.get(currentPlayer).beginTurn();
                } catch (FaithOverflowException e) {
                    printDebug("The game has ended, player " + currentPlayer + " triggered: " + e.getMessage());
                    gameEnded=true;
                    turns.get(0).endGame(currentPlayer==0);
                } catch (NotEnoughCardException e) {
                    printDebug("The game has ended, player " + currentPlayer + " triggered: " + e.getMessage());
                    gameEnded=true;
                    turns.get(0).endGame(false);
                } catch (WinException e) {
                    printDebug("The game has ended, player " + currentPlayer + " triggered: " + e.getMessage());
                    gameEnded=true;
                    turns.get(0).endGame(true);
                }
            }
        }
    }

    /**
     * Starts a multiple player game
     * @throws IOException In case a client disconnects
     */
    private void multiplePlayerGame() throws IOException {
        while(!gameEnded){
            for(currentPlayer = 0; currentPlayer < numPlayers; currentPlayer++){
                try {
                    turns.get(currentPlayer).beginTurn();
                } catch (FaithOverflowException | WinException | NotEnoughCardException e) {
                    gameEnded=true;
                    printDebug("The game has ended, player " + currentPlayer + " triggered: " + e.getMessage());
                }
            }
        }

        ArrayList<Integer> victoryPoints = getAllVictoryPoints();
        int maxValue = Collections.max(victoryPoints);

        for (int i = 0; i < numPlayers; i++) {
            turns.get(i).endGame(maxValue == victoryPoints.get(i));
        }
    }

    /**
     * Gets all the victory points from the players
     * @return A list with all the victory points
     */
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

        JsonReader reader = new JsonReader(new InputStreamReader(Objects.requireNonNull(ClassLoader.getSystemResourceAsStream("json/leaderCards.json"))));


        return gson.fromJson(reader,  new TypeToken<ArrayList<LeaderCard>>(){}.getType());
    }


    /**
     * Debug method that prints in the server's stdout debug messages regarding this game
     * @param s The message
     */
    private void printDebug(String s){
        System.out.println(LocalTime.now().toString().substring(0,14) + "\t\tGame[ID:" + id +"] -> "+s);
    }


    public PlayerTurn getPlayerTurn(int i)
    {
        return (PlayerTurn) turns.get(i);
    }

    public SelfPlayingTurn getSelfPLayingTurn()
    {
        return (SelfPlayingTurn) turns.get(1);
    }
}
