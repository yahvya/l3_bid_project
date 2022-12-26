package yahaya_alexandre.event.frame;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

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
        
        final EventCreator eventCreationPage = new EventCreator(this.window,this.windowBaseTitle,this);
        
        final EventLoader eventLoaderPage = new EventLoader(this.window,this.windowBaseTitle,this);
        
        // add onclick event on buttons
        createEventsButton.setOnAction(new EventHandler<ActionEvent>(){
            public void handle(ActionEvent event)
            {
                eventCreationPage.putPageOnWindow();
            }
        });
        
        loadEventsButton.setOnAction(new EventHandler<ActionEvent>(){
            public void handle(ActionEvent event)
            {
                eventLoaderPage.putPageOnWindow();
            }
        });
    }
}
