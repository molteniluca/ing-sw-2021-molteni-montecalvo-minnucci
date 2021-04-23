package it.polimi.ingsw.model.board.general;

import it.polimi.ingsw.model.board.personal.FaithTrack;
import java.util.ArrayList;

/**
 * Class implementing the pattern observer to notify all faith tracks when a player reaches a faith zone
 */
public class FaithObserver {
    ArrayList<FaithTrack> tracks;

    public FaithObserver(){
        tracks=new ArrayList<>();
    }

    /**
     * Registers a faith track in the observer
     * @param faithTrack The faith track to be registered
     */
    public void registerTrack(FaithTrack faithTrack){
        tracks.add(faithTrack);
    }

    /**
     * Notify all tracks
     * @param zone The zone triggering the nitification
     */
    public void notify(int zone){
        for(FaithTrack faithTrack : tracks){
            faithTrack.checkRelationship(zone);
        }
    }
}
