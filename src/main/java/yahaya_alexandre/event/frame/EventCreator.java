package yahaya_alexandre.event.frame;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import yahaya_alexandre.event.event.Event;
import yahaya_alexandre.event.exception.NotEnoughParticipants;

import yahaya_alexandre.event.tools.EventGenerator;

/**
 *
 * @author yahayab
 */
public class EventCreator extends EventPage
{
    private EventLandingPage landingPage;
    
    private ObservableList<Node> zoneChildren;
    
    private HashMap<HBox,String> linesMap;
    
    private FileChooser fileChooser;
    
    public EventCreator(Stage window,String windowBaseTitle,EventLandingPage landingPage)
    {
        super(window,windowBaseTitle,900,550);
        
        this.landingPage = landingPage;
        this.fileChooser = new FileChooser();
        this.fileChooser.setTitle("Fichier évenement");
        this.fileChooser.setSelectedExtensionFilter(new ExtensionFilter("Fichier évenement",EventGenerator.EXTENSION) );
        this.linesMap = new HashMap<HBox,String>();
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
        
        Label message = new Label(String.join(" ","Appuyez sur le boutton créer pour générer les évenements. Le nombre minimum de participants est de",Integer.toString(Event.MINIMUM_PARTICIPANTS) ) );
        
        message.getStyleClass().add("message");
        
        // events elements container

        VBox eventsToCreateContainer = new VBox(25);
        
        ScrollPane scrollablePane = new ScrollPane();
        
        scrollablePane.setContent(eventsToCreateContainer);
        scrollablePane.setPadding(new Insets(15) );
        
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
        
        confirmEventCreationButton.setOnAction(new EventHandler<ActionEvent>(){
            public void handle(ActionEvent event)
            {
                createEvents();
            }
        });
    }
    
    /**
     * print a message before an element or replace the previous message
     * @param value
     * @param message 
     */
    private void printMessageBefore(HBox value,String message)
    {
        int index = this.zoneChildren.indexOf(value);
        
        if(index == -1)
            return;
        
        Label messagePrinter = new Label(message);
        
        if(index != 0)
        {
            Node el = this.zoneChildren.get(index - 1);
            
            if(el instanceof Label)
            {   
                messagePrinter = (Label) el;
                
                messagePrinter.setText(message);
                
                return;
            }
        }
        
        this.zoneChildren.add(index,messagePrinter);
    }
    
    /**
     * @see try to create the given events
     */
    private void createEvents()
    {
        ArrayList<HBox> toRemove = new ArrayList<HBox>();
        
        for(Map.Entry<HBox,String> set : linesMap.entrySet() )
        {
            HBox container = set.getKey();
            
            String eventAbsolutePath = set.getValue();
            
            if(eventAbsolutePath == null)
            {
                this.printMessageBefore(container,"veuillez choisir un fichier");
                
                continue;
            }
            
            ObservableList<Node> children = container.getChildren();
            
            String eventName = ((TextField) children.get(0) ).getText();
            
            if(eventName.length() < 2)
            {
                this.printMessageBefore(container,"Le nom de l'évenement est trop court");
                
                continue;
            }
            
            int countOfParticipants;
            
            try
            {
                countOfParticipants = Integer.parseInt(((TextField) children.get(1) ).getText() );
            }
            catch(NumberFormatException e)
            {
                this.printMessageBefore(container,"Assurez vous d'avoir saisi au nombre de participant correct");
                continue;
            }
            
            try
            {
               if(EventGenerator.generateEvent(String.join(".",this.linesMap.get(container),EventGenerator.EXTENSION),eventName,countOfParticipants) )
                    toRemove.add(container);
                else 
                   this.printMessageBefore(container,"Une erreur s'est produite lors de la création du fichier"); 
            }
            catch(NotEnoughParticipants e)
            {
                this.printMessageBefore(container,"Il n'y a pas assez de participants");
            }
        }
        
        for(HBox zone : toRemove)
            this.deleteZone(zone);
    }
    
    /**
     * delete a zone in list
     * @param zone 
     */
    private void deleteZone(HBox zone)
    {
        int index = this.zoneChildren.indexOf(zone);
                
        if(index != 0 && this.zoneChildren.get(index - 1) instanceof Label)
            this.zoneChildren.remove(index - 1);

        this.zoneChildren.remove(zone);

        this.linesMap.remove(zone);
    }
    
    /**
     * @see add a new zone to get an event data
     */
    private void addNewEventGetterZone()
    {
        TextField eventNameField = new TextField(); 
        TextField eventParticipantsCountField = new TextField();
        
        eventNameField.setPromptText("Nom de l'évenement");
        eventParticipantsCountField.setPromptText("Nombre de participants");
        
        HBox fileChooserZone  = new HBox(5);
        
        final Label pathPrinter = new Label("veuillez sélectionner la destination");
        Button choosePathButton = new Button("Sélectionner");
        
        pathPrinter.setMaxWidth(230);
        
        fileChooserZone.getChildren().addAll(pathPrinter,choosePathButton);
        fileChooserZone.getStyleClass().addAll("bordered-zone","centered-zone");
        
        Button deleteButton = new Button("Retirer");
        
        final HBox zone = new HBox(20);
        
        zone.getChildren().addAll(eventNameField,eventParticipantsCountField,fileChooserZone,deleteButton);
        zone.getStyleClass().add("centered-zone");
        
        this.linesMap.put(zone,null);
        
        this.zoneChildren.add(zone);
        
        // remove event
        
        deleteButton.setOnAction(new EventHandler<ActionEvent>(){
            public void handle(ActionEvent event)
            {
                deleteZone(zone);
            }
        });
        
        choosePathButton.setOnAction(new EventHandler<ActionEvent>(){
            public void handle(ActionEvent event)
            {
                String choosenFileName = chooseFile(zone);
                
                if(choosenFileName != null)
                    pathPrinter.setText(choosenFileName);
            }
        });
    }
    
    /**
     * 
     * @return file absolute path or null
     */
    private String chooseFile(HBox key)
    {
        File choosenFile = this.fileChooser.showSaveDialog(this.window);
        
        if(choosenFile != null)
        {
            this.linesMap.put(key,choosenFile.getAbsolutePath() );
            
            return choosenFile.getName();
        }
        
        return null;
    }
}
