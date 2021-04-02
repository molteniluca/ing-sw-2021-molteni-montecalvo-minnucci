package it.polimi.ingsw.model.board.general;

import it.polimi.ingsw.model.resources.ResourceTypes;
import it.polimi.ingsw.model.resources.Resources;
import jdk.jshell.spi.ExecutionControl.NotImplementedException;

public class Market {
    private ResourceTypes[][] marketMatrix;
    private ResourceTypes externalResource;

    public Market() {
    }

    public Resources buyColumn(int column) throws NotImplementedException {
        throw new NotImplementedException("NON IMPLEMENTATO");
    }

    public Resources buyRow(int row)throws NotImplementedException {
        throw new NotImplementedException("NON IMPLEMENTATO");
    }

    public ResourceTypes getExternalResource() {
        return externalResource;
    }

    private void pushRow(int row)throws NotImplementedException {
        throw new NotImplementedException("NON IMPLEMENTATO");
    }

    private void pushColumn(int column) throws NotImplementedException {
        throw new NotImplementedException("NON IMPLEMENTATO");
    }
}
