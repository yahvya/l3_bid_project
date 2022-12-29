package yahaya_alexandre.event.frame;

import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.Serializable;

/**
 * 
 * @author yahayab
 */
public abstract class EventPage implements Serializable
{   
    protected Stage window;
    
    protected Scene page;
    
    protected String title;
    protected String windowBaseTitle;
    
    private ObservableList stylesheets;
    
    protected int heightToSet;
    protected int widthToSet;
    
    public EventPage(Stage window,String windowBaseTitle,int widthToSet,int heightToSet)
    {
        this.window = window;
        this.windowBaseTitle = windowBaseTitle;
        this.widthToSet = widthToSet;
        this.heightToSet = heightToSet;
        this.title = String.join(" - ",this.windowBaseTitle,this.getDefaultTitle() );
        this.buildPage();
        this.addStyleSheets();
    }
    
    public EventPage(Stage window,String windowBaseTitle,int withToSet,int heightToSet,boolean buildLater)
    {
        this.window = window;
        this.windowBaseTitle = windowBaseTitle;
        this.widthToSet = widthToSet;
        this.heightToSet = heightToSet;
        this.title = String.join(" - ",this.windowBaseTitle,this.getDefaultTitle() );
    }
    
    /**
     * add global stylessheets
     */
    protected void addStyleSheets()
    {
        // add the app default css
        this.stylesheets = this.page.getStylesheets();
        this.stylesheets.add("style.css");
    }
    
    /**
     * @see print the landing page on the current window
     * @return self
     */
    public EventPage putPageOnWindow()
    {
        this.window.setScene(this.page);
        this.window.setTitle(this.title);
        this.window.setWidth(this.widthToSet);
        this.window.setHeight(this.heightToSet);
        this.window.centerOnScreen();
        this.window.show();
        
        return this;
    }
    
    /**
     * @see print the landing page on the current window and wait
     * @return self
     */
    public EventPage putPageOnWindow(boolean wait)
    {
        this.window.setScene(this.page);
        this.window.setTitle(this.title);
        this.window.setWidth(this.widthToSet);
        this.window.setHeight(this.heightToSet);
        this.window.centerOnScreen();
        this.window.showAndWait();
        
        return this;
    }
    
    /**
     * add a css on the current css
     * @param cssFilePath
     * @return self
     */
    public EventPage addCssOn(String cssFilePath)
    {
        this.stylesheets.add(cssFilePath);
        
        return this;
    }
    
    /**
     * Get the value of windowBaseTitle
     * @return the value of windowBaseTitle
     */
    public String getWindowBaseTitle()
    {
        return windowBaseTitle;
    }
    
    /**
     * Get the value of title
     *
     * @return the value of title
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * Get the value of page
     *
     * @return the value of page
     */
    public Scene getPage()
    {
        return page;
    }
    
    /**
     * Get the value of window
     *
     * @return the value of window
     */
    public Stage getWindow()
    {
        return window;
    }

    /**
     * Set the value of window
     *
     * @param window new value of window
     */
    public void setWindow(Stage window)
    {
        this.window = window;
    }
    
    /**
     * @see have to return the default title of this page
     * @return 
     */
    abstract protected String getDefaultTitle();
    
    /**
     * @see have to build this page
     */
    abstract protected void buildPage();
}
