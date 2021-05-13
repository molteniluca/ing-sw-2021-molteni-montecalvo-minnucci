package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.exceptions.FullRoomException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * This class represents a waiting room where all the sockets hang before the game starts
 */
public class WaitingRoom extends Thread{
    private final int numPlayers;
    private final ArrayList<ClientHandler> clients = new ArrayList<>();
    private final ArrayList<String> clientsNames = new ArrayList<>();

    /**
     * The constructor of the class
     * @param numPlayers The number of players in this game
     */
    public WaitingRoom(int numPlayers) {
        this.numPlayers=numPlayers;
    }

    public void run(){
        try {
            new Game(numPlayers, clients, clientsNames);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
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
}
