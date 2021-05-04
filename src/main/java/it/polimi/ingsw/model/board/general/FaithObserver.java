package it.polimi.ingsw.model.board.general;

import it.polimi.ingsw.model.board.personal.FaithTrack;
import it.polimi.ingsw.model.exceptions.FaithOverflowException;
import it.polimi.ingsw.model.resources.Resources;

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
     * @param zone The zone triggering the notification
     */
    public void notify(int zone){
        for(FaithTrack faithTrack : tracks){
            faithTrack.checkRelationship(zone);
        }
    }

    /**
     * This method handles the drop of resources adding a bonus to the others faith tracks
     * @param res The resources to be dropped
     * @param callingFaithTrack The faith track calling the method
     * @throws FaithOverflowException In case a player gets out of bound on the faith track
     */
    public void dropResources(Resources res, FaithTrack callingFaithTrack) throws FaithOverflowException {
        for (FaithTrack ft : tracks) {
            if (ft != callingFaithTrack) {
                ft.incrementPosition(res.getTotalResourceNumber());
            }
        }
    }
}
