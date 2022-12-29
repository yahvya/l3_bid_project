package yahaya_alexandre.event.frame;

import java.io.File;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import yahaya_alexandre.event.auction.Auction;
import yahaya_alexandre.event.event.Event;
import yahaya_alexandre.event.participant.Participant;
import yahaya_alexandre.event.participant.ParticipantObject;
import yahaya_alexandre.event.tools.EventGenerator;
import yahaya_alexandre.event.tools.EventGenerator.GeneratorParseResult;

/**
 *
 * @author yahayab
 */
public class EventLoader extends EventPage
{
    private EventLandingPage landingPage;
    
    private ObservableList<Node> zoneChildren;
    
    private FileChooser fileChooser;
    
    private HashMap<Node,String> linesMap;
    
    public EventLoader(Stage window,String windowBaseTitle,EventLandingPage landingPage)
    {
        super(window,windowBaseTitle,900,550);
        
        this.landingPage = landingPage;
        this.fileChooser = new FileChooser();
        this.fileChooser.setTitle("Fichier évenement");
        this.fileChooser.setSelectedExtensionFilter(new ExtensionFilter("Fichier évenement",EventGenerator.EXTENSION) );
        this.linesMap = new HashMap<Node,String>();
    }
    
    @Override
    protected String getDefaultTitle()
    {
        return "Charger des évenements";
    }

    @Override
    protected void buildPage()
    {
         // page header elements
        Label h1 = new Label("Charger vos fichiers évenements");
        
        Button goBackHomeButton = new Button("Retourner à l'accueil");
        
        h1.getStyleClass().add("h1");
        
        HBox pageHeader = new HBox(100);
        
        pageHeader.getChildren().addAll(h1,goBackHomeButton);
        
         // action buttons elements
        
        HBox actionButtonsContainer = new HBox(20);
        
        Button loadNewEventButton = new Button("Charger un évenement");
        
        Button startEventsButton = new Button("Lancer les évenements chargés");
        
        actionButtonsContainer.getChildren().addAll(loadNewEventButton,startEventsButton);
        
        // message printer
        
        Label message = new Label("Appuyez sur le boutton lancer les évenements pour lancer l'exécution des évenements, les évenements dont le chargement aura échoué seront ignorés");
        
        message.getStyleClass().add("message");
        
        VBox eventsToLoadContainer = new VBox(20);
        
        this.zoneChildren = eventsToLoadContainer.getChildren();
        
        ScrollPane scrollPane = new ScrollPane();
        
        scrollPane.setContent(eventsToLoadContainer);
        scrollPane.setPadding(new Insets(20) );
        
        VBox globalContainer = new VBox(30);
        
        globalContainer.setPadding(new Insets(20) );
        globalContainer.getChildren().addAll(pageHeader,actionButtonsContainer,scrollPane);
        
        this.page = new Scene(globalContainer);
        
        // add events
        
         goBackHomeButton.setOnAction(new EventHandler<ActionEvent>(){
            public void handle(ActionEvent event)
            {
                landingPage.putPageOnWindow();
            }
        });
         
         loadNewEventButton.setOnAction(new EventHandler<ActionEvent>(){
             public void handle(ActionEvent event)
             {
                 addEventToLoadZone();
             }
         });
         
         startEventsButton.setOnAction(new EventHandler<ActionEvent>(){
             public void handle(ActionEvent event)
             {
                startEvents();
             }
         });
    }
    
    /**
     * @see add a new selection area
     */
    private void addEventToLoadZone()
    {   
        final Label fileNamePrinter = new Label("Veuillez sélectionner un fichier d'évenement");
        
        Button loadFileButton = new Button("Charger");
        
        HBox container = new HBox(20);
        
        container.getChildren().addAll(fileNamePrinter,loadFileButton);
        container.getStyleClass().add("bordered-zone");
        
        Button delete = new Button("Retirer");
        
        final HBox globalContainer = new HBox(20);
        
        globalContainer.getChildren().addAll(delete,container);
        globalContainer.getStyleClass().add("centered-zone");
        
        this.zoneChildren.add(globalContainer);
        this.linesMap.put(globalContainer,null);
        
        // events
        
        delete.setOnAction(new EventHandler<ActionEvent>(){
            public void handle(ActionEvent e)
            {
                zoneChildren.remove(globalContainer);
                linesMap.remove(globalContainer);
            }
        });
        
        loadFileButton.setOnAction(new EventHandler<ActionEvent>(){
            public void handle(ActionEvent e)
            {
                File choosenFile = fileChooser.showOpenDialog(window);
                
                if(choosenFile != null)
                {
                    fileNamePrinter.setText(choosenFile.getName() );
                    
                    linesMap.put(globalContainer,choosenFile.getAbsolutePath() );
                }
            }
        });
    }
    
    /**
     * @see parse events file and try to start them
     */
    private void startEvents()
    {
        EventGenerator generator = new EventGenerator();
        
        ArrayList<Event> eventsList = new ArrayList<Event>();
        
        ArrayList<String> failedEventsPath = new ArrayList<String>();
        
        if(linesMap.size() == 0)
            return;
        
        // iterate on entries to create events 
        
        for(Map.Entry<Node,String> set : linesMap.entrySet() )
        {
            try
            {
                String eventFilePath = set.getValue();
                
                if(eventFilePath == null)
                    continue;
                
                GeneratorParseResult parseResult = generator.getDatasFromEventFile(eventFilePath);
                
                if(!parseResult.getSuccessfulyParsed() )
                {
                    failedEventsPath.add(eventFilePath);
                    
                    continue;
                }
                
                String eventName = parseResult.getEventName();
                
                ArrayList<Participant> participants = parseResult.getParticipants();
                
                eventsList.add(new Event(eventName,participants,this.getAuctions(eventName,participants) ) );
            }
            catch(Exception e){}
        }
        
        EventViewer  eventManager = new EventViewer(this.window,this.windowBaseTitle,this.landingPage,failedEventsPath,eventsList);
        
        eventManager.putPageOnWindow();
        eventManager.startEvent();
    }
    
    /**
     * get auctions from user or create them automtically based on user choice
     * @param eventName
     * @param participants
     * @return auctions
     */
    public ArrayList<Auction> getAuctions(String eventName,ArrayList<Participant> participants)
    {
        ArrayList<Auction> auctions = new ArrayList<Auction>();
        
        try
        {
            // ask user to know the way to generate auctions
            ButtonType automaticChoice = new ButtonType("Créer automatiquement",ButtonBar.ButtonData.YES);
            ButtonType manualChoice = new ButtonType("Créer manuellement",ButtonBar.ButtonData.NO);

            Alert choiceBox = new Alert(Alert.AlertType.CONFIRMATION,"Comment voulez vous générer les ventes ? (manuel par défaut)",automaticChoice,manualChoice);

            choiceBox.setTitle(eventName);
            choiceBox.setHeaderText(String.join(" ","Choix pour l'évenement (",eventName,")") );
            choiceBox.setWidth(500);

            Optional<ButtonType> result = choiceBox.showAndWait();

            if(result.isPresent() && result.get() == automaticChoice)
            {
                int participantsIndex = participants.size() - 1;

                Random random = new Random();

                for(int countOfAuctions = random.nextInt(1,6); countOfAuctions > 0; countOfAuctions--)
                {
                    Participant owner = participants.get(random.nextInt(0,participantsIndex) );

                    ArrayList<ParticipantObject> ownerObjects = owner.getObjectList();

                    int countOfOwnerObjects = ownerObjects.size();

                    // restart if owner don't have an object to sell
                    if(countOfOwnerObjects < 1)
                    {
                        countOfAuctions++;

                        continue;
                    }

                    // the auction have to start after a set of seconds between(3 and 30)
                    ZonedDateTime startDateTime = ZonedDateTime.now().plusSeconds(random.nextInt(3,30) );

                    auctions.add(new Auction(owner,ownerObjects.get(countOfOwnerObjects == 1  ? 0 : random.nextInt(0,countOfOwnerObjects - 1) ),startDateTime,startDateTime.plusMinutes(random.nextInt(1,3) ) ) );
                }
            }
            else // manual mode
            {
                Stage manualModeWindow = new Stage();

                manualModeWindow.initOwner(this.window);

                new AuctionGetterPage(manualModeWindow,this.windowBaseTitle,auctions,participants).putPageOnWindow(true);
            }
        }
        catch(Exception e){System.out.println("exception ici -> " + e);};
        
        return auctions;
    }
}


