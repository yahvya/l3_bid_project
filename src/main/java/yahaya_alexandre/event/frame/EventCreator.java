package yahaya_alexandre.event.frame;

import java.util.ArrayList;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import yahaya_alexandre.event.tools.EventGenerator;

/**
 *
 * @author yahayab
 */
public class EventCreator extends EventPage
{
    private EventLandingPage landingPage;
    
    private ObservableList<Node> zoneChildren;
    
    private ArrayList<EventGeneratorData> eventsToCreateData;
    
    public EventCreator(Stage window,String windowBaseTitle,EventLandingPage landingPage)
    {
        super(window,windowBaseTitle,900,550);
        
        this.landingPage = landingPage;
        this.eventsToCreateData = new ArrayList<EventGeneratorData>();
    }
    
    @Override
    protected String getDefaultTitle()
    {
        return "Créer des évenements";
    }
    
    @Override
    protected void buildPage()
    {
        // page header elements
        Label h1 = new Label("Créer fichiers évenements");
        
        Button goBackHomeButton = new Button("Retourner à l'accueil");
        
        h1.getStyleClass().add("h1");
        
        HBox pageHeader = new HBox(100);
        
        pageHeader.getChildren().addAll(h1,goBackHomeButton);
        
        // action buttons elements
        
        HBox actionButtonsContainer = new HBox(20);
        
        Button addNewEventButton = new Button("Ajouter un évenement");
        
        Button confirmEventCreationButton = new Button("Créez vos fichiers d'évenements");
        
        actionButtonsContainer.getChildren().addAll(addNewEventButton,confirmEventCreationButton);
        
        // message printer
        
        Label message = new Label("Appuyez sur le boutton créer pour générer les évenements");
        
        message.getStyleClass().add("message");
        
        // events elements container

        VBox eventsToCreateContainer = new VBox();
        
        ScrollPane scrollablePane = new ScrollPane();
        
        scrollablePane.setContent(eventsToCreateContainer);
        
        this.zoneChildren = eventsToCreateContainer.getChildren();
        
        // global container
        
        VBox globalContainer = new VBox(30);
        
        globalContainer.getChildren().addAll(pageHeader,actionButtonsContainer,message,scrollablePane);
        globalContainer.setPadding(new Insets(20) );
        
        this.page = new Scene(globalContainer);
        
        // add events
        
        goBackHomeButton.setOnAction(new EventHandler<ActionEvent>(){
            public void handle(ActionEvent event)
            {
                landingPage.putPageOnWindow();
            }
        });
        
        addNewEventButton.setOnAction(new EventHandler<ActionEvent>(){
            public void handle(ActionEvent event)
            {
                addNewEventGetterZone();
            }
        });
    }
    
    private void addNewEventGetterZone()
    {
        HBox zone = new HBox(20);
        
        this.zoneChildren.add(zone);
    }
    
    class EventGeneratorData
    {
        private String eventFilePath;
        private String eventName; 
        
        private int countOfParticipants;

        public String getEventFilePath()
        {
            return eventFilePath;
        }

        public void setEventFilePath(String eventFilePath)
        {
            this.eventFilePath = eventFilePath;
        }

        public String getEventName()
        {
            return eventName;
        }

        public void setEventName(String eventName)
        {
            this.eventName = eventName;
        }

        public int getCountOfParticipants()
        {
            return countOfParticipants;
        }

        public void setCountOfParticipants(int countOfParticipants)
        {
            this.countOfParticipants = countOfParticipants;
        }
        
        
    }
}
