package it.polimi.ingsw.view;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class HeartBeatThreadClient extends TimerTask {
    private static final long interval = 10000;
    private final NetworkHandler networkHandler;

    public HeartBeatThreadClient(NetworkHandler c){
        networkHandler=c;
        Timer t = new Timer();
        t.schedule(this,interval,interval);
    }

    @Override
    public void run() {
        try {
            networkHandler.sendHeartBeat();
        } catch (IOException e) {
            networkHandler.closeConnection();
            this.cancel();
        }
    }
}
