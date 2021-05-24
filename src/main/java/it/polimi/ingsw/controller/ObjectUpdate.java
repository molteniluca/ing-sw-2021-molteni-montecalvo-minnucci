package it.polimi.ingsw.controller;

public class ObjectUpdate {
    private Object object;
    private int player;

    public ObjectUpdate(Object object, int player) {
        this.object = object;
        this.player = player;
    }

    public int getPlayer() {
        return player;
    }

    public Object getObject() {
        return object;
    }
}
