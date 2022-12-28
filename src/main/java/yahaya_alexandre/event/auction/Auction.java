package yahaya_alexandre.event.auction;

import java.time.ZonedDateTime;
import yahaya_alexandre.event.participant.Participant;
import yahaya_alexandre.event.participant.ParticipantObject;

import yahaya_alexandre.event.auction.Offer;

/**
 *
 * @author yahayab
 */
public class Auction
{
    private Participant seller;
    
    private ParticipantObject objectToSell;
    
    private Offer offer;
    
    private ZonedDateTime startDateTime;
    private ZonedDateTime endDateTime;
    
    public Auction(Participant seller,ParticipantObject objectToSell,ZonedDateTime startDateTime,ZonedDateTime endDate)
    {
        this.seller = seller;
        this.objectToSell = objectToSell;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.offer = null;
        
        System.out.println(String.join(" ","nouvelle vente crée avec comme propriétaire:",seller.getFname(),"et l'objet:",objectToSell.getName() ) );
    }

    /**
     * Get the value of offer
     *
     * @return the value of offer or null if no offer
     */
    public Offer getOffer()
    {
        return offer;
    }


    /**
     * Get the value of objectToSell
     *
     * @return the value of objectToSell
     */
    public ParticipantObject getObjectToSell()
    {
        return objectToSell;
    }


    /**
     * Get the value of seller
     *
     * @return the value of seller
     */
    public Participant getSeller()
    {
        return seller;
    }

}
