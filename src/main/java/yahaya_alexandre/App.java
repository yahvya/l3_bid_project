package yahaya_alexandre;

import yahaya_alexandre.event.tools.EventGenerator;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        try
        {
            EventGenerator.generateEvent("src/resources/event.txt","Evenement par défaut",40);
        }
        catch(Exception e)
        {
            System.out.println("exception -> " + e);
        }
    }
}
