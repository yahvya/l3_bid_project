package yahaya_alexandre.event.auction;

import java.util.ArrayList;
import yahaya_alexandre.event.frame.EventViewer;
import yahaya_alexandre.event.participant.Participant;

/**
 *
 * @author yahayab
 */
public abstract class ObservableAuction 
{
    private ArrayList<Participant> observers = new ArrayList<Participant>();
    
    protected String sellData;
    
    protected EventViewer printerPage;
    
    /**
     * add a participant to the observers list
     * @param p 
     */
    public void registerParticipant(Participant p)
    {
        if(!this.observers.contains(p) )
            this.observers.add(p);
    }
    
    /**
     * remove a participant to the observers list
     * @param p 
     */
    public void removeParticipant(Participant p)
    {
        this.observers.remove(p);
    }
    
    /**
     * notify the observers
     * @param offerSession 
     */
    public void notifyParticipants(ArrayList<Offer> offerSession,Participant except)
    {
        this.observers.forEach(participant -> {
            if(participant != except)
                participant.receiveNewOfferNotification(offerSession,sellData,printerPage);
        });
    }
}
