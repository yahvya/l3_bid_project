package yahaya_alexandre.event.auction;

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
    
    public Auction(Participant seller,ParticipantObject objectToSell)
    {
        this.seller = seller;
        this.objectToSell = objectToSell;
        this.offer = null;
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
