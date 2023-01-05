package yahaya_alexandre.event.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

            TransactionBlock[] groupToVerify;

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
                        this.printerPage.printMessage(String.join(" ","Le mineur <<",this.miner.getFname(),this.miner.getName(),">> a recu un nouveau paquet à vérifier sur l'objet",objectName),MessageType.MINER);

                        groupToVerify = actionToPerform.getTransactionBlockGroup();
                        
this.security.receiveTransactionBlockGroupVerificationConfirmation(groupToVerify,this.verifyTransactionBlockGroup(groupToVerify,random),actionToPerform);
                    ; break;

                    case SEARCH_PREFIX:
                        this.printerPage.printMessage(String.join(" ","Le mineur <<",this.miner.getFname(),this.miner.getName(),">> a recu un nouveau paquet dont il doit trouver le préfixe sur l'objet",objectName),MessageType.MINER);

                        groupToVerify = actionToPerform.getTransactionBlockGroup();

                        this.security.receivedTransactionBlockGroupPrefixConfirmation(groupToVerify,this.searchPrefix(groupToVerify,actionToPerform),actionToPerform,this);
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
            // System.out.println(e);
            e.printStackTrace();
        }
    }

    /**
     * verify a group of transaction and return if ok or not
     * @param transactionBlockGroup
     * @return
     */
    public boolean verifyTransactionBlockGroup(TransactionBlock[] transactionBlockGroup,Random random)
    {   
        try
        {
            MessageDigest hasher = MessageDigest.getInstance("SHA-256");

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
        }
        catch(NoSuchAlgorithmException e)
        {
            return false;
        }

        return true;
    }

    /**
     * search a prefix for the group
     * @param transactionBlockGroup
     * @param hasher
     * @return the prefix or -1 if action already performed or error
     */
    public int searchPrefix(TransactionBlock[] transactionBlockGroup,SecurityAction baseAction)
    {
        try
        {
            int toAdd = 0;

            MessageDigest hasher = MessageDigest.getInstance("SHA-256");

            byte[] hashFromGroup = SecurityManager.buildHashFromGroup(transactionBlockGroup,hasher);

            while(true)
            {   
                // create a new hash
                byte[] newHash = hasher.digest(SecurityManager.concatBytes(Integer.toString(toAdd).getBytes(),hashFromGroup) );

                int size = newHash.length;

                // check the number of need zero
                for(int i = 0; i < SecurityManager.COUNT_OF_ZERO && i < size; i++)
                {
                    if(newHash[i] != 0)
                        break;

                    if(i + 1 == SecurityManager.COUNT_OF_ZERO)
                        return toAdd;
                }

                // exit loop if the action already performed
                if(baseAction.getAlreadyPerformed() )
                    break;

                toAdd++;
            }
        }
        catch(NoSuchAlgorithmException e){}

        return -1;
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
