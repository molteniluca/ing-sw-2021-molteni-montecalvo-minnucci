package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.board.FaithTrack;
import it.polimi.ingsw.model.board.general.GeneralBoard;
import it.polimi.ingsw.model.board.personal.CardBoard;
import it.polimi.ingsw.model.board.personal.Deposit;
import it.polimi.ingsw.model.board.personal.LeaderBoard;
import it.polimi.ingsw.model.cards.DevelopmentCard;
import it.polimi.ingsw.model.resources.ResourceTypes;
import jdk.jshell.spi.ExecutionControl;

public class PersonalBoard {
    private FaithTrack faithTrack;
    private Deposit deposit;
    private CardBoard cardBoard;
    private LeaderBoard leaderBoard;
    private GeneralBoard generalBoard;

    public PersonalBoard(GeneralBoard generalBoard) {
        this.faithTrack = new FaithTrack();
        this.deposit = new Deposit();
        this.cardBoard = new CardBoard();
        this.leaderBoard = new LeaderBoard();
        this.generalBoard = generalBoard;
    }

    public FaithTrack getFaithTrack() {
        return faithTrack;
    }

    public Deposit getDeposit() {
        return deposit;
    }

    public CardBoard getCardBoard() {
        return cardBoard;
    }

    public GeneralBoard getGeneralBoard() {
        return generalBoard;
    }

    public LeaderBoard getLeaderBoard() {
        return leaderBoard;
    }

    public void produce(DevelopmentCard card)throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("NON IMPLEMENTATO");
    }

    public boolean checkProduce(DevelopmentCard card)throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("NON IMPLEMENTATO");
    }

    public void produce(ResourceTypes resource1, ResourceTypes resource2, ResourceTypes output)throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("NON IMPLEMENTATO");
    }

    public void buyColumn(int column)throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("NON IMPLEMENTATO");
    }

    public void buyRow(int row)throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("NON IMPLEMENTATO");
    }

    public void drawCard(int row, int column)throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("NON IMPLEMENTATO");
    }
}
