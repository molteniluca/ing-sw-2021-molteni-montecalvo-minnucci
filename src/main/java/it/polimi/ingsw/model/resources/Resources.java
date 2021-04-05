package it.polimi.ingsw.model.resources;

import java.util.HashMap;
import it.polimi.ingsw.model.exceptions.NegativeResourceValueException;
import it.polimi.ingsw.model.exceptions.DuplicateResourceException;

/**
 * Class that represents a quantity of resources.
 */
public class Resources {
    private final HashMap<ResourceTypes,Integer> resourceMap;

    public Resources() {
        this.resourceMap = new HashMap<>();
    }

    /**
     * Sets the value of a specified resource in the object
     * @param resource the type of the resource to be set
     * @param number the value of the resource to be set
     * @throws DuplicateResourceException in case the resource has already been set.
     */
    public void set(ResourceTypes resource, int number) throws DuplicateResourceException {
        Integer oldNumber;
        oldNumber=resourceMap.putIfAbsent(resource,number);

        if(oldNumber!=null)
            throw new DuplicateResourceException("Trying to set an already defined resource");
    }

    /**
     * Adds the current object to the one passed as argument
     * @param operand the operand to be added to the object
     * @return the sum of the two resources
     */
    public Resources add(Resources operand){
        Resources sum=new Resources();
        operand.resourceMap.forEach(sum.resourceMap::put); /*duplicate the current object*/
        this.resourceMap.forEach((key,value) -> sum.resourceMap.merge(key,value, Integer::sum)); /*sum the operand*/
        return sum;
    }

    /**
     * Subtracts to the object the operand passed as argument.
     * @param operand the second operand of the subtraction
     * @return the difference between the two operands
     * @throws NegativeResourceValueException in case of a negative resource value.
     */
    public Resources sub(Resources operand) throws NegativeResourceValueException {
        Resources sum=new Resources();
        this.resourceMap.forEach(sum.resourceMap::put); /*duplicate the current object*/
        operand.resourceMap.forEach((key,value)-> sum.resourceMap.merge(key,value, (v1,v2)->v1-v2));/*subtract the operand*/

        if(sum.resourceMap.entrySet().stream().anyMatch(e -> e.getValue() < 0)){
            throw new NegativeResourceValueException("Negative resource value in subtraction");
        }
        return sum;
    }
}
