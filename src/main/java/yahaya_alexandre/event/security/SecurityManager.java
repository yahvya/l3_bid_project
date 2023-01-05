package yahaya_alexandre.event.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

import yahaya_alexandre.event.auction.Auction;
import yahaya_alexandre.event.auction.Offer;
import yahaya_alexandre.event.frame.EventViewer;
import yahaya_alexandre.event.frame.EventViewer.MessageType;
import yahaya_alexandre.event.participant.Participant;
import yahaya_alexandre.event.security.SecurityAction.ActionType;

public class SecurityManager
{
    public static final int COUNT_OF_BLOCK_BEFORE_VERIFICATION = 2;
    // greater reasonnable value for test mode
    public static final int COUNT_OF_ZERO = 3;
    public static final int MIN_MINERS = 2;
    public static final int MAX_MINERS = 4;

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

    private ArrayList<SecurityAction> incompleteActions;

    private boolean haveToStopAfterActions;

    public SecurityManager(Auction linkedAuction)
    {
        this.transactionBlockId = 0;
        this.transactionBlockGroupIndex = 0;
        this.haveToStopAfterActions = false;
        this.linkedAuction = linkedAuction;
        this.transactionBlockGroup = new TransactionBlock[SecurityManager.COUNT_OF_BLOCK_BEFORE_VERIFICATION];
        this.sellerId = linkedAuction.getSeller().getId();
        this.objectId = linkedAuction.getObjectToSell().getObjectId();
        this.transactionsBlock = new ArrayList<TransactionBlock>();
        this.confirmationsBlock = new ArrayList<ConfirmationBlock>();
        this.printerPage = linkedAuction.getPrinterPage();
        this.incompleteActions = new ArrayList<SecurityAction>();
        this.locker = new ReentrantLock();

        linkedAuction.setSecurity(this);
    }
    
    /**
     * create a new block for the offer
     * @param offer
     */
    public void addOfferToTransactions(Offer offer)
    {
        try
        {
            TransactionBlock newTransactionBlock = new TransactionBlock(offer,this.sellerId,this.objectId,this.transactionBlockId,this.transactionBlockId == 0 ? null : this.transactionsBlock.get(this.transactionBlockId - 1),MessageDigest.getInstance("SHA-256"));

            this.transactionsBlock.add(newTransactionBlock);
            this.transactionBlockId++;
            this.transactionBlockGroup[this.transactionBlockGroupIndex++] = newTransactionBlock;

            if(this.transactionBlockGroupIndex == SecurityManager.COUNT_OF_BLOCK_BEFORE_VERIFICATION)
            {
                SecurityAction todo = new SecurityAction(ActionType.VERIFY,this.transactionBlockGroup);

                this.incompleteActions.add(todo);

                // notify miners to verify this group of transaction block
                for(Miner m : this.miners)
                    m.receiveActionToPerform(todo);

                // place the last element of the group in the start of the array to verify because the miners can't verify this block in this turn

                this.transactionBlockGroupIndex = 1;

                this.transactionBlockGroup = new TransactionBlock[SecurityManager.COUNT_OF_BLOCK_BEFORE_VERIFICATION];

                this.transactionBlockGroup[0] = newTransactionBlock;
            }
        }
        catch(Exception e){}
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
        if(this.incompleteActions.size() == 0)
            this.endAuction();
        else 
            this.haveToStopAfterActions = true;
    }

    /**
     * complete end auction action
     */
    private void endAuction()
    {
        for(Miner m : this.miners)
            m.receiveActionToPerform(new SecurityAction(ActionType.STOP) );

        this.printerPage.printMessage(String.join(" ","fin de la vente de la vente de l'objet",this.linkedAuction.getObjectToSell().getName() ),MessageType.STATE);
    }

    /**
     * add a transaction group as verified from miners if success
     * @param transactionBlocks
     * @param isOk
     * @param baseAction
     */
    public void receiveTransactionBlockGroupVerificationConfirmation(TransactionBlock[] transactionBlockGroup,boolean isOk,SecurityAction baseAction)
    {   
        this.locker.lock();

        // ignore if already performed
        if(baseAction.getAlreadyPerformed() )
        {
            this.printerPage.printMessage("La vérification d'un mineur vient d'être ignoré car déjà complété par un autre",MessageType.MINER);

            return;
        }

        // check if this block is not already verified

        if(isOk)
        {
            this.printerPage.printMessage("Un mineur vient de finir la vérification d'un paquet",MessageType.MINER);

            baseAction.setAsPerfomed();

            this.incompleteActions.remove(baseAction);

            // set blocks as verified
            for(TransactionBlock t : transactionBlockGroup)
                t.setAsVerified();

            try
            {
                // notify miners to find the prefix of the verifed group
                SecurityAction todo = new SecurityAction(ActionType.SEARCH_PREFIX,transactionBlockGroup);

                this.incompleteActions.add(todo);

                for(Miner m : this.miners)
                    m.receiveActionToPerform(todo);
            }
            catch(Exception e){}

            if(this.haveToStopAfterActions && this.incompleteActions.size() == 0)
                this.endAuction();
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
     * add a group in the second chain with the prefix from miners if success
     * @param transactionBlocksGroup
     * @param prefix
     * @param baseAction
     * @param verificator
     */
    public void receivedTransactionBlockGroupPrefixConfirmation(TransactionBlock[] transactionBlocksGroup,int prefix,SecurityAction baseAction,Miner verificator)
    {
        this.locker.lock();

        // ignore if already performed
        if(baseAction.getAlreadyPerformed() || prefix == -1)
        {
            this.printerPage.printMessage("Un préfix d'un mineur vient d'être ignoré car déjà complété par un autre",MessageType.MINER);

            return;
        }

        int currentIndex = this.confirmationsBlock.size();

        double workMoney = 300;

        try
        {     
            ConfirmationBlock confirmationBlock = new ConfirmationBlock(transactionBlocksGroup,currentIndex != 0 ? this.confirmationsBlock.get(currentIndex - 1).getBlockHash() : null,MessageDigest.getInstance("SHA-256"),verificator,workMoney,prefix);

            Participant miner = verificator.getMiner();

            this.printerPage.printMessage(String.join(" ","envoie de",Double.toString(workMoney),"€ au mineur",miner.getFname(),miner.getName(),"pour le travail de recherche de préfix"),MessageType.MONEY_TRANSMISSION);

            confirmationBlock.transferMoneyToMiner();

            this.confirmationsBlock.add(confirmationBlock);

            baseAction.setAsPerfomed();

            this.incompleteActions.remove(baseAction);

            if(this.haveToStopAfterActions && this.incompleteActions.size() == 0)
                this.endAuction();
        }
        catch(Exception e){}

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

    /**
     * create a hash by concatenate the hash of all transactions block
     * @param transactionBlockGroup
     * @param hasher
     * @return hash
     */
    public static byte[] buildHashFromGroup(TransactionBlock[] transactionBlockGroup,MessageDigest hasher)
    {
        byte[] blockHash = transactionBlockGroup[0].getBlockHash();

        int size = transactionBlockGroup.length;

        for(int index = 1; index < size; index++)
            blockHash = SecurityManager.concatBytes(blockHash,transactionBlockGroup[index].getBlockHash() );

        return blockHash;
    }

    class TransactionBlock
    {
        private byte[] previousBlockHash;
        private byte[] blockHash;

        private int transactionBlockId;
        private int sellerId;
        private int objectId;
        
        private Offer blockOffer;

        private boolean isVerified;

        public TransactionBlock(Offer blockOffer,int sellerId,int objectId,int transactionBlockId,TransactionBlock previousBlock,MessageDigest hasher)
        {
            this.previousBlockHash = previousBlock == null ? null : previousBlock.getBlockHash();
            this.sellerId = sellerId;
            this.objectId = objectId;
            this.transactionBlockId = transactionBlockId;
            this.blockOffer = blockOffer;   
            this.isVerified = false;
            this.blockHash = this.buildHash(hasher);
        }

        /**
         * set this block as verifed
         */
        synchronized public void setAsVerified()
        {
            this.isVerified = true;
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
        
        /**
         * Get the value of isVerified
         * @return if this block is verified
         */
        synchronized public boolean getIsVerified()
        {
            return this.isVerified;
        }
    }

    class ConfirmationBlock
    {
        private TransactionBlock[] containedTransactionsBlock;

        private byte[] previousBlockHash;
        private byte[] blockHash;

        private Miner minerWitchFound;

        private double workMoney;

        private int prefix;

        public ConfirmationBlock(TransactionBlock[] containedTransactionsBlock,byte[] previousBlockHash,MessageDigest hasher,Miner minerWitchFound,double workMoney,int prefix)
        {
            this.containedTransactionsBlock = containedTransactionsBlock;
            this.previousBlockHash = previousBlockHash;
            this.minerWitchFound = minerWitchFound;
            this.workMoney = workMoney;
            this.prefix = prefix;
            this.blockHash = SecurityManager.buildHashFromGroup(containedTransactionsBlock,hasher);
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
        public TransactionBlock[] getContainedTransactionsBlock()
        {
            return this.containedTransactionsBlock;
        }

        /**
         * Get the value of workMoney
         * @return workMoney
         */
        public double getWorkMoney()
        {
            return this.workMoney;
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

        /**
         * Get the value of prefix
         * @return
         */
        public int getPrefix()
        {
            return this.prefix;
        }   
    }
}