package yahaya_alexandre.event.auction;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import yahaya_alexandre.event.participant.Participant;
import yahaya_alexandre.event.participant.ParticipantObject;
import java.time.temporal.ChronoUnit;
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
        this.offer = null;
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
            
            while(!this.stopThread)
            {
                Thread.sleep(2000);
                
                this.printerPage.printMessage(String.join(" ","message de l'objet",this.sellData),MessageType.SUCCESS);
            }
            
            /*
                dans la boucle 
                on récupère au hazaed une personne qui veut faire une offre
                on notifie chaque participant qui remplis ou non une arraylist avec son offre puis on y ajoute l'offre du participant actuel et ensuite on parcours la arraylist pour accepter ou non l'offre 
                on garde donc la meilleure offre puis on rend la main au run qui va chercher une autre personne
            */
            
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
        this.participants = participants;
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
