package it.polimi.ingsw.network;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Class that represents a demon that keeps an eye on the connection and notifies if there are problems
 */
public class HeartbeatThreadServer extends TimerTask {
    private static final long interval = 10000;
    private static final int threshold = 2;
    private final ClientHandler clientHandler;
    private boolean isWaitingForMessage;
    private int countFromDisconnect;
    private boolean messageReceived;

    /**
     * Constructor of the class
     * @param c The client handler that handles the connection
     */
    public HeartbeatThreadServer(ClientHandler c){
        clientHandler=c;
        Timer t = new Timer();
        t.schedule(this,interval,interval);
        isWaitingForMessage=false;
        messageReceived=false;
    }

    @Override
    public void run() {
        if(clientHandler==null){
            this.cancel();
        }else {
            try {
                clientHandler.sendHeartBeat();
                if(isWaitingForMessage){
                    if(!messageReceived){
                        countFromDisconnect++;
                        if(countFromDisconnect >= threshold) {
                            clientHandler.closeConnection();
                            this.cancel();
                        }
                    }else
                        messageReceived=false;
                }
            } catch (IOException e) {
                clientHandler.handleDisconnect();
                this.cancel();
            }
        }
    }

    /**
     * Notifies that the server is waiting for a message from this client
     */
    public void notifyIsWaitingForMessage() {
        isWaitingForMessage=true;
    }

    /**
     * Notifies that a message has arrived
     */
    public void notifyMessage() {
        messageReceived=true;
    }

    /**
     * Notifies that the server is not currently receiving messages from this client
     */
    public void notifyIdle() {
        isWaitingForMessage=false;
    }
}
