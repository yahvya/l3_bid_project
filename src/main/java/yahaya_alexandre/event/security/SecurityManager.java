package yahaya_alexandre.event.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import yahaya_alexandre.event.auction.Auction;
import yahaya_alexandre.event.auction.Offer;

public class SecurityManager
{
    private MessageDigest hasher;

    private int sellerId;
    private int objectId;

    int transactionBlockId;

    private ArrayList<TransactionBlock> transactionsBlock;

    public SecurityManager(Auction linkedAuction) throws NoSuchAlgorithmException
    {
        this.hasher = MessageDigest.getInstance("SHA-256");
        this.sellerId = linkedAuction.getSeller().getId();
        this.objectId = linkedAuction.getObjectToSell().getObjectId();
        this.transactionBlockId = 0;
        this.transactionsBlock = new ArrayList<TransactionBlock>();

        linkedAuction.setSecurity(this);
    }
    
    /**
     * create a new block for the offer
     * @param offer
     */
    public void addOfferToTransactions(Offer offer)
    {
        this.transactionsBlock.add(new TransactionBlock(offer,this.sellerId,this.objectId,this.transactionBlockId,this.transactionBlockId == 0 ? null : this.transactionsBlock.get(this.transactionBlockId - 1),this.hasher) );

        this.transactionBlockId++;
    }

    /**
     * concat the given byte array by creating a new byte array
     * @param first
     * @param second
     * @return result of concatenation
     */
    public static byte[] concatBytes(byte[] first,byte[] second)
    {
        byte[] result = new byte[first.length + second.length];

        System.arraycopy(first,0,result,0,first.length);
        System.arraycopy(second,0,result,first.length,second.length);

        return result;
    }

    class TransactionBlock
    {
        private byte[] previousBlockHash;
        private byte[] blockHash;

        private int transactionBlockId;
        private int sellerId;
        private int objectId;
        
        private Offer blockOffer;

        public TransactionBlock(Offer blockOffer,int sellerId,int objectId,int transactionBlockId,TransactionBlock previousBlock,MessageDigest hasher)
        {
            this.previousBlockHash = previousBlock == null ? null : previousBlock.getBlockHash();
            this.sellerId = sellerId;
            this.objectId = objectId;
            this.transactionBlockId = transactionBlockId;
            this.blockOffer = blockOffer;   
            this.blockHash = this.buildHash(hasher);
        }

        /**
         * build the hash of this block
         * @param hasher
         * @return the hash
         */
        public byte[] buildHash(MessageDigest hasher)
        {
            // hash the concatenation of previoushash-transactionblockid-sellerid-objectId-buyerId-price-date
            byte[] hash = hasher.digest(String.join("",
                Integer.toString(this.transactionBlockId),
                Integer.toString(this.sellerId),
                Integer.toString(objectId),
                Integer.toString(this.blockOffer.getBuyer().getId() ),
                Double.toString(this.blockOffer.getPrice() ),
                this.blockOffer.getOfferDate().toString()
            ).getBytes(StandardCharsets.UTF_8) );

            if(this.previousBlockHash != null)
                hash = SecurityManager.concatBytes(this.previousBlockHash,hash);

            return hash;
        }
        
        /**
         * Get the value of previousBlockHash
         * @return the previous block hash
         */
        public byte[] getPreviousBlockHash()
        {
            return this.previousBlockHash;
        }

         /**
         * Get the value of blockHash
         * @return the block hash
         */
        public byte[] getBlockHash()
        {
            return this.blockHash;
        }
    }

    // trouver quel valeur additioné au hash donnera 20 0
}