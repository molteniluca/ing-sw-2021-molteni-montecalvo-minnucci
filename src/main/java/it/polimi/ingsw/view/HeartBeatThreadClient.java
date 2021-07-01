package it.polimi.ingsw.view;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Class that represents a module that checks weather the connection is still alive
 */
public class HeartBeatThreadClient extends TimerTask {
    private static final long interval = 10000;
    private static final long threshold = 2;
    private final NetworkHandler networkHandler;
    private boolean messageReceived;
    private int countFromDisconnect;

    /**
     * Constructor of the class
     * @param c The network handler to be monitored
     */
    public HeartBeatThreadClient(NetworkHandler c){
        networkHandler=c;
        countFromDisconnect=0;
        messageReceived=true;
        Timer t = new Timer();
        t.schedule(this,interval,interval);
    }

    @Override
    public void run() {
        try {
            if(!messageReceived) {
                countFromDisconnect++;
                if(countFromDisconnect >= threshold){
                    networkHandler.closeConnection();
                    this.cancel();
                }
            }
            else
                countFromDisconnect=0;
            messageReceived=false;
            networkHandler.sendHeartBeat();
        } catch (IOException e) {
            networkHandler.closeConnection();
            this.cancel();
        }
    }

    /**
     * Sends a notification to this object that a message has been received
     */
    public void messageReceived() {
        messageReceived=true;
    }
}
