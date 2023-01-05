package yahaya_alexandre.event.frame;

import java.util.ArrayList;
import java.util.HashMap;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import yahaya_alexandre.event.auction.Auction;
import yahaya_alexandre.event.event.Event;
import yahaya_alexandre.event.participant.Participant;
import yahaya_alexandre.event.participant.ParticipantObject;

/**
 *
 * @author yahayab
 */
public class EventViewer extends EventPage
{
    private EventLandingPage landingPage;
    
    private ArrayList<String> failedEventsPath;
    private ArrayList<Event> eventsList;
    
    private Event managedEvent; 
    
    private ObservableList<Node> messagesZoneChildren;
    private ObservableList<Node> futureAuctionsRecapChildren;
    private ObservableList<Node> futureEventsRecapChildren;
    
    private HashMap<Auction,Label> linesMap;
    
    /**
     * wait non empty eventsList
     * @param window
     * @param windowBaseTitle
     * @param landingPage
     * @param failedEventsPath
     * @param eventsList 
     */
    public EventViewer(Stage window,String windowBaseTitle,EventLandingPage landingPage,ArrayList<String> failedEventsPath,ArrayList<Event> eventsList)
    {
        super(window,windowBaseTitle,800,750,true);
        
        this.failedEventsPath = failedEventsPath;
        this.eventsList = eventsList;
        
        if(eventsList.size() > 0)
        {
            this.managedEvent = eventsList.get(0);
            
            eventsList.remove(0);
        }
        else this.managedEvent = null;
        
        this.landingPage = landingPage;
        this.linesMap = new HashMap<Auction,Label>();
        this.buildPage();
        this.addStyleSheets();
    }
    
    /**
     * start the managed event
     */
    public void startEvent()
    {   
        if(this.managedEvent != null)
            this.managedEvent.startEvent(this);
    }
    
    /**
     * print the given in the execution zone 
     * @param message
     * @param type 
     */
    public void printMessage(String message,MessageType type)
    {
        // print the message when javafx will can
        Platform.runLater(new Runnable(){
            public void run()
            {
                Label messageContainer = new Label(message);
        
                switch(type)
                {
                    case SUCCESS:
                        messageContainer.getStyleClass().add("success");
                    ; break;

                    case FAILURE:
                        messageContainer.getStyleClass().add("error");
                    ; break;
                    
                    case STATE:
                        messageContainer.getStyleClass().add("state");
                    ; break;

                    case MINER:
                        messageContainer.getStyleClass().add("miner");
                    ; break;

                    case MONEY_TRANSMISSION:
                        messageContainer.getStyleClass().add("money-transmission");
                    ; break;
                    
                    default:;
                }

                messagesZoneChildren.add(0,messageContainer);
                // messagesZoneChildren.add(messageContainer);
            }
        });
    }
    
    /**
     * remove the given auction in the future auctions recap
     * @param startedAuction 
     */
    public void setAuctionIsStarted(Auction startedAuction)
    {
        // remove the line when javafx will can
        Platform.runLater(new Runnable(){
            public void run()
            {
                futureAuctionsRecapChildren.remove(linesMap.get(startedAuction) );
            }
        });
    }
    
    @Override
    protected String getDefaultTitle()
    {
        return "Gestion des évenements";
    }

    @Override
    protected void buildPage()
    {
        VBox globalContainer = new VBox(20);
        
        Label headerText = new Label(String.join(" ","Evenement <<",this.managedEvent == null ? "Non trouvé" : this.managedEvent.getEventName(),">>") );
        
        headerText.getStyleClass().add("h1");
        
        Button goBackHomeButton = new Button("Retour accueil");
        
        HBox header = new HBox(20);
        
        header.getChildren().addAll(headerText,goBackHomeButton);
        
        globalContainer.getChildren().addAll(header,this.createBody() );
        
        ScrollPane scrollableZone = new ScrollPane();
        
        scrollableZone.setContent(globalContainer);
        scrollableZone.setPadding(new Insets(20) );
        
        this.page = new Scene(scrollableZone);
        
        // place events
        goBackHomeButton.setOnAction(new EventHandler<ActionEvent>(){
            public void handle(ActionEvent e)
            {
                landingPage.putPageOnWindow();
            }
        });
    }
    
    /**
     * create the body
     * @return the body container as hbox
     */
    private HBox createBody()
    {
        VBox executionMessagesZone = new VBox(20);
        
        executionMessagesZone.setPadding(new Insets(20) );
        
        this.messagesZoneChildren = executionMessagesZone.getChildren();
        
        ScrollPane scrollableZone = new ScrollPane();
        
        scrollableZone.setContent(executionMessagesZone);
        scrollableZone.setMaxHeight(400);
        
        VBox executionZone = new VBox(20);
        
        executionZone.getChildren().addAll(new Label("Exécution"),scrollableZone);
        
        HBox bodyContainer = new HBox(30);
        
        bodyContainer.getChildren().addAll(this.createInformationsZone(),executionZone);
        
        return bodyContainer;
    }
    
    /**
     * create the informations zone
     * @return information zone as vbox
     */
    private VBox createInformationsZone()
    {
        VBox informationsZone = new VBox(20);
        
        informationsZone.getChildren().addAll(this.createFutureAuctionsZone(),this.createFutureEventsZone(),this.createFailedEventsZone() );
        
        return informationsZone;
    }
    
    /**
     * create the recap zone of future auctions
     * @return recap zone as vbox
     */
    private VBox  createFutureAuctionsZone()
    {
        VBox recapZone = new VBox(15);
        
        this.futureAuctionsRecapChildren = recapZone.getChildren();
        
        if(this.managedEvent != null)
        {
            this.managedEvent.getAuctions().forEach(auction -> {
                Participant seller = auction.getSeller();
                ParticipantObject toSell = auction.getObjectToSell();
                
                try
                {
                    Label sellDataText = new Label(String.join(" ",seller.getName(),seller.getFname(),"vend",toSell.getName(),"(prix de départ : ",Double.toString(toSell.getPrice() ),")") );
                    
                    this.linesMap.put(auction,sellDataText);
                    
                    futureAuctionsRecapChildren.add(sellDataText);
                }
                catch(Exception e){}
            });
        }
        
        ScrollPane scrollableZone = new ScrollPane();
        
        scrollableZone.setContent(recapZone);
        scrollableZone.setPadding(new Insets(10) );
        scrollableZone.setMaxSize(370,250);
        
        VBox futureAuctionsZone = new VBox(20);
        
        futureAuctionsZone.getChildren().addAll(new Label("Ventes à venir"),scrollableZone);
        
        return futureAuctionsZone;
    }
    
     /**
     * create the recap zone of future events
     * @return recap zone as vbox
     */
    private VBox  createFutureEventsZone()
    {
        VBox recapZone = new VBox(15);
        
        this.futureEventsRecapChildren = recapZone.getChildren();
        
        this.eventsList.forEach(event -> futureEventsRecapChildren.add(new Label(event.getEventName() ) ) );
        
        ScrollPane scrollableZone = new ScrollPane();
        
        scrollableZone.setContent(recapZone);
        scrollableZone.setPadding(new Insets(10) );
        scrollableZone.setMinSize(370,100);
        scrollableZone.setMaxSize(370,100);
        
        VBox futureEventsZone = new VBox(20);
        
        futureEventsZone.getChildren().addAll(new Label("Evènements à venir"),scrollableZone);
        
        return futureEventsZone;
    }
    
    
     /**
     * create the recap zone of failed events
     * @return recap zone as vbox
     */
    private VBox  createFailedEventsZone()
    {   
        final VBox recapZone = new VBox(15);
        
        ObservableList<Node> recapZoneChildren = recapZone.getChildren();
        
        this.failedEventsPath.forEach(eventPath -> {
            Label failLine = new Label(eventPath);
            
            failLine.getStyleClass().add("error");
            
            recapZoneChildren.add(failLine);
        });
        
        ScrollPane scrollableZone = new ScrollPane();
        
        scrollableZone.setContent(recapZone);
        scrollableZone.setPadding(new Insets(10) );
        scrollableZone.setMinSize(370,100);
        scrollableZone.setMaxSize(370,100);
        
        VBox failedEventsZone = new VBox(20);
        
        failedEventsZone.getChildren().addAll(new Label("Evènements non chargés"),scrollableZone);
        
        return failedEventsZone;
    }
    
    public enum MessageType{SUCCESS,FAILURE,STATE,NORMAL,MINER,MONEY_TRANSMISSION};
}
