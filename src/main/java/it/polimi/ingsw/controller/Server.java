package it.polimi.ingsw.controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalTime;
import java.util.HashMap;

/**
 * This class represents the server which handles the incoming connections and hands them to a client handler
 */
public class Server {
    private final HashMap<String,WaitingRoom> rooms;
    public int port;

    /**
     * The constructor
     * @param port The port number where to bind the server
     */
    public Server(int port)
    {
        rooms=new HashMap<>();
        this.port=port;

        try {
            waitForConnection(new ServerSocket(port));
        } catch (IOException e) {
            System.out.println("Can't bind on this port!");
        }
    }

    /**
     * This method waits for a new connection and hands it to the accept connection method
     * @param serverSocket The socket of the server
     */
    private void waitForConnection(ServerSocket serverSocket){
        printDebug("Server started");
        while(true){
            try {
                acceptConnection(serverSocket.accept());
            } catch (IOException e) {
                System.out.println("Connection to the client failed:" + e.getMessage());
            }
        }
    }

    /**
     * Accepts a connections and hands the socket to a client handler
     * @param client The client socket
     */
    private void acceptConnection(Socket client){
        new ClientHandler(client,rooms).start();
    }

    /**
     * Debug method that prints in the server's stdout debug messages regarding the server
     * @param s The message
     */
    private void printDebug(String s){
        System.out.println(LocalTime.now().toString() + "\t\t" + "SERVER -> "+s);
    }

}
