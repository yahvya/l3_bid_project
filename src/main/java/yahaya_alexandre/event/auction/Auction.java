package yahaya_alexandre.event.auction;

import java.time.ZonedDateTime;
import yahaya_alexandre.event.participant.Participant;
import yahaya_alexandre.event.participant.ParticipantObject;

import yahaya_alexandre.event.auction.Offer;
import yahaya_alexandre.event.frame.EventViewer;
import yahaya_alexandre.event.frame.EventViewer.MessageType;

/**
 *
 * @author yahayab
 */
public class Auction implements Runnable
{
    private Participant seller;
    
    private ParticipantObject objectToSell;
    
    private Offer offer;
    
    private ZonedDateTime startDateTime;
    private ZonedDateTime endDateTime;
    
    private EventViewer printerPage;
    
    public Auction(Participant seller,ParticipantObject objectToSell,ZonedDateTime startDateTime,ZonedDateTime endDate)
    {
        this.seller = seller;
        this.objectToSell = objectToSell;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.offer = null;
        
        System.out.println(String.join(" ","nouvelle vente crée avec comme propriétaire:",seller.getFname(),"et l'objet:",objectToSell.getName() ) );
    }
    
    @Override
    public void run()
    {
        try
        {
            this.printerPage.printMessage(
                String.join(" ",
                "début de la vente de la vente de l'objet <<",
                Integer.toString(this.objectToSell.getObjectId() ),
                this.objectToSell.getName(),
                ">> avec un prix de départ à",
                Double.toString(this.objectToSell.getPrice() ),
                " appatenant à",
                this.seller.getName(),
                this.seller.getFname(),
                ">>"
                ),
                MessageType.STATE
            );
            this.printerPage.setAuctionIsStarted(this);
            
            while(true)
            {
                this.printerPage.printMessage("nouveau message",MessageType.FAILURE);
                
                Thread.sleep(2000);
            }
            
//            this.printerPage.printMessage("fin d'une vente".concat(this.objectToSell.getName() ), MessageType.STATE);
        }
        catch(Exception e){}
    }
    
    /**
     * set the value of printerPage
     * @param printerPage 
     */
    public void setPrinterPage(EventViewer printerPage)
    {
        this.printerPage = printerPage;
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
    
    /**
     * get the value of startDateTime
     * @return 
     */
    public ZonedDateTime getStartDateTime()
    {
        return this.startDateTime;
    }
    
    /**
     * get the value of endDateTime
     * @return 
     */
    public ZonedDateTime getEndDateTime()
    {
        return this.endDateTime;
    }

}
