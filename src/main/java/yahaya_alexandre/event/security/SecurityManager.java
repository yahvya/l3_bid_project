package yahaya_alexandre.event.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

import javax.crypto.SecretKey;

import yahaya_alexandre.event.auction.Auction;
import yahaya_alexandre.event.auction.Offer;
import yahaya_alexandre.event.frame.EventViewer;
import yahaya_alexandre.event.frame.EventViewer.MessageType;
import yahaya_alexandre.event.participant.Participant;
import yahaya_alexandre.event.security.SecurityAction.ActionType;

public class SecurityManager
{
    public static final int COUNT_OF_BLOCK_BEFORE_VERIFICATION = 2;
    public static final int COUNT_OF_ZERO = 3;
    public static final int MIN_MINERS = 2;
    public static final int MAX_MINERS = 4;

    private MessageDigest hasher;

    private int sellerId;
    private int objectId;

    private int transactionBlockId;

    private Auction linkedAuction;

    private ArrayList<TransactionBlock> transactionsBlock;
    private ArrayList<ConfirmationBlock> confirmationsBlock;

    private EventViewer printerPage;

    private Miner[] miners;

    private TransactionBlock[] transactionBlockGroup;

    private int transactionBlockGroupIndex;

    private ReentrantLock locker;

    public SecurityManager(Auction linkedAuction) throws NoSuchAlgorithmException
    {
        this.transactionBlockId = 0;
        this.transactionBlockGroupIndex = 0;
        this.linkedAuction = linkedAuction;
        this.transactionBlockGroup = new TransactionBlock[SecurityManager.COUNT_OF_BLOCK_BEFORE_VERIFICATION];
        this.hasher = MessageDigest.getInstance("SHA-256");
        this.sellerId = linkedAuction.getSeller().getId();
        this.objectId = linkedAuction.getObjectToSell().getObjectId();
        this.transactionsBlock = new ArrayList<TransactionBlock>();
        this.confirmationsBlock = new ArrayList<ConfirmationBlock>();
        this.printerPage = linkedAuction.getPrinterPage();
        this.locker = new ReentrantLock();

        linkedAuction.setSecurity(this);
    }
    
    /**
     * create a new block for the offer
     * @param offer
     */
    public void addOfferToTransactions(Offer offer)
    {
        TransactionBlock newTransactionBlock = new TransactionBlock(offer,this.sellerId,this.objectId,this.transactionBlockId,this.transactionBlockId == 0 ? null : this.transactionsBlock.get(this.transactionBlockId - 1),this.hasher);

        this.transactionsBlock.add(newTransactionBlock);
        this.transactionBlockId++;
        this.transactionBlockGroup[this.transactionBlockGroupIndex++] = newTransactionBlock;

        if(this.transactionBlockGroupIndex == SecurityManager.COUNT_OF_BLOCK_BEFORE_VERIFICATION)
        {
            SecurityAction todo = new SecurityAction(ActionType.VERIFY,this.transactionBlockGroup,this.hasher);

            // notify miners to verify this group of transaction block
            for(Miner m : this.miners)
                m.receiveActionToPerform(todo);

            // place the last element of the group in the start of the array to verify because the miners can't verify this block in this turn

            this.transactionBlockGroupIndex = 1;

            this.transactionBlockGroup = new TransactionBlock[SecurityManager.COUNT_OF_BLOCK_BEFORE_VERIFICATION];

            this.transactionBlockGroup[0] = newTransactionBlock;
        }
    }

    /**
     * set the linked auction as started, create miners
     */
    public void setAuctionAsStarted()
    {
        Random random = new Random();
        
        ArrayList<Participant> linkedAuctionParticipants = this.linkedAuction.getParticipants();
        
        int countOfMiners = random.nextInt(SecurityManager.MIN_MINERS,SecurityManager.MAX_MINERS);
        int participantsIndex = linkedAuctionParticipants.size() - 1;

        boolean alreadyAMiner;
        
        this.miners = new Miner[countOfMiners];

        for(int index = 0; countOfMiners > 0; countOfMiners--,index++)
        {
            Participant miner;

            // get a new miner in participants
            do
            {
                miner = linkedAuctionParticipants.get(random.nextInt(0,participantsIndex) );
                
                alreadyAMiner = false;

                for(Miner m : this.miners)
                {
                    if(m == null)
                        break;

                    if(m.getMiner() == miner)
                    {
                        alreadyAMiner = true;
                        break;
                    }
                }
            }
            while(alreadyAMiner);

            this.miners[index] = new Miner(miner,this.printerPage,this.linkedAuction,this);
        }        

        // start miners thread
        for(Miner m : this.miners)
            m.start();
    }
    
    /**
     * set the linked auction as finished , stop miners
     */
    public void setAuctionAsFinished()
    {
        for(Miner m : this.miners)
            m.receiveActionToPerform(new SecurityAction(ActionType.STOP) );
    }

    /**
     * add a transaction group as verified from miners if success
     * @param transactionBlocks
     */
    public void receiveTransactionBlockGroupVerificationConfirmation(TransactionBlock[] transactionBlockGroup,boolean isOk,SecurityAction baseAction)
    {   
        this.locker.lock();

        // ignore if already performed
        if(baseAction.getAlreadyPerformed() )
        {
            this.printerPage.printMessage("Une action d'un mineur vient d'être ignoré car déjà complété par un autre",MessageType.MINER);

            return;
        }

        // check if this block is not already verified

        if(isOk)
        {
            this.printerPage.printMessage("Un mineur vient de finir la vérification d'un paquet",MessageType.MINER);

            // ajout aux groupes à faire

            baseAction.setAsPerfomed();
        }
        else
        {
            // stop the auction cause something is wrong

            this.linkedAuction.setStopThread(true);

            this.printerPage.printMessage(String.join(" ","Un mineur à trouvé une erreur lors d'une vérification, arrêt de la vente"),MessageType.FAILURE);
        }

        this.locker.unlock();
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
         * help to test if miners verification are correct
         */
        synchronized public void changeSomething(Random random)
        {  
            switch(random.nextInt(1,4) )
            {
                case 1:
                    this.sellerId = -1;
                ; break;  
                
                case 2:
                    this.objectId = -1;
                ; break;

                case 3:
                    this.transactionBlockId = -1;
                ; break;

                case 4:
                    this.blockOffer = new Offer(this.blockOffer.getBuyer(),-1);   
                ; break;
            }
        }

        /**
         * build the hash of this block
         * @param hasher
         * @return the hash
         */
        synchronized public byte[] buildHash(MessageDigest hasher)
        {
            // hash the concatenate value of previoushash-transactionblockid-sellerid-objectId-buyerId-price-date
            byte[] hash = hasher.digest(String.join("",
                Integer.toString(this.transactionBlockId),
                Integer.toString(this.sellerId),
                Integer.toString(this.objectId),
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
        synchronized public byte[] getPreviousBlockHash()
        {
            return this.previousBlockHash;
        }

         /**
         * Get the value of blockHash
         * @return the block hash
         */
        synchronized public byte[] getBlockHash()
        {
            return this.blockHash;
        }
    }

    class ConfirmationBlock
    {
        private TransactionBlock[] containedTransactions;

        private byte[] previousBlockHash;
        private byte[] blockHash;

        private Miner minerWitchFound;

        private Double workMoney;

        private ArrayList<TransactionBlock> containedTransactionsBlock;

        public ConfirmationBlock(ArrayList<TransactionBlock> containedTransactionsBlock,byte[] previousBlockHash,MessageDigest hasher)
        {
            this.containedTransactionsBlock = containedTransactionsBlock;
            this.previousBlockHash = previousBlockHash;

            // this hash is the hash value of the concatenation of all transactionblock hash
            this.blockHash = containedTransactionsBlock.get(0).getBlockHash();
            
            for(TransactionBlock b : containedTransactionsBlock.subList(1,containedTransactionsBlock.size() ) )
                this.blockHash = SecurityManager.concatBytes(this.blockHash,b.getBlockHash() );

            if(previousBlockHash != null)
                this.blockHash = SecurityManager.concatBytes(previousBlockHash,this.blockHash);

            this.blockHash = hasher.digest(this.blockHash);
        }

        /**
         * send the work money to the miner
         */
        public void transferMoneyToMiner()
        {
            this.minerWitchFound.receiveMoney(this.workMoney);
        }

        /**
         * Get the value of containedTransactionsBlock
         * @return contained transactions list
         */
        public ArrayList<TransactionBlock> getContainedTransactionsBlock()
        {
            return this.containedTransactionsBlock;
        }

        /**
         * Get the value of blockHash 
         * @return the hash of this block
         */
        public byte[] getBlockHash()
        {
            return this.blockHash;
        }

        /**
         * Get the value of previousBlockHash
         * @return the previous block hash or null if first element
         */
        public byte[] getPreviousBlockHash()
        {
            return this.previousBlockHash;
        }
    }
}