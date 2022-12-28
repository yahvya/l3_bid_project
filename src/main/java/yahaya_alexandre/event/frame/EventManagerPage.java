package yahaya_alexandre.event.frame;

import java.util.ArrayList;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import yahaya_alexandre.event.event.Event;
import yahaya_alexandre.event.participant.Participant;
import yahaya_alexandre.event.participant.ParticipantObject;

/**
 *
 * @author yahayab
 */
public class EventManagerPage extends EventPage
{
    private EventLandingPage landingPage;
    
    private ArrayList<String> failedEventsPath;
    private ArrayList<Event> eventsList;
    
    private Event managedEvent; 
    
    private ObservableList<Node> messagesZoneChildren;
    private ObservableList<Node> futureAuctionsRecapChildren;
    private ObservableList<Node> futureEventsRecapChildren;
    
    /**
     * wait non empty eventsList
     * @param window
     * @param windowBaseTitle
     * @param landingPage
     * @param failedEventsPath
     * @param eventsList 
     */
    public EventManagerPage(Stage window,String windowBaseTitle,EventLandingPage landingPage,ArrayList<String> failedEventsPath,ArrayList<Event> eventsList)
    {
        super(window,windowBaseTitle,700,900,true);
        
        this.failedEventsPath = failedEventsPath;
        this.eventsList = eventsList;
        
        if(eventsList.size() > 0)
        {
            this.managedEvent = eventsList.get(0);
            
            eventsList.remove(0);
        }
        else this.managedEvent = null;
        
        this.buildPage();
        this.addStyleSheets();
    }
    
    @Override
    public EventPage putPageOnWindow()
    {
        this.window.setScene(this.page);
        this.window.setTitle(this.title);
        this.window.setMaximized(true);
        this.window.show();
        
        return this;
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
    }
    
    /**
     * create the body
     * @return the body container as hbox
     */
    private HBox createBody()
    {
        VBox executionMessagesZone = new VBox(20);
        
        this.messagesZoneChildren = executionMessagesZone.getChildren();
        
        ScrollPane scrollableZone = new ScrollPane();
        
        scrollableZone.setContent(executionMessagesZone);
        
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
                    
                    futureAuctionsRecapChildren.add(sellDataText);
                }
                catch(Exception e){}
            });
        }
        
        ScrollPane scrollableZone = new ScrollPane();
        
        scrollableZone.setContent(recapZone);
        scrollableZone.setPadding(new Insets(10) );
        scrollableZone.setMinSize(260,100);
        scrollableZone.setMaxSize(260,100);
        
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
        scrollableZone.setMinSize(260,100);
        scrollableZone.setMaxSize(260,100);
        
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
        scrollableZone.setMinSize(260,100);
        scrollableZone.setMaxSize(260,100);
        
        VBox failedEventsZone = new VBox(20);
        
        failedEventsZone.getChildren().addAll(new Label("Evènements non chargés"),scrollableZone);
        
        return failedEventsZone;
    }
    
}
