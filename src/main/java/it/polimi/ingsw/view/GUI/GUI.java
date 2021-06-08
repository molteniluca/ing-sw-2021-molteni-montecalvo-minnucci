package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.model.board.personal.storage.WarehouseDepots;
import it.polimi.ingsw.view.View;
import javafx.application.Application;

public class GUI extends View {

    @Override
    public void run() {
        Application.launch(GUIApplication.class);
    }

    @Override
    public void initializeView() {

    }

    @Override
    public void welcomeInfo() {

    }

    @Override
    public void askCreateOrJoin() {

    }

    @Override
    public void askServerInfo() {

    }

    @Override
    public void askNickname() {

    }

    @Override
    public void showHomepage() {

    }

    @Override
    public void showFaithTrack() {

    }

    @Override
    public void showWarehouse(WarehouseDepots warehouseDepots) {

    }

    @Override
    public void showStrongbox() {

    }

    @Override
    public void updateObjects(Game game) {

    }

    @Override
    protected void notifyEndGame(boolean youWon) {

    }
}
