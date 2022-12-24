package yahaya_alexandre;

import yahaya_alexandre.event.tools.EventGenerator;
import yahaya_alexandre.event.tools.EventGenerator.GeneratorParseResult;

import yahaya_alexandre.event.participant.Participant;
import yahaya_alexandre.event.participant.ParticipantObject;

public class App 
{
    public static void main( String[] args )
    {
        try
        {   
            EventGenerator.generateEvent("src/resources/event.txt","Evenement par défaut",40);
            GeneratorParseResult result = new EventGenerator().getParticipantsFromEventFile("src/resources/event.txt");
            
            if(result.getSuccessfulyParsed() )
            {
                System.out.println("Nom de l'évenement".concat(result.getEventName() ) );
                
                for(Participant p : result.getParticipants() )
                {
                    System.out.println("participant -> ".concat(String.join(" ",p.getName(),p.getFname(),Double.toString(p.getMoney() ) ) ) );
                    
                    for(ParticipantObject po : p.getObjectList() )
                        System.out.println("object -> ".concat(String.join(" ",po.getName(),Double.toString(po.getPrice() ) ) ) );
                }
            }
            else System.out.println("echec du parse");
        }
        catch(Exception e)
        {
            System.out.println("exception dans le main -> " + e);
        }
    }
}
