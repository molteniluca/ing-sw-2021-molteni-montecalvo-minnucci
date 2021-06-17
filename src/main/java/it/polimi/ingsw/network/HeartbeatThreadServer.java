package it.polimi.ingsw.network;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class HeartbeatThreadServer extends TimerTask {
    private static final long interval = 10000;
    private final ClientHandler clientHandler;

    public HeartbeatThreadServer(ClientHandler c){
        clientHandler=c;
        Timer t = new Timer();
        t.schedule(this,interval,interval);
    }

    @Override
    public void run() {
        if(clientHandler==null){
            this.cancel();
        }else {
            try {
                clientHandler.sendHeartBeat();
            } catch (IOException e) {
                clientHandler.handleDisconnect();
                this.cancel();
            }
        }
    }
}
