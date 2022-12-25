package yahaya_alexandre.event.frame;

import javafx.collections.ObservableList;

import javafx.geometry.Insets;

import javafx.scene.Scene;

import javafx.scene.control.Label;
import javafx.scene.control.Button;

import javafx.scene.layout.VBox;

import javafx.stage.Stage;

/**
 *
 * @author yahayab
 */
public class EventLandingPage extends EventPage
{
    public EventLandingPage(Stage window,String windowBaseTitle)
    {
        super(window,windowBaseTitle,500,280);
    }
    
    @Override
    protected String getDefaultTitle()
    {
        return "Accueil";
    }
    
    @Override
    protected void buildPage()
    {
        Button loadEventsButton = new Button("Charger des évenements");
        Button createEventsButton = new Button("Créer des évenements");
        
        Label h1 = new Label("Bienvenue sur l'outil de gestion d'enchères");
        Label h3 = new Label("Choisissez l'action à faire");
        
        VBox globalContainer = new VBox(20);
        
        h1.getStyleClass().add("h1");
        
        globalContainer.getChildren().addAll(h1,h3,loadEventsButton,createEventsButton);
        globalContainer.setPadding(new Insets(30) );
                
        this.page = new Scene(globalContainer);
        
        // add onclick event on buttons
    }
}
