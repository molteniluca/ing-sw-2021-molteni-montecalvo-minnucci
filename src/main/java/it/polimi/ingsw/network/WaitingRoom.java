package it.polimi.ingsw.network;

import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.network.exceptions.FullRoomException;

import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;

/**
 * This class represents a waiting room where all the sockets hang before the game starts
 */
public class WaitingRoom extends Thread{
    private final int numPlayers;
    private final String id;
    private final ArrayList<ClientHandler> clients = new ArrayList<>();
    private final ArrayList<String> clientsNames = new ArrayList<>();
    private Game game=null;
    private boolean connectionsClosed=false;

    /**
     * The constructor of the class
     * @param numPlayers The number of players in this game
     */
    public WaitingRoom(int numPlayers, String id) {
        this.id = id;
        this.numPlayers=numPlayers;
    }

    public Game getGame() {
        return game;
    }

    public void run(){
        try {
            game=new Game(numPlayers, clients, clientsNames, id, this);
            game.startGame();
        } catch (IOException e) {
            printDebug("Socket error, a client has disconnected");
            game.setGameEnded(true);
            closeAll();
        }
    }

    /**
     * Adds a client to this waiting room
     * @param client The client to add
     * @throws FullRoomException In case the room is full
     */
    public synchronized void joinRoom(ClientHandler client, String name) throws FullRoomException {
        if(clients.size()>=numPlayers){
            throw new FullRoomException("This waiting room is already full and the game is started!");
        }else {
            clients.add(client);
            clientsNames.add(name);
            if (clients.size() == numPlayers) {
                this.start();
            }
        }
    }

    /**
     * Closes all clients connections
     */
    public synchronized void closeAll(){
        if(!connectionsClosed)
        {
            for(ClientHandler c : clients){
                c.closeConnection();
            }
            printDebug("Game ended");
            connectionsClosed=true;
        }
    }

    /**
     * Debug method that prints in the server's stdout debug messages regarding this game
     * @param s The message
     */
    private void printDebug(String s){
        System.out.println(LocalTime.now().toString() + "\t\tGame[ID:" + id +"] -> "+s);
    }

    /**
     * Notifies the clients the new objects
     * @param o The new objects
     */
    private void notifyClients(Object o){
        ObjectUpdate objectUpdate=new ObjectUpdate(o,this.game.getCurrentPlayer());

        for(ClientHandler c : clients){
            c.insertUpdate(objectUpdate);
        }

        for(ClientHandler c : clients){
            c.notify();
        }
    }
}
