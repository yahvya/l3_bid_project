package yahaya_alexandre.event.tools;

import yahaya_alexandre.event.event.Event;
import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import com.github.javafaker.Commerce;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import yahaya_alexandre.event.exception.NotEnoughParticipants;

/**
 *
 * @author yahayab
 */
public abstract class EventGenerator
{
    public static String[] instructions =
    {
        "c fichier représentant un évenement",
        "c description du format",
        "c les lignes commençant par c sont des commentaires ignorés lors du parsage",
        "c les lignes commençant par p sont des personnes sous le format(id,nom,prénom,argent)",
        "c les lignes commençant par o sont des objets sous le format (id_objet,id_proprietaire,nom_objet,prix)",
        "c les lignes commençant par n définissent le nom de l'évenement"
    };

    /**
     * permet de générer une fichier évenement
     *
     * @eventFilePath chemin du fichier évenement
     * @eventName nom de l'évenement
     * @countOfParticipant nombre de participants à l'évenement
     * @return true si la création réussi ou faux sinon
     */
    public static boolean generateEvent(String eventFilePath, String eventName, int countOfParticipants) throws NotEnoughParticipants
    {
        if(countOfParticipants < Event.MINIMUM_PARTICIPANTS)
              throw new NotEnoughParticipants(countOfParticipants);
            
        try
        {
            FileWriter writer = new FileWriter(eventFilePath);

            Faker faker = new Faker();

            Random random = new Random();

            // écriture des instructions dans le fichier
            writer.write(String.join("\n", EventGenerator.instructions) );
            
            // écriture du nom de l'évenement
            writer.write("\nt ".concat(eventName).concat("\n") );

            // écriture des participants
            for (int i = 0; i < countOfParticipants; i++)
            {
                Name participantNameData = faker.name();

                writer.write("p ".concat(String.join(",", Integer.toString(i + 1), participantNameData.lastName(), participantNameData.name(),Integer.toString(faker.number().numberBetween(2000, 6000) ) ) ).concat("\n") );
            }
           
            // écriture des objets
            int countOfObjects = random.nextInt(15,40);
            
            for(int i = 0; i < countOfObjects; i++)
            {
                Commerce objectCommerce = faker.commerce();
                
                writer.write("o ".concat(String.join(",",Integer.toString(random.nextInt(1,countOfParticipants) ),Integer.toString(i + 1),objectCommerce.productName(),objectCommerce.price() ) ).concat("\n") );
            }
            
            writer.close();
            
            return true;
        }
        catch(NullPointerException e){}
        catch(IOException e){}

        return false;
    }
}
