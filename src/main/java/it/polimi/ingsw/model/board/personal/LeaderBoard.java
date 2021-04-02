package it.polimi.ingsw.model.board.personal;

import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.model.cards.specialAbility.Discount;
import it.polimi.ingsw.model.cards.specialAbility.ExtraDeposit;
import it.polimi.ingsw.model.cards.specialAbility.ExtraProduction;
import it.polimi.ingsw.model.cards.specialAbility.ExtraResource;
import jdk.jshell.spi.ExecutionControl;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class LeaderBoard {
    private ArrayList<LeaderCard> leaderCards;

    public LeaderBoard() {
        this.leaderCards = new ArrayList<>();
    }

    public void playLeader(LeaderCard leader)throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("NON IMPLEMENTATO");
    }

    public void discardLeader (LeaderCard leader)throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("NON IMPLEMENTATO");
    }

    public ArrayList<ExtraProduction> getProductionEffects() throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("NON IMPLEMENTATO");
    }

    public ArrayList<Discount> getDiscountEffects()throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("NON IMPLEMENTATO");
    }

    public ArrayList<ExtraDeposit> getExtraDepostEffects()throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("NON IMPLEMENTATO");
    }

    public ArrayList<ExtraResource> getExtraResource()throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("NON IMPLEMENTATO");
    }

    public void activateLeaderEffect(LeaderCard leader)throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("NON IMPLEMENTATO");
    }
}
