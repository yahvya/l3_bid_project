package yahaya_alexandre.event.tools;

import yahaya_alexandre.event.event.Event;
import yahaya_alexandre.event.exception.NotEnoughParticipants;

import yahaya_alexandre.event.participant.Participant;
import yahaya_alexandre.event.participant.ParticipantObject;

import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import com.github.javafaker.Commerce;

import java.io.FileWriter;
import java.io.File;
import java.io.IOException;

import java.util.Scanner;
import java.util.ArrayList;
import java.util.Random;
import java.util.Locale;

/**
 *
 * @author yahayab
 */
public class EventGenerator
{
    public static String[] instructions =
    {
        "c fichier représentant un évenement",
        "c description du format",
        "c les lignes commençant par c sont des commentaires ignorés lors du parsage",
        "c les lignes commençant par p sont des personnes sous le format(id,nom,prénom,argent)",
        "c les lignes commençant par o sont des objets sous le format (id_proprietaire,id_objet,nom_objet,prix)",
        "c les lignes commençant par n définissent le nom de l'évenement"
    };

    /**
     * generate an event file
     *
     * @param eventFilePath destination file path
     * @param eventName event name
     * @param countOfParticipants count of participants in the event
     * @return true if successfuly created else false
     */
    public static boolean generateEvent(String eventFilePath, String eventName, int countOfParticipants) throws NotEnoughParticipants
    {
        if(countOfParticipants < Event.MINIMUM_PARTICIPANTS)
              throw new NotEnoughParticipants(countOfParticipants);
            
        try
        {
            FileWriter writer = new FileWriter(eventFilePath);

            Faker faker = new Faker(Locale.FRANCE);

            Random random = new Random();

            // écriture des instructions dans le fichier
            writer.write(String.join("\n", EventGenerator.instructions) );
            
            // écriture du nom de l'évenement
            writer.write("\nn ".concat(eventName).concat("\n") );

            // écriture des participants
            for (int i = 0; i < countOfParticipants; i++)
            {
                Name participantNameData = faker.name();

                writer.write("p ".concat(String.join(",", Integer.toString(i + 1), participantNameData.lastName(), participantNameData.name(),Integer.toString(faker.number().numberBetween(2000, 6000) ).replace(",",".") ) ).concat("\n") );
            }
           
            // écriture des objets
            int countOfObjects = random.nextInt(15,40);
            
            for(int i = 0; i < countOfObjects; i++)
            {
                Commerce objectCommerce = faker.commerce();
                
                writer.write("o ".concat(String.join(",",Integer.toString(random.nextInt(1,countOfParticipants) ),Integer.toString(i + 1),objectCommerce.productName(),objectCommerce.price().replace(",",".") ) ).concat("\n") );
            }
            
            writer.close();
            
            return true;
        }
        catch(NullPointerException e){}
        catch(IOException e){}

        return false;
    }
        
    /**
     * parse the event file and get the participants with their objects
     * @param eventFilePath the event file path
     * @return parsing result
     */
    public GeneratorParseResult getParticipantsFromEventFile(String eventFilePath)
    {    
        try
        {
            ArrayList<Participant> participants = new ArrayList<Participant>();
            
            Scanner reader = new Scanner(new File(eventFilePath) );
            
            String fileLine;    
            String eventName = "default name";
            
            String[] lineDatas;
            
            while(reader.hasNextLine() )
            {
                fileLine = reader.nextLine();
                
                if(fileLine.length() == 0)
                    continue;
                
                char linePropableType = fileLine.charAt(0);
                
                fileLine = fileLine.substring(1).trim();
                
                switch(linePropableType)
                {
                    case 'n': // event title case
                        eventName = fileLine;
                    ; break;
                    
                    case 'p': // a participant case
                        lineDatas = fileLine.split(",");
                        
                        participants.add(new Participant(Integer.parseInt(lineDatas[0]),lineDatas[1],lineDatas[2],Integer.parseInt(lineDatas[3]) ) );
                    ; break;
                    
                    case 'o': // an object case
                        lineDatas = fileLine.split(",");
                        
                        Participant owner = participants.get(Integer.parseInt(lineDatas[0]) );
                        
                        owner.addObject(new ParticipantObject(Integer.parseInt(lineDatas[1]),lineDatas[2],Double.parseDouble(lineDatas[3]) ) );
                    ; break;
                }
            }
            
            reader.close();
            
            System.out.println("je suis bien ici");
            
            return new GeneratorParseResult(participants,eventName);
        }
        catch(Exception e){System.out.println("je suis dans l'exception avec -> " + e.getMessage() + e.getStackTrace()[0].getLineNumber() );}
        
        System.out.println("je suis sortit on ne sait pourquoi");
        
        return new GeneratorParseResult();
    }
    
    /**
     * @see properties can be used only if getSuccesfultParsed is true
     */
    public class GeneratorParseResult
    {
        private ArrayList<Participant> participants;
        
        private String eventName;
        
        private boolean successfulyParsed;
        
        public GeneratorParseResult()
        {
            this.successfulyParsed = false;
        }
        
        public GeneratorParseResult(ArrayList<Participant> participants,String eventName)
        {
            this.participants = participants;
            this.eventName = eventName;
            this.successfulyParsed = true;
        }

        /**
         * Get the value of successfulyParsed
         *
         * @return the value of successfulyParsed
         */
        public boolean getSuccessfulyParsed()
        {
            return successfulyParsed;
        }

        /**
         * Get the value of eventName
         *
         * @return the value of eventName
         */
        public String getEventName()
        {
            return eventName;
        }

        /**
         * Get the value of participants
         *
         * @return the value of participants
         */
        public ArrayList<Participant> getParticipants()
        {
            return participants;
        }

    }
}
