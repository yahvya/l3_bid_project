package yahaya_alexandre.event.auction;

import java.util.ArrayList;
import yahaya_alexandre.event.frame.EventViewer;

/**
 *
 * @author yahayab
 */
public interface AuctionObserver
{
    /**
     * receive a notification and have to add his offer if want
     * @param offerSession
     * @param printerPage 
     */
    public void receiveNewOfferNotification(ArrayList<Offer> offerSession,String sellData,EventViewer printerPage);    
}
