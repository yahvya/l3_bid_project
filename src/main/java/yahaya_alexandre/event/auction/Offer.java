package yahaya_alexandre.event.auction;

import java.util.Random;
import yahaya_alexandre.event.participant.Participant;

/**
 *
 * @author yahayab
 */
public class Offer
{
    
    private Participant buyer;
    
    private double price;
    
    public Offer(Participant buyer,double price)
    {
        this.buyer = buyer;
        this.price = price;
    }
    
    /**
     * check if the given offer is better than this
     * @param offer
     * @return true if better of false
     */
    public boolean offerIsBetter(Offer offer)
    {
        return offer.getPrice() > this.price;
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
     * return a random offer from a potentiel buyer
     * @param probableBuyer
     * @param random
     * @return 
     */
    public static Offer simulateFrom(Participant probableBuyer,Random random)
    {
        Double buyerMoney = probableBuyer.getMoney();
        
        return new Offer(probableBuyer,buyerMoney < 1 ? 0 : random.nextDouble(1,buyerMoney) );
    }
    
    /**
     * return a random offer from a potentiel buyer
     * @param probableBuyer
     * @return 
     */
    public static Offer simulateFrom(Participant probableBuyer)
    {   
        return Offer.simulateFrom(probableBuyer,new Random() );
    }


    /**
     * Get the value of buyer
     *
     * @return the value of buyer
     */
    public Participant getBuyer()
    {
        return buyer;
    }

}
