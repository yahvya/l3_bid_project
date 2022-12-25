package yahaya_alexandre;

import javafx.application.Application;

import javafx.stage.Stage;

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
       
       this.setWindowStyle();
       
       this.window.setTitle("Projet poo2 licence 3");
       this.window.show();
    }
    
    /**
     * @see init the application
     */
    public static void initEventFrame(String[] args)
    {
        AppLauncher.launch(args);
    }
    
    /**
     * @see setup the window before show
     */
    private void setWindowStyle()
    {
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
