package yahaya_alexandre.event.event;

import java.io.Serializable;

import yahaya_alexandre.event.auction.Auction;

import yahaya_alexandre.event.participant.Participant;

import java.util.ArrayList;

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
     * @see start the event and print messages on terminal
     */
    public void startEvent()
    {
        System.out.println("demarrage d'un evenement dans le terminal");
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
