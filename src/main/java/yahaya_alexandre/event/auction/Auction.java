package yahaya_alexandre.event.auction;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import yahaya_alexandre.event.participant.Participant;
import yahaya_alexandre.event.participant.ParticipantObject;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.util.concurrent.TimeUnit;
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
    
    private boolean stopThread;
    
    private String sellData;
    
    private ArrayList<Participant> participants;
    
    public Auction(Participant seller,ParticipantObject objectToSell,ZonedDateTime startDateTime,ZonedDateTime endDateTime)
    {
        this.seller = seller;
        this.objectToSell = objectToSell;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.stopThread = false;
        this.offer = new Offer(seller,objectToSell.getPrice() );
        this.sellData = String.join(" ",
    "<<",
    Integer.toString(this.objectToSell.getObjectId() ),
    this.objectToSell.getName(),
    ">> avec un prix de départ à",
    Double.toString(this.objectToSell.getPrice() ),
    " appatenant à",
    this.seller.getName(),
    this.seller.getFname(),
    ">>"
        );
    }
    
    /**
     * try to make an offer in this auction
     * @param offer 
     */
    public void makeOffer(Offer offer)
    {
        this.printerPage.printMessage(String.join(" ","réception d'une nouvelle offre (notification des participants) pour l'objet",this.sellData),MessageType.NORMAL);
        
        ArrayList<Offer> offerSession = new ArrayList<Offer>();
        
        offerSession.add(offer);
        
        // notifier gens qui ont déjà fait une offre pour qu'ils remplissent la arraylist
        
        // sort the session to have to have the greater offer from the start
        offerSession.sort(Comparator.comparing(Offer::getPrice) );
        Collections.reverse(offerSession);
        
        Offer greaterOffer = offerSession.remove(0);
        
        if(greaterOffer.offerIsBetter(this.offer) )
        {
            // accept the offer
            this.offer = greaterOffer;
            
            Participant probableBuyer =  this.offer.getBuyer();
            
            this.printerPage.printMessage(
                String.join(" ",
                    "offre de",
                    Double.toString(offer.getPrice() ),
                    "€ fait par",
                    probableBuyer.getName(),
                    probableBuyer.getFname(),
                    "accepté sur l'objet",
                    this.sellData
                ),
            MessageType.SUCCESS
            );
        }
        else
        {
            // refused offers
            offerSession.add(greaterOffer);
            
            for(Offer refusedOffer : offerSession)
            {
                Participant probableBuyer = refusedOffer.getBuyer();
                
                printerPage.printMessage(
                    String.join(" ",
                        "offre de",
                        Double.toString(offer.getPrice() ),
                        "€ fait par",
                        probableBuyer.getName(),
                        probableBuyer.getFname(),
                        "refusé sur l'objet",
                        sellData
                    ),
                    MessageType.FAILURE
                );
            }
        }
    }
    
    @Override
    public void run()
    {
        try
        {   
            ScheduledThreadPoolExecutor scheduler = new ScheduledThreadPoolExecutor(1);
            
            ZonedDateTime now = ZonedDateTime.now();
            
            // configure the scheduler to stop the thread
            scheduler.schedule(new Runnable(){
                @Override
                public void run()
                {
                    stopThread = true;
                }
            },now.isAfter(this.endDateTime) ? 0 : now.until(this.endDateTime,ChronoUnit.MILLIS),TimeUnit.MILLISECONDS);
            
            this.printerPage.printMessage(String.join(" ","début de la vente de la vente de l'objet",this.sellData),MessageType.STATE);
            this.printerPage.setAuctionIsStarted(this);
            
            Random random = new Random();
            
            int participantsIndex = this.participants.size() -1;
            
            while(!this.stopThread)
            {
                // get a random participant to make an offer
                Participant probableBuyer = this.participants.get(random.nextInt(0,participantsIndex) );
                
                // get a random offer from this participant
                
                this.makeOffer(Offer.simulateFrom(probableBuyer,random) );
                
                Thread.sleep(random.nextInt(1500,3000) );
            }
            
            this.printerPage.printMessage(String.join(" ","fin de la vente de la vente de l'objet",this.sellData),MessageType.STATE);
        }
        catch(Exception e){this.printerPage.printMessage("fin erreur ici",MessageType.STATE);}
    }
    
    /**
     * set the value of stopThread
     * @param stopThread
     */
    public void setStopThread(boolean stopThread)
    {
        this.stopThread = stopThread;
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
     * set the value of participants
     * @param participants 
     */
    public void setParticipants(ArrayList<Participant> participants)
    {
        ArrayList<Participant> subParticipants = (ArrayList<Participant>) participants.clone();
        
        subParticipants.remove(this.seller);
        
        this.participants = subParticipants;
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
     * @return startDateTime
     */
    public ZonedDateTime getStartDateTime()
    {
        return this.startDateTime;
    }
    
    /**
     * get the value of endDateTime
     * @return endDateTime
     */
    public ZonedDateTime getEndDateTime()
    {
        return this.endDateTime;
    }
    
    /**
     * get the values of participants
     * @return participants
     */
    public ArrayList<Participant> getParticipants()
    {
        return this.participants;
    }

}
