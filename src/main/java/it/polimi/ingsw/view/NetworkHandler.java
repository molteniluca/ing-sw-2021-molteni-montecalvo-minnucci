package it.polimi.ingsw.view;

import it.polimi.ingsw.controller.NetworkMessages;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import static it.polimi.ingsw.controller.NetworkMessages.*;


public class NetworkHandler extends Thread{

    private static int heartBeatInterval=50000; /*Heartbeat interval in milliseconds*/
    private Socket server;
    private ObjectInputStream in = null;
    private ObjectOutputStream out = null;

    public NetworkHandler(String serverAddress, int serverPort)
    {
        try {
            server = new Socket(serverAddress, serverPort);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        NetworkMessages command;

            try {
                out = new ObjectOutputStream(server.getOutputStream());
                in = new ObjectInputStream(server.getInputStream());


            } catch (IOException  e) {
                e.printStackTrace();
            }

/*
        while (!server.isClosed()){
            try {
                sendHeartBeat();
                sleep(heartBeatInterval);
            } catch (IOException e) {
                //printDebug("Error client disconnected!");
               closeConnection();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }*/

    }



    /**
     * Receive an object of type c from the client
     * @param c The type of the object
     * @return The object received from the client
     * @throws IOException In case there's a problem communicating with the client
     * @throws ClassNotFoundException In case the client sends an unknown class
     * @throws ClassCastException In case the client doesn't send the specified type of object
     */
    public <T> T receiveObject(Class<? extends T> c) throws IOException, ClassNotFoundException, ClassCastException {
        Object read = null;
        while(read==null){
            read = in.readObject();
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


    private void sendHeartBeat() throws IOException {
        sendObject(HEARTBEAT);
    }

    /**
     * Closes a connection with the client
     */
    private void closeConnection(){
        try {
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
