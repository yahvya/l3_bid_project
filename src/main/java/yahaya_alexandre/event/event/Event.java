package yahaya_alexandre.event.event;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import yahaya_alexandre.event.auction.Auction;
import yahaya_alexandre.event.participant.Participant;
import java.util.ArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import yahaya_alexandre.event.frame.EventViewer;
import yahaya_alexandre.event.frame.EventViewer.MessageType;
import yahaya_alexandre.event.security.SecurityManager;

/**
 *
 * @author yahayab
 */
public class Event implements Serializable
{
    public static final int MINIMUM_PARTICIPANTS = 6;
    
    private String eventName;
    
    private ArrayList<Participant> participants;
    
    private ArrayList<Auction> auctions;
    
    public Event(String eventName,ArrayList<Participant> participants,ArrayList<Auction> auctions)
    {
        this.eventName = eventName;
        this.participants = participants;
        this.auctions = auctions;
    }
    
    /**
     * @see start the event and print messages in the manager interface
     */
    public void startEvent(EventViewer manager)
    {
        ScheduledThreadPoolExecutor scheduler = new ScheduledThreadPoolExecutor(this.auctions.size() );
        
        ZonedDateTime now = ZonedDateTime.now(); 
        
        manager.printMessage("Lancement de l'évenement", MessageType.STATE);
        
        for(Auction auction : this.auctions)
        {
            try
            {
                auction.setPrinterPage(manager);
                auction.setParticipants(participants);
                
                new SecurityManager(auction);

                ZonedDateTime startDateTime = auction.getStartDateTime();

                // config to start the thread after start date or now if already past
                scheduler.schedule(auction,now.isAfter(startDateTime) ? 0 : now.until(startDateTime,ChronoUnit.MILLIS),TimeUnit.MILLISECONDS);
            }
            catch(Exception e)
            {
                manager.printMessage("une erreur interne s'est produite, vente ignoré", MessageType.FAILURE);
            }
        }
    }

    /**
     * Get the value of auctions
     *
     * @return the value of auctions
     */
    public ArrayList<Auction> getAuctions()
    {
        return auctions;
    }


    /**
     * Set the value of participants
     *
     * @param participants new value of participants
     */
    public void setParticipants(ArrayList<Participant> participants)
    {
        this.participants = participants;
    }


    /**
     * Get the value of eventName
     *
     * @return the value of eventName
     */
    public String getEventName()
    {
        return eventName;
    }
}
