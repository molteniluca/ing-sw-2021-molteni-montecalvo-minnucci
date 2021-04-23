package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.cards.specialAbility.SpecialAbility;
import it.polimi.ingsw.model.resources.Resources;

import java.util.ArrayList;
import java.util.List;

/**
 * LeaderCard is a subclass of Card, it has a special effect available only after obtain certain requirements
 */
public class LeaderCard extends Card{
    private Resources resourceRequirements;
    private DevelopmentCardRequirement developmentCardRequirementWithLevel;
    private List<DevelopmentCardRequirement> developmentCardRequirementOnlyColor;
    private boolean isActive;
    private final SpecialAbility specialAbility;

    /**
     * Constructor of LeaderCard
     * @param victoryPoint Victory Point of Card
     * @param resourceRequirements resources required to activate LeaderCard's effect
     * @param specialAbility the ability of LeaderCard: Discount, ExtraDeposit, ExtraResource, ExtraProduction
     */
    public LeaderCard(int victoryPoint, Resources resourceRequirements, SpecialAbility specialAbility) {
        super(victoryPoint);
        this.resourceRequirements = resourceRequirements;
        this.isActive = false;
        this.specialAbility = specialAbility;
    }

    /**
     * Constructor of LeaderCard
     * @param victoryPoint Victory Point of Card
     * @param developmentCardRequirementWithLevel DevelopmentCard with level and color necessary to activate LeaderCard's effect
     * @param specialAbility the ability of LeaderCard: Discount, ExtraDeposit, ExtraResource, ExtraProduction
     */
    public LeaderCard(int victoryPoint, DevelopmentCardRequirement developmentCardRequirementWithLevel, SpecialAbility specialAbility) {
        super(victoryPoint);
        this.developmentCardRequirementWithLevel = developmentCardRequirementWithLevel;
        this.isActive = false;
        this.specialAbility = specialAbility;
    }

    /**
     * Constructor of LeaderCard
     * @param victoryPoint Victory Point of Card
     * @param developmentCardRequirementOnlyColor list of DevelopmentCards with only color required, 1 card equals 1 char
     * @param specialAbility the ability of LeaderCard: Discount, ExtraDeposit, ExtraResource, ExtraProduction
     */
    public LeaderCard(int victoryPoint, List<DevelopmentCardRequirement> developmentCardRequirementOnlyColor, SpecialAbility specialAbility) {
        super(victoryPoint);
        this.developmentCardRequirementOnlyColor = developmentCardRequirementOnlyColor;
        this.isActive = false;
        this.specialAbility = specialAbility;
    }

    /**
     * Getter of SpecialAbility
     * @return the specialAbility: Discount, ExtraDeposit, ExtraProduction, ExtraResource
     */
    public SpecialAbility getSpecialAbility() {
        return specialAbility;
    }

    /**
     * Getter of effect activation
     * @return true if LeaderCard's effect is active, else false
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * Setter of effect activation
     * @param active boolean activator of LeaderCard's effect
     */
    public void setActive(boolean active) {
        isActive = active;
    }

    /**
     * Getter of resourceRequirements
     * @return resource required to use LeaderCard
     */
    public Resources getResourceRequirements() {
        return resourceRequirements;
    }

    /**
     * Getter of DevelopmentCardRequirementWithLevel
     * @return DevelopmentCardRequirementWithLevel (level and color) needed to activate LeaderCard's effect
     */
    public DevelopmentCardRequirement getDevelopmentCardRequirementWithLevel() {
        return developmentCardRequirementWithLevel;
    }

    /**
     * Getter of DevelopmentCardRequirementOnlyColor
     * @return the list of DevelopmentCardRequirements (list of colors = cards) needed to activate LeaderCard's effect
     */
    public List<DevelopmentCardRequirement> getDevelopmentCardRequirementOnlyColor() {
        return developmentCardRequirementOnlyColor;
    }

    /**
     * Check the requirements to activate LeaderCard's effect, it requires Resources or DevelopmentCards
     * @param resources resources available for player
     * @param cards DevelopmentCards available for player
     * @return true or false, true if that LeaderCard has the correct parameters to be active
     */
    public boolean checkRequirements(Resources resources, List<DevelopmentCard> cards) {

        //Check which requirement is needed: resources or developmentCards;
        //then: check if LeaderCards has its requirements

        if(getResourceRequirements()!=null) return resources.isSubPositive(resourceRequirements);

        else if(getDevelopmentCardRequirementWithLevel()!=null) { //if it has DevelopmentCard requirements (level and color) instead of resources requirements
            for (DevelopmentCard card: cards) {
                if(card.getType()==getDevelopmentCardRequirementWithLevel().getType())
                    if(card.getLevel()==getDevelopmentCardRequirementWithLevel().getLevel())
                        return true;
            }
        }
        else if(getDevelopmentCardRequirementOnlyColor()!=null){
                if((cards.stream().filter(x -> (x.getType()=='b')).count())>=developmentCardRequirementOnlyColor.stream().filter(x -> x.getType()=='b').count()){
                    if((cards.stream().filter(x -> (x.getType()=='g')).count())>=developmentCardRequirementOnlyColor.stream().filter(x -> x.getType()=='g').count()){
                        if((cards.stream().filter(x -> (x.getType()=='p')).count())>=developmentCardRequirementOnlyColor.stream().filter(x -> x.getType()=='p').count()){
                            if((cards.stream().filter(x -> (x.getType()=='y')).count())>=developmentCardRequirementOnlyColor.stream().filter(x -> x.getType()=='y').count()){
                                return true;
                            }
                        }
                    }
                }
        }
        return false;
    }

}
