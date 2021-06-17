package it.polimi.ingsw.network;

import java.io.Serializable;

public class ObjectUpdate implements Serializable {
    private static final long serialVersionUID = 6732146736278436200L;
    private final Object object;
    private final int player;
    private final UpdateTypes updateType;

    public ObjectUpdate(Object object, UpdateTypes type ,int player) {
        this.object = object;
        this.player = player;
        this.updateType = type;
    }

    public int getPlayer() {
        return player;
    }

    public Object getObject() {
        return object;
    }

    public UpdateTypes getUpdateType() {
        return updateType;
    }
}
