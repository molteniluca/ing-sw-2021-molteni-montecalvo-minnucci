package it.polimi.ingsw.model.cards.specialAbility;


import java.io.Serializable;

/**
 * SpecialAbility Interface, Discount, ExtraDeposit, ExtraResource and ExtraProduction
 * are all classes attribute to this Interface, connect with LeaderCard abstract class
 */
public interface SpecialAbility extends Serializable {
    long serialVersionUID = 6732146736278436285L;
    default Discount applyDiscount() {return null;}

    default ExtraDeposit applyExtraDeposit() {return null;}

    default ExtraProduction applyExtraProduction() {return null;}

    default ExtraResource applyExtraResource() {return null;}

}