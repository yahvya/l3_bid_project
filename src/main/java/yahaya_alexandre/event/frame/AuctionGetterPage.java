package yahaya_alexandre.event.frame;

import java.util.ArrayList;
import java.util.HashMap;
import javafx.geometry.Insets;
import javafx.scene.Node;
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
import yahaya_alexandre.event.auction.Auction;
import yahaya_alexandre.event.participant.Participant;

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
        super(window,windowBaseTitle,900,450);
        
        this.toFill = toFill;
        this.participants = participants;
        this.linesMap = new HashMap<HBox,Auction>();
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
        
        VBox recapContainer = new VBox(20);
        
        ScrollPane recapScrollPane = new ScrollPane();
        
        recapScrollPane.setContent(recapContainer);
        recapScrollPane.setPadding(new Insets(20) );
        
        VBox recapZone = new VBox(30);
        
        recapZone.getChildren().addAll(recapTitle,recapScrollPane);
        recapZone.setPadding(new Insets(20) );
        
        // add new auction zone
        
        // participant selecton group
        Label participantSelectorIndicator = new Label("Choisissez le participant");
        ChoiceBox participantSelector = new ChoiceBox();
        VBox participantSelectorGroup = new VBox(10);
        
        participantSelectorGroup.getChildren().addAll(participantSelectorIndicator,participantSelectorGroup);
        
        // object selection group
        Label objectSelectorIndicator = new Label("Choissiez l'objet à vendre");
        ChoiceBox objectSelector = new ChoiceBox();
        VBox objectSelectorGroup = new VBox(10);
        
        objectSelectorGroup.getChildren().addAll(objectSelectorIndicator,objectSelector);
        
        // start datetitme group
        Label startDateTimeIndicator = new Label("Date heure de début de la vente");
        TextField startDateTime = new TextField();
        startDateTime.setPromptText("YYYY-mm-dd h:i:s");
        VBox startDateTimeGroup = new VBox(10);
        
        startDateTimeGroup.getChildren().addAll(startDateTimeIndicator,startDateTime);
        
        // end datetimegroup
        Label endDateTimeIndicator = new Label("Date heure de fin de la vente");
        TextField endDateTime = new TextField();
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
    }
    
}
