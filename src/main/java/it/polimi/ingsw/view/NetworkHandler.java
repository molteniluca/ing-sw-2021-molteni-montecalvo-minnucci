package it.polimi.ingsw.view;

import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.controller.NetworkMessages;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import static it.polimi.ingsw.controller.NetworkMessages.*;


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
                    if (read == HEARTBEAT) {
                        read = null;
                        continue;
                    }
                }
                if(read.getClass() == Game.class) {
                    view.updateObjects((Game) read);
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
