package it.polimi.ingsw.model.board.personal.storage;

import it.polimi.ingsw.model.resources.Resources;
import jdk.jshell.spi.ExecutionControl;

public class StrongBox {
    private Resources resources;

    public void addResource(Resources resource)throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("NON IMPLEMENTATO");
    }

    public void removeResource()throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("NON IMPLEMENTATO");
    }

    public Resources getResources()throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("NON IMPLEMENTATO");
    }

    public boolean isAvailableSpace(Resources resource)throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("NON IMPLEMENTATO");
    }
}
