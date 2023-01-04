package yahaya_alexandre.event.security;

import java.security.MessageDigest;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

import yahaya_alexandre.event.auction.Auction;
import yahaya_alexandre.event.frame.EventViewer;
import yahaya_alexandre.event.frame.EventViewer.MessageType;
import yahaya_alexandre.event.participant.Participant;
import yahaya_alexandre.event.security.SecurityAction.ActionType;
import yahaya_alexandre.event.security.SecurityManager.TransactionBlock;

public class Miner extends Thread
{
    public static final int MAX_ACTIONS = 20;
    public static final boolean ACTIVE_TEST = false;

    private Participant miner;
    
    private EventViewer printerPage;

    private Auction linkedAuction;

    private SecurityManager security;

    private ReentrantLock locker;

    private ArrayBlockingQueue<SecurityAction> actionsToPerform;

    public Miner(Participant miner,EventViewer printerPage,Auction linkedAuction,SecurityManager security)
    {
        this.miner = miner;
        this.printerPage = printerPage;
        this.linkedAuction = linkedAuction;
        this.security = security;
        this.locker = new ReentrantLock();
        this.actionsToPerform = new ArrayBlockingQueue<SecurityAction>(Miner.MAX_ACTIONS);
    }

    @Override
    public void run()
    {
        String objectName = this.linkedAuction.getObjectToSell().getName();

        this.printerPage.printMessage(String.join(" ","Lancement de <<",this.miner.getName(),this.miner.getFname(),">> comme mineur sur la vente de l'objet",objectName),MessageType.MINER);

        try
        {
            boolean exit = false;

            Random random = new Random();

            while(!exit)
            {
                // get the action to perform or wait until an action come
                SecurityAction actionToPerform = this.actionsToPerform.take();

                // ignore the action if already performed by another one
                if(actionToPerform.getAlreadyPerformed() )
                    continue;

                switch(actionToPerform.getAction() )
                {
                    case VERIFY:
                        this.printerPage.printMessage(String.join(" ","Les mineurs ont recu un nouveau paquet à vérifier sur l'objet",objectName),MessageType.MINER);

                        TransactionBlock[] groupToVerify = actionToPerform.getTransactionBlockGroup();
                        
this.security.receiveTransactionBlockGroupVerificationConfirmation(groupToVerify,this.verifyTransactionBlockGroup(groupToVerify,actionToPerform.getHasher(),random),actionToPerform);
                    ; break;

                    default:
                        exit = true;
                    ;
                }
            }

            this.printerPage.printMessage(String.join(" ","Mineur <<",this.miner.getName(),this.miner.getFname(),">> se déconnecte de la vente sur l'objet",objectName),MessageType.MINER);
        }
        catch(Exception e)
        {
            this.printerPage.printMessage("erreur sur le mineur déconnexion",MessageType.MINER);
        }
    }

    /**
     * verify a group of transaction and return if ok or not
     * @param transactionBlockGroup
     * @return
     */
    public boolean verifyTransactionBlockGroup(TransactionBlock[] transactionBlockGroup,MessageDigest hasher,Random random)
    {   
        int size = transactionBlockGroup.length;

        for(int i = 0; i < size - 1; i++)
        {
            TransactionBlock transactionBlock = transactionBlockGroup[i];

            // if test mode is active the miner will change the offer price in one block so the verification have to faill
            if(Miner.ACTIVE_TEST)
            {
                if(!random.nextBoolean() )
                {
                    this.printerPage.printMessage("Le mineur a décidé de changer une valeur pour le test",MessageType.MINER);

                    transactionBlock.changeSomething(random);
                }
            }
            
            // get the block current hash to compare him with the saved hash in the next block
            byte[] thisHash = transactionBlock.buildHash(hasher);
            byte[] saveHash = transactionBlockGroup[i + 1].getPreviousBlockHash();

            int hashSize = thisHash.length;

            if(hashSize != saveHash.length)
                return false;

            for(int index = 0; index < hashSize; index++)
            {
                if(thisHash[index] != saveHash[index])
                    return false;
            }
        }

        return true;
    }

    /**
     * add in the internal queue the action to perform
     * @param action
     */
    public void receiveActionToPerform(SecurityAction action) 
    {
        if(action.getAction() == ActionType.STOP)
            this.actionsToPerform.clear();
        
        try
        {
            this.actionsToPerform.put(action);
        }
        catch(Exception e){}
    }

    /**
     * add the given money to the miner account
     * @param money
     */
    public void receiveMoney(Double money)
    {
        this.locker.lock();
        this.miner.increaseMoney(money);
        this.locker.unlock();    
    }

    /**
     * Get the value of miner
     * @return the linked particioant
     */
    public Participant getMiner()
    {
        return this.miner;
    }
}
