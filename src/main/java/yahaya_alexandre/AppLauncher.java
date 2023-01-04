package yahaya_alexandre;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import yahaya_alexandre.event.frame.EventLandingPage;

/**
 *
 * @author yahayab
 */
public class AppLauncher extends Application
{
    public static final int WINDOW_WIDTH = 500;
    public static final int WINDOW_HEIGHT = 400;
    
    private Stage window;
    
    @Override
    public void start(Stage mainWindow) throws Exception
    {
       this.window = mainWindow;
       
       this.setDefaultWindowStyle();
       
       EventLandingPage landingPage = new EventLandingPage(this.window,"Projet poo2 licence 3");
       
       landingPage.putPageOnWindow();
    }
    
    /**
     * @see setup the window before show
     */
    private void setDefaultWindowStyle()
    {
        this.window.getIcons().add(new Image("app-icon.jpg") );
        this.window.setWidth(AppLauncher.WINDOW_WIDTH);
        this.window.setHeight(AppLauncher.WINDOW_HEIGHT);
        this.window.centerOnScreen();
    }
    
    /**
     * Get the value of window
     * @return window
     */
    public Stage getWindow()
    {
        return this.window;
    }
}
