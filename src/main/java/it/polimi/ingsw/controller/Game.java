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

import static it.polimi.ingsw.controller.NetworkMessages.*;

/**
 * Class that represents an entire game
 */
public class Game implements Serializable {
    private static final long serialVersionUID = 6732146736278436274L;

    private final ArrayList<PlayerTurn> playerTurns = new ArrayList<>();
    private final String id;
    private boolean gameEnded = false;
    private final int  numPlayers;

    /**
     * Constructor of the class
     * @param numPlayers The number of players of this game
     * @param clients The client handlers for each player
     * @param playerNames The name of each player
     * @param id The game room id for debugging reasons
     */
    public Game(int numPlayers, ArrayList<ClientHandler> clients, ArrayList<String> playerNames, String id){
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
            playerTurns.add(new PlayerTurn(players[i],clients.get(i)));
        }

        for(int i=0; i<inkwellPlayer; i++){
            players[i] = new Player(playerNames.get(i),false,generalBoard, leaderCardsInHand[i]);
            playerTurns.add(new PlayerTurn(players[i],clients.get(i)));
        }
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
        for(PlayerTurn p : playerTurns){
            p.getClientHandler().sendObject(GAMESTARTED);
            p.getClientHandler().refreshClientObjects();
        }

        while(!gameEnded){
            for(PlayerTurn p: playerTurns){
                try {
                    p.beginTurn();
                } catch (FaithOverflowException | WinException | EmptyStackException e) {
                    gameEnded=true;
                }
            }
        }

        ArrayList<Integer> victoryPoints=getAllVictoryPoints();
        int maxValue = Collections.max(victoryPoints);

        for(int i=0; i<numPlayers; i++){
            playerTurns.get(i).getClientHandler().sendObject(GAMEENDED);
            if(maxValue == victoryPoints.get(i)){
                playerTurns.get(i).getClientHandler().sendObject(YOUWON);
            }else{
                playerTurns.get(i).getClientHandler().sendObject(YOULOST);
            }
        }
    }

    private ArrayList<Integer> getAllVictoryPoints(){
        ArrayList<Integer> points= new ArrayList<>();
        for (PlayerTurn p : playerTurns){
            points.add(p.getPlayer().getPersonalBoard().getVictoryPoints());
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
        try {
            reader = new JsonReader(new FileReader(ClassLoader.getSystemResource("json/leaderCards.json").getPath()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        return gson.fromJson(reader,  new TypeToken<ArrayList<LeaderCard>>(){}.getType());
    }


    /**
     * Debug method that prints in the server's stdout debug messages regarding this game
     * @param s The message
     */
    private void printDebug(String s){
        System.out.println(LocalTime.now().toString() + "\t\tGame[ID:" + id +"] -> "+s);
    }
}
