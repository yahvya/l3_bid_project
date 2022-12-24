package yahaya_alexandre.event.exception;

import yahaya_alexandre.event.event.Event;

/**
 *
 * @author yahayab
 */
public class NotEnoughParticipants extends Exception
{
    private int countOfMissedParticipants;
    
    public NotEnoughParticipants(int givenCountOfParticipants)
    {
        super("not enough paricipants");
        
        this.countOfMissedParticipants = Event.MINIMUM_PARTICIPANTS - givenCountOfParticipants;
    }
    
    public int getCountOfMissedParticipants()
    {
        return this.countOfMissedParticipants;
    }
}
