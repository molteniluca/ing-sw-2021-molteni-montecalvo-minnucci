package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.cards.specialAbility.SpecialAbility;
import it.polimi.ingsw.model.resources.Resources;

public class LeaderCard {
    private final Resources resourceRequirements;
    private final DevelopmentCard developmentRequirements;
    private final int levelRequirements;
    private final SpecialAbility specialAbility;

    public LeaderCard(Resources resourceRequirements, DevelopmentCard developmentRequirements, int levelRequirements, SpecialAbility specialAbility) {
        this.resourceRequirements = resourceRequirements;
        this.developmentRequirements = developmentRequirements;
        this.levelRequirements = levelRequirements;
        this.specialAbility = specialAbility;
    }

    public Resources getResourceRequirements() {
        return resourceRequirements;
    }

    public DevelopmentCard getDevelopmentRequirements() {
        return developmentRequirements;
    }

    public int getLevelRequirements() {
        return levelRequirements;
    }

    public SpecialAbility getSpecialAbility() {
        return specialAbility;
    }
}
