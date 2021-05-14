package it.polimi.ingsw.model.board.general;

import it.polimi.ingsw.model.resources.ResourceTypes;
import it.polimi.ingsw.model.resources.Resources;

import java.util.*;


public class Market {
    /// first element of a row is [x][0], first element of a column is [0][x]
    private final ResourceTypes[][] marketMatrix;
    private final int ROWS = 3;
    private final int COLUMNS = 4;
    private ResourceTypes externalResource;

    /**
     * Creates the concrete object, especially marketMatrix, filing it in casual order
     * with a pool of ResourceTypes : 2 Gold, 2 stones, 2 shields, 2 Servants, 1 faith and 4 blank ; one of them is assigned to externalResource
     */
    public Market() {

        int resourcesCursor = 0; //the cursor used to iterate availableResources
        int i, j; //matrix indexes

        //create and then shuffle an arraylist of available resources in the market
        ArrayList<ResourceTypes> availableResources = new ArrayList<>();

        availableResources.add(ResourceTypes.GOLD);
        availableResources.add(ResourceTypes.GOLD);
        availableResources.add(ResourceTypes.SERVANT);
        availableResources.add(ResourceTypes.SERVANT);
        availableResources.add(ResourceTypes.SHIELD);
        availableResources.add(ResourceTypes.SHIELD);
        availableResources.add(ResourceTypes.STONE);
        availableResources.add(ResourceTypes.STONE);
        availableResources.add(ResourceTypes.FAITH);

        for (i = 0; i < 4; i++) {
            availableResources.add(ResourceTypes.BLANK);
        }

        Collections.shuffle(availableResources);

        marketMatrix = new ResourceTypes[ROWS][COLUMNS];

        //fills the matrix
        for (i = 0; i < ROWS; i++) {
            for (j = 0; j < COLUMNS; j++) {
                marketMatrix[i][j] = availableResources.get(resourcesCursor);
                resourcesCursor++;
            }
        }
        externalResource = availableResources.get(resourcesCursor);
    }


    /**
     * It's a TEST METHOD because i don't know how i can access the matrix from test because
     * it creates marketMatrix filling it in casual order
     * @return the matrix representing the market
     */
    public ResourceTypes[][] getMarketMatrix()
    {
        return marketMatrix;
    }


    /**
     * It's a TEST METHOD
     * @param externalResource the resource assigned to externalResource
     */
    public void setExternalResource(ResourceTypes externalResource)
    {
        this.externalResource = externalResource;
    }



    /**
     * @return the only one external resource in the market
     */
    public ResourceTypes getExternalResource() {
        return externalResource;
    }




    /**
     * It's the counterpart of buyRow
     * @param column the column the user wants to buy
     * @return Map of the resources found in the chosen column
     * @throws IndexOutOfBoundsException in case the provided column is out of bounds
     */
    public Resources buyColumn(int column) throws IndexOutOfBoundsException {

        /* adding element to a Resources map requires a Resources element
        * so currentElement is like a cursor for the elements of marketMatrix
        * the ResourceTypes in [i][column] is assigned to it and than it is added to the final result
        */
        Resources chosenColumn = new Resources();
        Resources currentElement;
        int i;

        for (i = 0; i < ROWS; i++) {
            currentElement = new Resources();
            currentElement.set(marketMatrix[i][column], 1);
            chosenColumn = chosenColumn.add(currentElement);
        }

        pushColumn(column);
        return chosenColumn;
    }


    /**
     * @param row the row the user wants to buy
     * @return Map of the resources found in the chosen row
     * @throws IndexOutOfBoundsException in case the provided row is out of bounds
     */
    public Resources buyRow(int row) throws IndexOutOfBoundsException {

        Resources chosenRow = new Resources();
        Resources currentElement;
        int j;

        for (j = 0; j < COLUMNS; j++) {
            currentElement = new Resources();
            currentElement.set(marketMatrix[row][j], 1);
            chosenRow = chosenRow.add(currentElement);
        }

        pushRow(row);
        return chosenRow;
    }



    /**
     * Pushes left the chosen row, the first element of the row becomes the externalResource
     * while the previous external resource becomes row's last element
     *
     * @param row the row the users bought
     * @throws IndexOutOfBoundsException in case the provided row is out of bounds
     */
    private void pushRow(int row) throws IndexOutOfBoundsException {

        ResourceTypes tmp = externalResource; //temporary variable used for swapping
        externalResource = marketMatrix[row][0]; //set the external resource with the first element of the row

        for (int i = 0; i < COLUMNS-1; i++)
            marketMatrix[row][i] = marketMatrix[row][i + 1];

        marketMatrix[row][COLUMNS - 1] = tmp; //set the last element of the row with the previous value of externalResource
    }

    /**
     * Pushes up the chosen column, the first element of the column becomes the externalResource
     * while the previous external resource becomes column's last element
     *
     * @param column the column the user bought
     * @throws IndexOutOfBoundsException in case the given column is out of bounds
     */
    private void pushColumn(int column) throws IndexOutOfBoundsException {

        ResourceTypes tmp = externalResource;
        externalResource = marketMatrix[0][column];

        for (int i = 0; i < ROWS-1; i++)
            marketMatrix[i][column] = marketMatrix[i + 1][column];

        marketMatrix[ROWS - 1][column] = tmp;
    }
}
