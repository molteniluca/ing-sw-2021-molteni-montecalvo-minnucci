package it.polimi.ingsw.model.resources;

import java.util.HashMap;
import it.polimi.ingsw.model.exceptions.NegativeResourceValueException;

/**
 * Class that represents a quantity of resources.
 */
public class Resources {
    private final HashMap<ResourceTypes,Integer> resourceMap;

    /**
     * Constructor of the class
     */
    public Resources() {
        this.resourceMap = new HashMap<>();
        for (ResourceTypes r: ResourceTypes.values()) {
            this.resourceMap.put(r,0);
        }
    }

    /**
     * Sets the value of a specified resource in the object
     * @param resource the type of the resource to be set
     * @param number the value of the resource to be set
     */
    public void set(ResourceTypes resource, int number){
        resourceMap.put(resource,number);
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
        Resources diff=new Resources();

        this.resourceMap.forEach(diff.resourceMap::put); /*duplicate the current object*/
        operand.resourceMap.forEach((key,value)-> diff.resourceMap.merge(key,value, (v1,v2)->v1-v2));/*subtract the operand*/

        diff.eraseBlank();/*sets zero to the blank resource*/
        if(diff.resourceMap.entrySet().stream().anyMatch(e -> e.getValue() < 0)){
            throw new NegativeResourceValueException("Negative resource value in subtraction");
        }
        return diff;
    }

    /**
     * Returns the as positive the negative resources in a subtraction
     * @param operand operand of the subrtaction
     * @return Returns the as positive the negative resources in a subtraction
     */
    public Resources negativeSubValues(Resources operand){
        Resources diff=new Resources();

        this.resourceMap.forEach(diff.resourceMap::put); /*duplicate the current object*/
        operand.resourceMap.forEach((key,value)-> diff.resourceMap.merge(key,value, (v1,v2)->v1-v2));/*subtract the operand*/

        for(ResourceTypes type : ResourceTypes.values()){
            if(diff.resourceMap.get(type)>0){
                diff.set(type,0);
            }
            else{
                diff.set(type,-diff.resourceMap.get(type));
            }
        }
        return diff;
    }

    /**
     * This method switches blank resources in a specified type resource
     * @param type Type of the resource in which blank need to be converted
     * @return The new resource quantity
     */
    public Resources switchBlank(ResourceTypes type){
        Resources substitute=new Resources();

        this.resourceMap.forEach(substitute.resourceMap::put); /*duplicate the current object*/
        substitute.resourceMap.put(type,this.resourceMap.get(type)+this.resourceMap.get(ResourceTypes.BLANK));

        substitute.eraseBlank();
        return substitute;
    }

    /**
     * Check if every resource of the subtraction is greater than zero
     * @param operand The resource to subtract
     * @return True if every resource of the subtraction is greater than zero and false otherwiwse
     */
    public boolean isSubPositive(Resources operand){
        try{
            this.sub(operand);
            return true;
        }
        catch (NegativeResourceValueException e){
            return false;
        }
    }

    /**
     * Returns the number of resources of a specified type
     * @param type Type of the resource to be returned
     * @return The number of resources
     */
    public int getResourceNumber(ResourceTypes type){
        return this.resourceMap.get(type);
    }

    /**
     * A method that sets the blank resource value to zero
     */
    private void eraseBlank(){
        set(ResourceTypes.BLANK,0);
    }
}
