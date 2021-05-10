package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.exceptions.FullRoomException;

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
    Socket client;
    HashMap<String,WaitingRoom> waitingRooms;
    ObjectInputStream in = null;
    ObjectOutputStream out = null;

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
        String command;

        try {
            in = new ObjectInputStream(client.getInputStream());
            out = new ObjectOutputStream(client.getOutputStream());

            command = receiveObject(String.class);

            if(command.equals("createGame")){
                createGame(receiveObject(Integer.class));
            }else if(command.equals("joinGame")){
                joinGame(receiveObject(String.class));
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Receive an object of type c from the client
     * @param c The type of the object
     * @return The object recived from the client
     * @throws IOException In case there's a problem communicating with the client
     * @throws ClassNotFoundException In case the client sends an unknown class
     * @throws ClassCastException In case the client doesn't send the specified type of object
     */
    private <T> T receiveObject(Class<? extends T> c) throws IOException, ClassNotFoundException, ClassCastException {
        Object read = null;
        while(read==null){
            read = in.readObject();
        }
        return c.cast(read);
    }

    /**
     * Creates a new game and adds it to the sever list
     * @param numPlayers Number of players to add
     * @throws IOException In case there's a problem communicating with the client
     */
    private void createGame(int numPlayers) throws IOException {
        String id = randomizeId();
        waitingRooms.put(id,new WaitingRoom(numPlayers));

        printDebug("New game,Players:"+((Integer)numPlayers).toString()+" ID:"+id);

        out.writeObject("CREATE SUCCESS ID:"+id);

        joinGame(id);
    }

    /**
     * Joins a game contained in the game list
     * @param id The id of the game
     * @throws IOException In case there's a problem communicating with the client
     */
    private void joinGame(String id) throws IOException{
        try {
            waitingRooms.get(id).joinRoom(client);
            printDebug("Joined game:"+id);
        } catch (FullRoomException e) {
            out.writeObject("ERROR! THE ROOM IS FULL");
            printDebug("FULL ROOM:"+id);
        } catch (NullPointerException e){
            out.writeObject("THIS ROOM DOESN'T EXIST!");
            printDebug("NULL ROOM:"+id);
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
}
