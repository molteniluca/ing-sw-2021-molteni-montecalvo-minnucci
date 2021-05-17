package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.exceptions.FullRoomException;
import static it.polimi.ingsw.controller.NetworkMessages.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Random;


/**
 * This class represents an entity that communicates with the client and executes the basic operations
 */
public class ClientHandler extends Thread{
    private static final int heartBeatInterval=10000; /*Heartbeat interval in milliseconds*/

    private final Socket client;
    private final HashMap<String,WaitingRoom> waitingRooms;
    private ObjectInputStream in = null;
    private ObjectOutputStream out = null;
    private String id=null;

    /**
     * Constructor of the class
     * @param client The client socket to handle
     * @param waitingRooms The list of waiting rooms
     */
    public ClientHandler(Socket client, HashMap<String,WaitingRoom> waitingRooms){
        this.client=client;
        this.waitingRooms=waitingRooms;
    }

    /**
     * Waits for commands from the client and executes them
     */
    @Override
    public void run() {
        printDebug("Connected");
        NetworkMessages command;

        try {
            in = new ObjectInputStream(client.getInputStream());
            out = new ObjectOutputStream(client.getOutputStream());

            command = receiveObject(NetworkMessages.class);

            if(command == CREATEGAME){
                createGame(receiveObject(Integer.class));
            }else if(command == JOINGAME){
                joinGame(receiveObject(String.class));
            }else{
                client.close();
            }
        } catch (IOException e) {
            try {
                client.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

        while (!client.isClosed()){
            try {
                sendHeartBeat();
                sleep(heartBeatInterval);
            } catch (IOException e) {
                printDebug("Error client disconnected!");
                waitingRooms.get(id).closeAll();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        printDebug("Client Disconnected");
    }

    /**
     * Receive an object of type c from the client
     * @param c The type of the object
     * @return The object received from the client
     * @throws IOException In case there's a problem communicating with the client
     * @throws ClassCastException In case the client doesn't send the specified type of object
     */
    public <T> T receiveObject(Class<? extends T> c) throws IOException {
        Object read = null;
        while(read==null){
            try {
                read = in.readObject();
                if(read.getClass() != c){
                    read = null;
                }
            } catch (ClassNotFoundException e) {
                continue;
            }
            if(read==null){
                sendObject(ERROR);
                sendObject("Unexpected object, expecting:"+c.toString());
                printDebug("Unexpected object, expecting:"+c.toString());
            }
        }

        return c.cast(read);
    }

    /**
     * Sends an object to the client
     * @param o The object to be sent
     * @throws IOException In case there's a problem communicating with the client
     */
    public synchronized void sendObject(Object o) throws IOException {
        out.writeObject(o);
    }

    /**
     * Creates a new game and adds it to the sever list
     * @param numPlayers Number of players to add
     * @throws IOException In case there's a problem communicating with the client
     */
    private void createGame(int numPlayers) throws IOException {
        String id = randomizeId();
        waitingRooms.put(id,new WaitingRoom(numPlayers,id));

        printDebug("New game ID:"+id+"\tPlayers:"+((Integer)numPlayers).toString());

        sendObject(SUCCESS);
        sendObject(id);

        joinGame(id);
    }

    /**
     * Joins a game contained in the game list
     * @param id The id of the game
     * @throws IOException In case there's a problem communicating with the client
     */
    private void joinGame(String id) throws IOException {
        try {
            waitingRooms.get(id).joinRoom(this, receiveObject(String.class));
            this.id=id;
            printDebug("Joined game:"+id);
        } catch (FullRoomException e) {
            out.writeObject(ERROR);
            out.writeObject("THE ROOM IS FULL!");
            printDebug("Trying to join a full room:"+id);
            client.close();
        } catch (NullPointerException e){
            out.writeObject(ERROR);
            out.writeObject("THIS ROOM DOESN'T EXIST!");
            printDebug("Trying to join a not existing room:"+id);
            client.close();
        }
    }

    /**
     * Debug method that prints in the server's stdout debug messages regarding this client
     * @param s The message
     */
    private void printDebug(String s){
        System.out.println(LocalTime.now().toString() + "\t\t" + client.toString() +" -> "+s);
    }

    /**
     * Method that randomizes the id of a new game
     * @return A five char code string
     */
    private String randomizeId(){
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder id= new StringBuilder();
        Random rand = new Random();
        for(int i=0;i<5;i++){
            id.append(chars.charAt((int) (rand.nextFloat() * chars.length())));
        }
        return id.toString();
    }

    /**
     * Sends the heart beat to the client to make sure is working
     * @throws IOException In case the server can't communicate with the client
     */
    private void sendHeartBeat() throws IOException {
        sendObject(HEARTBEAT);
    }

    /**
     * Closes a connection with the client
     */
    public void closeConnection(){
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Game getGame(){
        return waitingRooms.get(id).getGame();
    }

    public void refreshClientObjects() throws IOException {
        sendObject(getGame());
    }

    public NetworkMessages receiveMessage() throws IOException {
        return receiveObject(NetworkMessages.class);
    }
}
