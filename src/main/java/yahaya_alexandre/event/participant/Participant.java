package yahaya_alexandre.event.participant;

import java.util.ArrayList;

/**
 *
 * @author yahayab
 */
public class Participant
{
    private String name;
    private String fname;
    
    private ArrayList<ParticipantObject> objectList;
    
    private int id;
    
    private double money;
    
    public Participant(int id,String name,String fname,double money,ArrayList<ParticipantObject> objectList)
    {
        this.id = id;
        this.name = name;
        this.fname = fname;
        this.money = money;
        this.objectList = objectList;
    }
    
    public Participant(int id,String name,String fname,double money)
    {
        this.id = id;
        this.name = name;
        this.fname = fname;
        this.money = money;
        this.objectList = new ArrayList<ParticipantObject>();
    }
      
    /**
     * increase the participant money
     * @param moneyToAdd amount of money to add
     * @return self
     */
    public Participant increaseMoney(double moneyToAdd)
    {
       this.money += moneyToAdd;
        
        return this;
    }
       
    /**
     * decrease the participant money
     * @param moneyToRemove amount of money to remove
     * @return self
     */
    public Participant decreaseMoney(double moneyToRemove)
    {
        this.money -= moneyToRemove;
        
        return this;
    }
    
    /**
     * decrease the participant money if the result is superior or equal to 0 else set the money to 0
     * @param moneyToRemove amount of money to remove
     * @param checkIfNotNeg if this param is present the check will be active
     * @return self
     */
    public Participant decreaseMoney(double moneyToRemove,boolean checkIfNotNeg)
    {
        this.money = this.money - moneyToRemove >= 0 ? this.money - moneyToRemove : 0;
        
        return this;
    }
    
    /**
     * try to tranfer the given object to the participant if this possess
     * @param objectToTransfer the object to transfer
     * @param to the receiver
     * @param takeMoneyWith if true this money will be increased by object price and receiver will lost some money (dicrease without check will be called)
     * @return self
     */
    public Participant transferObjectTo(ParticipantObject objectToTransfer,Participant to,boolean takeMoneyWith)
    {   
        if(this.objectList.contains(objectToTransfer) )
        {
            this.objectList.remove(objectToTransfer);
            to.objectList.add(objectToTransfer);
            
            if(takeMoneyWith)
            {
                double objectPrice = objectToTransfer.getPrice();
                
                this.increaseMoney(objectPrice);
                to.decreaseMoney(objectPrice);
            }
        }
        
        return this;
    }
      
    /**
     * try to add the object to participant objects list 
     * @param object object to add
     * @return self
     */
    public Participant addObject(ParticipantObject object)
    {
        this.objectList.add(object);
        
        return this;
    }
    
    /**
     * try to add the object to participant objects list
     * @param object object to add
     * @param checkIfExist if set check if the object already exist before add in list
     * @return self
     */
    public Participant addObject(ParticipantObject object,boolean checkIfExist)
    {
        if(!this.objectList.contains(object) )
            this.objectList.add(object);
        
        return this;
    }
    
    // getters

    /**
     * Get the value of money
     *
     * @return the value of money
     */
    public double getMoney()
    {
        return money;
    }

    /**
     * Get the value of objectList
     *
     * @return the value of objectList
     */
    public ArrayList<ParticipantObject> getObjectList()
    {
        return objectList;
    }

    /**
     * Get the value of fname
     *
     * @return the value of fname
     */
    public String getFname()
    {
        return fname;
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
     * get of the value of id
     * @return id
     */
    public int getId()
    {
        return id;
    }
}
