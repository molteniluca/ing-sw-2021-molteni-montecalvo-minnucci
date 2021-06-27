package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.view.View;


public class GUIView extends View {
    public static GUIView singleton;

    @Override
    protected boolean isSuccessReceived() {
        return false;
    }

    @Override
    public void updateObjects(Game game) {
        this.game=game;
    }

    @Override
    protected void notifyEndGame(boolean youWon) {

    }

    public static GUIView singleton(){
        if(singleton == null){
            singleton = new GUIView();
        }
        return singleton;
    }
}
