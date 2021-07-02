package it.polimi.ingsw.view;

import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.network.NetworkMessages;
import it.polimi.ingsw.network.ObjectUpdate;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import static it.polimi.ingsw.network.NetworkMessages.*;

/**
 * Object that handles a connection with the server
 */
public class NetworkHandler extends Thread{
    private final Socket server;
    private final ObjectInputStream in;
    private final ObjectOutputStream out;
    private final View view;
    private final HeartBeatThreadClient heartBeatThreadClient;
    private boolean alreadyClosed=false;

    /**
     * Constructor of the class
     * @param serverAddress The address of the server
     * @param serverPort The port of the server
     * @param view The view to be notified when events happen
     * @throws IOException In case the communication with the server could not be established
     */
    public NetworkHandler(String serverAddress, int serverPort, View view) throws IOException {
        this.view=view;
        server = new Socket(serverAddress, serverPort);
        out = new ObjectOutputStream(server.getOutputStream());
        in = new ObjectInputStream(server.getInputStream());
        heartBeatThreadClient = new HeartBeatThreadClient(this);
    }

    @Override
    public void run() {
        while (!server.isClosed()){
            try {
                view.notifyResponse(receiveObject());
            } catch (IOException e) {
               closeConnection();
            }
        }
    }

    /**
     * Method that receives an object from the server
     * @return The object received
     * @throws IOException In case the server has disconnected
     */
    private Object receiveObject() throws IOException {
        Object read = null;
        while(read==null){
            heartBeatThreadClient.messageReceived();
            try {
                read = in.readObject();
                if(read==null)
                    continue;
                if(read.getClass() == NetworkMessages.class) {
                    switch((NetworkMessages)read) {
                        case HEARTBEAT:
                            read = null;
                            continue;
                        case YOUWON:
                            read=null;
                            view.notifyEndGame(true);
                            continue;
                        case YOULOST:
                            read=null;
                            view.notifyEndGame(false);
                            continue;
                        case TURNBEGIN:
                            view.notifyTurnStarted();
                            continue;
                        case ENDTURN:
                            read = SUCCESS;
                            view.notifyTurnEnded();
                    }
                }
                if(read.getClass() == Game.class) {
                    view.notifyNewGame((Game) read);
                    read=null;
                }else if(read.getClass() == ObjectUpdate.class) {
                    view.notifyNewUpdate((ObjectUpdate) read);
                    read=null;
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return read;
    }

    /**
     * Sends an object to the server
     * @param o The object to be sent
     * @throws IOException In case the server has disconnected
     */
    public synchronized void sendObject(Object o) throws IOException {
        out.writeObject(o);
    }

    /**
     * Sends an heartbeat to the server
     * @throws IOException In case the server has disconnected
     */
    public void sendHeartBeat() throws IOException {
        sendObject(HEARTBEAT);
    }

    /**
     * Closes the connection with the server
     */
    public synchronized void closeConnection(){
        if(!alreadyClosed) {
            view.notifyResponse(ERROR);
            view.notifyResponse("Action not completed because of a network error");
            this.notifyAll();
            alreadyClosed=true;
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            view.notifyDisconnection();
        }
    }
}
