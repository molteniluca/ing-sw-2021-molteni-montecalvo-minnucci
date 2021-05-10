package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.exceptions.FullRoomException;

import java.net.Socket;
import java.util.ArrayList;

/**
 * This class represents a waiting room where all the sockets hang before the game starts
 */
public class WaitingRoom extends Thread{
    int numPlayers;
    ArrayList<Socket> clients = new ArrayList<>();

    /**
     * The constructor of the class
     * @param numPlayers The number of players in this game
     */
    public WaitingRoom(int numPlayers) {
        this.numPlayers=numPlayers;
    }

    public void Run(){
        //start the game
    }

    /**
     * Adds a client to this waiting room
     * @param client The client to add
     * @throws FullRoomException In case the room is full
     */
    public void joinRoom(Socket client) throws FullRoomException {
        if(clients.size()>=numPlayers){
            throw new FullRoomException("This waiting room is already full and the game is started!");
        }else {
            clients.add(client);
            if (clients.size() == numPlayers) {
                this.start();
            }
        }
    }
}
