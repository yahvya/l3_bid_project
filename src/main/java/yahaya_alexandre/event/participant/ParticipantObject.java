package yahaya_alexandre.event.participant;

import java.io.Serializable;

/**
 *
 * @author yahayab
 */
public class ParticipantObject implements Serializable
{
    private String name;
    
    private double price;
    
    private int objectId;
    
    public ParticipantObject(int objectId,String name,double price)
    {
        this.name = name;
        this.price = price;
        this.objectId = objectId;
    }

    /**
     * Get the value of price
     *
     * @return the value of price
     */
    public double getPrice()
    {
        return price;
    }

    /**
     * Get the value of name
     *
     * @return the value of name
     */
    public String getName()
    {
        return name;
    }
    
    /**
     * Get the value of objectId
     * @return the value of objectId
     */
    public int getObjectId()
    {
        return objectId;
    }

    
}
