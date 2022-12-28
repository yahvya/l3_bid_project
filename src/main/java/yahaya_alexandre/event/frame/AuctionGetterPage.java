package yahaya_alexandre.event.frame;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import yahaya_alexandre.event.auction.Auction;
import yahaya_alexandre.event.participant.Participant;
import yahaya_alexandre.event.participant.ParticipantObject;

/**
 *
 * @author yahayab
 */
public class AuctionGetterPage extends EventPage
{
    private ArrayList<Auction> toFill;
    private ArrayList<Participant> participants;
    
    private HashMap<HBox,Auction> linesMap;
    
    public AuctionGetterPage(Stage window,String windowBaseTitle,ArrayList<Auction> toFill,ArrayList<Participant> participants)
    {
        super(window,windowBaseTitle,900,450,true);
        
        this.toFill = toFill;
        this.participants = participants;
        this.linesMap = new HashMap<HBox,Auction>();
        this.buildPage();
        this.addStyleSheets();
    }
    
    @Override
    protected String getDefaultTitle()
    {
        return "Créez les ventes";
    }

    @Override
    protected void buildPage()
    {
        // recap zone elements
        Label recapTitle = new Label("Récapitulatif des ventes à créer");
        
        Button confirmationButton = new Button("Confirmer les ventes");
        
        VBox recapContainer = new VBox(20);
        
        ScrollPane recapScrollPane = new ScrollPane();
        
        recapScrollPane.setContent(recapContainer);
        recapScrollPane.setPadding(new Insets(20) );
        
        final VBox recapZone = new VBox(30);
        
        HBox recapHeader = new HBox(15);
        
        recapHeader.getChildren().addAll(recapTitle,confirmationButton);
        
        recapZone.getChildren().addAll(recapHeader,recapScrollPane);
        recapZone.setPadding(new Insets(20) );
        
        // add new auction zone
        
        // participant selecton group
        
        // fill participants items
        
        final ObservableList<String> participantsItems = FXCollections.observableArrayList();
        
        final HashMap<String,Participant> linesMap = new HashMap<String,Participant>();
        
        this.participants.forEach(p -> {
            if(p.getObjectList().size() > 0)
            {
                String key = String.join(" ",Integer.toString(p.getId() ),p.getName(),p.getFname() );
            
                linesMap.put(key,p);
                participantsItems.add(key);
            }
        });
        
        Label participantSelectorIndicator = new Label("Choisissez le participant");
        ChoiceBox<String> participantSelector = new ChoiceBox<String>(participantsItems);
        VBox participantSelectorGroup = new VBox(10);
        
        participantSelectorGroup.getChildren().addAll(participantSelectorIndicator,participantSelector);
        
        // object selection group
        Label objectSelectorIndicator = new Label("Choissiez l'objet à vendre");
        ChoiceBox<String> objectSelector = new ChoiceBox<String>();
        final ObservableList<String> objectSelectorChildren = objectSelector.getItems();
        VBox objectSelectorGroup = new VBox(10);
        
        objectSelectorGroup.getChildren().addAll(objectSelectorIndicator,objectSelector);
        
        // start datetitme group
        Label startDateTimeIndicator = new Label("Date heure de début de la vente");
        final TextField startDateTime = new TextField();
        startDateTime.setPromptText("YYYY-mm-dd h:i:s");
        VBox startDateTimeGroup = new VBox(10);
        
        startDateTimeGroup.getChildren().addAll(startDateTimeIndicator,startDateTime);
        
        // end datetimegroup
        Label endDateTimeIndicator = new Label("Date heure de fin de la vente");
        final TextField endDateTime = new TextField();
        endDateTime.setPromptText("YYYY-mm-dd h:i:s");
        VBox endDateTimeGroup = new VBox(10);
        
        endDateTimeGroup.getChildren().addAll(endDateTimeIndicator,endDateTime);
        
        Button addConfirmationButton = new Button("Ajouter");
        
        HBox addingElementsContainer = new HBox(20);
        
        addingElementsContainer.getChildren().addAll(participantSelectorGroup,objectSelectorGroup,startDateTimeGroup,endDateTimeGroup,addConfirmationButton);
        addingElementsContainer.setPadding(new Insets(20) );
        addingElementsContainer.getStyleClass().add("bordered-zone");
        
        Label creationTitle = new Label("Ajouter une vente");
        
        VBox addZone = new VBox(25);
        
        addZone.getChildren().addAll(creationTitle,addingElementsContainer);
        addZone.setPadding(new Insets(20) );
        
        VBox globalContainer = new VBox(10);
        
        globalContainer.getChildren().addAll(addZone,recapZone);

        this.page = new Scene(globalContainer);
        this.window.setMaximized(true);
        
        // add event on participants chooser
        
        final HashMap<String,ParticipantObject> objectsMap = new HashMap<String,ParticipantObject>();
        
        participantSelector.getSelectionModel().selectedItemProperty().addListener((options,oldValue,newValue) -> {
            
            // fill the object selector with the choosed client objets
            
            objectsMap.clear();
            objectSelectorChildren.clear();

            linesMap.get((String) newValue).getObjectList().forEach(object -> {
                String key = String.join(" ",Integer.toString(object.getObjectId() ),object.getName(),Double.toString(object.getPrice() ).concat("€") );
                
                objectsMap.put(key,object);

                objectSelectorChildren.add(key);
            });
        });
        
        addConfirmationButton.setOnAction(new EventHandler<ActionEvent>(){
            public void handle(ActionEvent e)
            {
                String participantKey = (String) participantSelector.getValue();
                String objectKey = (String) objectSelector.getValue();
                String startDate = startDateTime.getText();
                String endDate = endDateTime.getText();
                
                if(participantKey == null || objectKey == null || startDate.length() == 0 || endDate.length() == 0)
                    return;
                
                addNewAuction(linesMap.get(participantKey),objectsMap.get(objectKey),startDate,endDate,recapContainer);
            }
        });
        
        confirmationButton.setOnAction(new EventHandler<ActionEvent>(){
            public void handle(ActionEvent e)
            {
                // close the window
                window.close();
            }
        });
    }
    
    /**
     * add a new line to the recap zone and add to the tmp auctions
     * @param participant
     * @param object
     * @param startDate
     * @param endDate 
     */
    private void addNewAuction(Participant owner,ParticipantObject object,String startDate,String endDate,VBox recapZone)
    {
        try
        {      
            // create the recap and auctions object
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
            
            final Auction createdAuction = new Auction(owner,object,ZonedDateTime.parse(startDate + " Europe/Paris",formatter),ZonedDateTime.parse(endDate + " Europe/Paris",formatter) );
            
            this.toFill.add(createdAuction);
            
            Label auctionRecapText = new Label(String.join(" ","Vente de l'objet << ",object.getName()," (",String.join(" - ",startDate,endDate),")", ">> appartenant à",owner.getName(),owner.getFname(),"(",Integer.toString(owner.getId() ),")") );

            Button removeButton = new Button("Retirer");

            removeButton.getStyleClass().add("centered-zone");

            HBox newRecap = new HBox(10);

            newRecap.getChildren().addAll(removeButton,auctionRecapText);
            newRecap.getStyleClass().add("bordered-zone");
            newRecap.setPadding(new Insets(15) );

            recapZone.getChildren().add(newRecap);

            removeButton.setOnAction(new EventHandler<ActionEvent>(){
                public void handle(ActionEvent e)
                {
                    recapZone.getChildren().remove(newRecap);
                    
                    toFill.remove(createdAuction);
                }
            });
        }
        catch(Exception e){}
    }
}
