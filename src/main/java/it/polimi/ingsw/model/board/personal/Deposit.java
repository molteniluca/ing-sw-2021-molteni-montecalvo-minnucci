package it.polimi.ingsw.model.board.personal;

import it.polimi.ingsw.model.board.personal.storage.StrongBox;
import it.polimi.ingsw.model.board.personal.storage.WarehouseDepots;
import jdk.jshell.spi.ExecutionControl;

public class Deposit {
    private final WarehouseDepots storage;
    private final StrongBox chest;

    public Deposit() {
        this.storage = new WarehouseDepots();
        this.chest = new StrongBox();
    }

    public WarehouseDepots getStorage() {
        return storage;
    }

    public StrongBox getChest() {
        return chest;
    }

    public void checkRemoveResource()throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("NON IMPLEMENTATO");
    }
}
