package it.polimi.ingsw.view;

import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.network.NetworkMessages;
import it.polimi.ingsw.network.ObjectUpdate;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import static it.polimi.ingsw.network.NetworkMessages.*;


public class NetworkHandler extends Thread{
    private Socket server;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private final View view;

    public NetworkHandler(String serverAddress, int serverPort, View view) throws IOException {
        this.view=view;
        server = new Socket(serverAddress, serverPort);
        out = new ObjectOutputStream(server.getOutputStream());
        in = new ObjectInputStream(server.getInputStream());
        new HeartBeatThreadClient(this);
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


    private Object receiveObject() throws IOException {
        Object read = null;
        while(read==null){
            try {
                read = in.readObject();
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

    public synchronized void sendObject(Object o) throws IOException {
        out.writeObject(o);
    }

    public void sendHeartBeat() throws IOException {
        sendObject(HEARTBEAT);
    }

    public void closeConnection(){
        try {
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
