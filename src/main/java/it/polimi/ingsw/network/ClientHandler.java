package it.polimi.ingsw.network;

import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.network.exceptions.FullRoomException;
import static it.polimi.ingsw.network.NetworkMessages.*;

import java.io.*;
import java.net.Socket;
import java.time.LocalTime;
import java.util.*;


/**
 * This class represents an entity that communicates with the client and executes the basic operations
 */
public class ClientHandler extends Thread{
    private final Socket client;
    private final HashMap<String,WaitingRoom> waitingRooms;
    private final Queue<ObjectUpdate> objectUpdates = new LinkedList<>();
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

            new HeartbeatThreadServer(this);

            if(command == CREATEGAME){
                createGame(receiveObject(Integer.class));
            }else if(command == JOINGAME){
                joinGame(receiveObject(String.class));
            }else{
                sendObject(ERROR);

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
                synchronized (this){
                    this.wait();
                    refreshObjects();
                }
            } catch (InterruptedException | IOException e) {
                break;
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
    public synchronized <T> T receiveObject(Class<? extends T> c) throws IOException {
        Object read = null;
        while(read==null){
            try {
                read = in.readObject();
                if(read.getClass() == NetworkMessages.class) {
                    if (read == HEARTBEAT) {
                        read = null;
                        continue;
                    }
                }
                if(read.getClass() != c){
                    sendObject(ERROR);
                    sendObject("Unexpected object, expecting:"+c.toString()+", but got:"+read.getClass());
                    printDebug("Unexpected object, expecting:"+c+", but got:"+read.getClass());
                    read = null;
                }
            } catch (ClassNotFoundException e) {
                sendObject(ERROR);
                sendObject("Unexpected object");
                printDebug("Unexpected object");
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
        out.reset();
        out.writeObject(o);
    }

    /**
     * Creates a new game and adds it to the sever list
     * @param numPlayers Number of players to add
     * @throws IOException In case there's a problem communicating with the client
     */
    private void createGame(int numPlayers) throws IOException {
        synchronized (waitingRooms){
            String id = randomizeId();
            while(waitingRooms.containsKey(id))
                id = randomizeId();
            waitingRooms.put(id,new WaitingRoom(numPlayers,id));

            printDebug("New game ID:"+id+"\tPlayers:"+(numPlayers));

            sendObject(SUCCESS);
            sendObject(id);

            joinGame(id);
        }
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
            out.writeObject(FULLROOMERROR);
            printDebug("Trying to join a full room:"+id);
            client.close();
        } catch (NullPointerException e){
            out.writeObject(UNKNOWNIDERROR);
            printDebug("Trying to join a not existing room:"+id);
            client.close();
        }
    }

    /**
     * Debug method that prints in the server's stdout debug messages regarding this client
     * @param s The message
     */
    private void printDebug(String s){
        System.out.println(LocalTime.now().toString().substring(0,14) + "\t\t" + client.toString() +" -> "+s);
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
    public void sendHeartBeat() throws IOException {
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

    /**
     * Handles the disconnection of this client
     */
    public void handleDisconnect(){
        printDebug("Invoked disconnect");
        if(getWaitingRoom()!=null) {
            getWaitingRoom().closeAll();
            getWaitingRoom().interrupt();
            waitingRooms.remove(id);
        }else{
            this.closeConnection();
        }
    }

    /**
     * Gets the game associated with this client
     * @return The game object
     */
    private Game getGame(){
        return waitingRooms.get(id).getGame();
    }

    /**
     * Sends the entire game to the client
     */
    public void sendGame() throws IOException {
        Game g=getGame();
        sendObject(g);
    }

    /**
     * Method used to receive a network message from the client
     * @return The received message
     */
    public NetworkMessages receiveMessage() throws IOException {
        return receiveObject(NetworkMessages.class);
    }

    /**
     * Gets the waiting room associated with this client
     * @return The waiting room
     */
    public WaitingRoom getWaitingRoom(){
        return waitingRooms.get(id);
    }

    /**
     * Refreshes client objects
     */
    private void refreshObjects() throws IOException {
        while (objectUpdates.size()!=0)
            sendObject(objectUpdates.remove());
    }

    /**
     * Method that inserts an update in the sending queue
     * @param objectUpdate The object to be sent
     */
    public void insertUpdate(ObjectUpdate objectUpdate){
        this.objectUpdates.add(objectUpdate);
    }
}
