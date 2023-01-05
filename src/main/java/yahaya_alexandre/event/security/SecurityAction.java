package yahaya_alexandre.event.security;

import yahaya_alexandre.event.security.SecurityManager.TransactionBlock;

public class SecurityAction 
{
    private ActionType action;

    private TransactionBlock[] transactionBlockGroup;

    private boolean alreadyPerformed;

    public SecurityAction(ActionType action)
    {
        this.action = action;
        this.transactionBlockGroup = null;
        this.alreadyPerformed = false;
    }

    public SecurityAction(ActionType action,TransactionBlock[] transactionBlockGroup)
    {
        this.action = action;
        this.transactionBlockGroup = transactionBlockGroup;
        this.alreadyPerformed = false;
    }

    /**
     * Get the value of action
     * @return action type
     */
    public ActionType getAction()
    {
        return this.action;
    }

    /**
     * Get the value of transactionBlockGroup
     * @return transactionBlock object or null if not
     */
    public TransactionBlock[] getTransactionBlockGroup()
    {
        return this.transactionBlockGroup;
    }

    /**
     * set the action as performed
     */
    synchronized public void setAsPerfomed()
    {
        this.alreadyPerformed = true;
    }

    /**
     * Get the value of alreadyPerformed
     * @return if this action is already performed
     */
    synchronized public boolean getAlreadyPerformed()
    {
        return this.alreadyPerformed;
    }

    public enum ActionType{STOP,VERIFY,SEARCH_PREFIX};    
}
