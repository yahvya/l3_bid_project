package yahaya_alexandre.event.security;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import yahaya_alexandre.event.auction.Offer;
import yahaya_alexandre.event.frame.EventViewer;
import yahaya_alexandre.event.frame.EventViewer.MessageType;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author yahayab
 */
public class SecurityMap
{
    public static final int COUNT_BEFORE_VERIFY = 2;

    private String hash;
    
    private MessageDigest hasher;
    
    private ObjectOutputStream encoder;
    
    private ByteArrayOutputStream byteArray;

    private ArrayList<SecurityBlock> blocks;

    private EventViewer printerPage;

    private String sellData;

    private int lastVerifiedBlockIndex;
    
    public SecurityMap(EventViewer printerPage) throws NoSuchAlgorithmException,IOException,SecurityException
    {
        this.hasher = MessageDigest.getInstance("SHA-256");
        this.byteArray = new ByteArrayOutputStream();
        this.encoder = new ObjectOutputStream(this.byteArray);
        this.blocks = new ArrayList<SecurityBlock>();
        this.printerPage = printerPage;
        this.hash = "";
        this.lastVerifiedBlockIndex = 0;
    }

    /**
     * add the offer in the block
     */
    public void addInMap(Offer offer) throws IOException
    {
        this.encoder.writeObject(offer);

        this.hash = this.hash.concat(new String(this.hasher.digest(this.byteArray.toByteArray() ) ) );

        this.blocks.add(new SecurityBlock(offer,this.hash) );

        System.out.println("byte array -> " + this.byteArray.toByteArray() );
    }

    /**
     * set the value of sellData
     * @param sellData
     */
    public void setSellData(String sellData)
    {
        this.sellData = sellData;
    }

    class SecurityBlock
    {
        private Offer offer;

        String hash;

        public SecurityBlock(Offer offer,String hash)
        {
            this.offer = offer;
            this.hash = hash;
        }

        /**
         * Get the value of offer
         * @return offer
         */
        public Offer getOffer()
        {
            return this.offer;
        }

        /**
         * Get the value of hash
         * @return hash
         */
        public String getHash()
        {
            return this.hash;
        }
    }
}
