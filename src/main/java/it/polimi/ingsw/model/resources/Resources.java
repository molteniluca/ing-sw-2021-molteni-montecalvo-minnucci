package it.polimi.ingsw.model.resources;

import java.util.HashMap;
import it.polimi.ingsw.model.exceptions.NegativeResourceValueException;
import it.polimi.ingsw.model.exceptions.DuplicateResourceException;
import jdk.jshell.spi.ExecutionControl.NotImplementedException;

public class Resources {
    private HashMap<ResourceTypes,Integer> resourceMap;

    public Resources() {
        this.resourceMap = new HashMap<>();
    }

    public void set(ResourceTypes resource, int number) throws DuplicateResourceException, NotImplementedException {
        throw new NotImplementedException("NON IMPLEMENTATO");
    }

    public Resources add(Resources operand)throws NotImplementedException {
        throw new NotImplementedException("NON IMPLEMENTATO");
    }

    public Resources sub(Resources operand) throws NegativeResourceValueException , NotImplementedException {
        throw new NotImplementedException("NON IMPLEMENTATO");
    }
}
