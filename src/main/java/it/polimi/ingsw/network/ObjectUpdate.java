package it.polimi.ingsw.network;

import java.io.Serializable;

/**
 * Class that represents partial object update
 */
public class ObjectUpdate implements Serializable {
    private static final long serialVersionUID = 6732146736278436200L;
    private final Object object;
    private final int player;
    private final UpdateTypes updateType;

    /**
     * Constructor of the class
     * @param object The object to be updated
     * @param type The type of update
     * @param player The player involved with the update
     */
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
