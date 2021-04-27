package it.polimi.ingsw.model.resources;

import it.polimi.ingsw.model.exceptions.NegativeResourceValueException;
import junit.framework.TestCase;

public class ResourcesTest extends TestCase {

    public void testAdd() {
        Resources resources= new Resources();
        resources.set(ResourceTypes.BLANK,8);
        resources.set(ResourceTypes.STONE,5);
        resources.set(ResourceTypes.GOLD,5);

        Resources resources2= new Resources();
        resources2.set(ResourceTypes.BLANK,90);
        resources2.set(ResourceTypes.STONE,5);
        resources2.set(ResourceTypes.GOLD,5);

        resources=resources.add(resources2);

        assert resources.getResourceNumber(ResourceTypes.STONE)==10;
        assert resources.getResourceNumber(ResourceTypes.GOLD)==10;
        assert resources.getResourceNumber(ResourceTypes.BLANK)==98;
    }

    public void testSub() {
        Resources resources= new Resources();
        resources.set(ResourceTypes.BLANK,8);
        resources.set(ResourceTypes.STONE,5);
        resources.set(ResourceTypes.GOLD,5);

        Resources resources2= new Resources();
        resources2.set(ResourceTypes.BLANK,90);
        resources2.set(ResourceTypes.STONE,5);
        resources2.set(ResourceTypes.GOLD,5);

        Resources diff = null;
        try {
            diff = resources.sub(resources2);
        } catch (NegativeResourceValueException e) {
            assert false;
        }

        assert diff.getResourceNumber(ResourceTypes.GOLD) ==0;
        assert diff.getResourceNumber(ResourceTypes.STONE) == 0;

        boolean test=false;
        try {
            diff.sub(resources2);
        } catch (NegativeResourceValueException e) {
            test=true;
        }

        assert test;
    }

    public void testNegativeSubValues() {
        Resources resources= new Resources();
        resources.set(ResourceTypes.BLANK,8);
        resources.set(ResourceTypes.STONE,5);
        resources.set(ResourceTypes.GOLD,5);

        Resources resources2= new Resources();
        resources2.set(ResourceTypes.BLANK,90);
        resources2.set(ResourceTypes.STONE,3);
        resources2.set(ResourceTypes.GOLD,9);

        resources = resources.negativeSubValues(resources2);

        assert resources.getResourceNumber(ResourceTypes.GOLD)==4;
        assert resources.getResourceNumber(ResourceTypes.STONE)==0;
    }

    public void testSwitchBlank() {
        Resources resources= new Resources();
        resources.set(ResourceTypes.BLANK,8);
        resources.set(ResourceTypes.STONE,5);
        resources.set(ResourceTypes.GOLD,5);

        resources=resources.switchBlank(ResourceTypes.GOLD);

        assert resources.getResourceNumber(ResourceTypes.GOLD) == 13;
        assert resources.getResourceNumber(ResourceTypes.STONE) == 5;
    }

    public void testEquals(){
        Resources resources= new Resources();
        resources.set(ResourceTypes.BLANK,8);
        resources.set(ResourceTypes.STONE,5);
        resources.set(ResourceTypes.GOLD,5);

        Resources resources2= new Resources();
        resources2.set(ResourceTypes.BLANK,8);
        resources2.set(ResourceTypes.STONE,5);
        resources2.set(ResourceTypes.GOLD,5);

        assert resources.equals(resources2);
        assert resources.equals(resources);

        assert !resources.equals(resources.add(resources2));

    }
}