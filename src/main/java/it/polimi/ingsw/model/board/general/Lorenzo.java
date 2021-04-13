package it.polimi.ingsw.model.board.general;

import it.polimi.ingsw.model.board.FaithTrack;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Lorenzo {
    private final FaithTrack faithTrack;
    private int positionToken;
    private ArrayList<ActionTokens> actionTokens;

    public Lorenzo() {
        faithTrack = new FaithTrack(new FaithObserver());
        positionToken = 0;
        actionTokens = new ArrayList<>();
        this.actionTokens.addAll(Arrays.asList(ActionTokens.values()));
        this.actionTokens.add(ActionTokens.DOUBLEFAITH);
        Collections.shuffle(this.actionTokens);
    }

    public FaithTrack getFaithTrack() {
        return faithTrack;
    }
}
