/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package yahaya_alexandre.event.auction;

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
     * Get the value of buyer
     *
     * @return the value of buyer
     */
    public Participant getBuyer()
    {
        return buyer;
    }

}
