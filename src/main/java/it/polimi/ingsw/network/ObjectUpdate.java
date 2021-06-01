package it.polimi.ingsw.network;

public class ObjectUpdate {
    private static final long serialVersionUID = 6732146736278436200L;
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
