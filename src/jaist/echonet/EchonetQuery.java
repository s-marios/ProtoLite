package jaist.echonet;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * The resulting object of an echonet query. Used for synchronization purposes
 * and to access answers related to this query in a synchronous, serialized 
 * manner.
 * 
 * @author Sioutis Marios
 * @see EchonetAnswer
 */
public class EchonetQuery extends ArrayList<EchonetAnswer> implements Query{
    /**
     * 
     */
    public AbstractEchonetObject querysource = null;
    
    private boolean processed = false;
    private long when = System.currentTimeMillis();
    private EchoEventListener listener;
    private ServiceCode code;
    
    EchonetQuery(ServiceCode code,AbstractEchonetObject parent, EchoEventListener listener){
        this.querysource = parent;
        this.listener = listener;
        this.code = code;
    }
    
    /**
     * Returns the query echonet object that represents the target of this query
     * @return the target echonet object 
     */
    public AbstractEchonetObject getSource(){
        return this.querysource;
    }

    /**
     * Gets the number of responses available
     * @return the number of available responses
     */
    @Override
    public int getResponseAvailable() {
        return this.size();
    }
    
    /**
     * Returns if this query has been processed
     * @return true if it has been processed, false otherwise
     */
    @Override
    public boolean getProcessed() {
        return this.processed;
    }

    /**
     * Gets the submission time of this query
     * @return the submission time as a long
     */
    @Override
    public long getSubmissionTime() {
        return when;
    }

    /**
     * Sets the processed status of this query
     */
    @Override
    public void setProcessed() {
        this.processed = true;
    }
    
    /**
     * Set this query as "not processed"
     */
    @Override
    public void setUnprocessed() {
        this.processed = false;
    }

    /**
     * Sets the submission time
     * @param when the submission time as a long
     */
    @Override
    public void setSubmissionTime(long when) {
        this.when = when;
    }

    /**
     * Gets the associated time out value for this query
     * @return the timeout value as a long
     */
    @Override
    public long getTimoutInMillis() {
        //TODO this is just plain wrong, should have different timeouts for
        //unicasts and multicasts
        return EchonetProtocol.AWAITRESPONSEMULTICAST;
    }

    /**
     * Returns if this query has expired
     * @return true if this query has expired, false otherwise
     */
    @Override
    public boolean hasExpired() {
        return (System.currentTimeMillis() - this.when > getTimoutInMillis())?  true :  false;
    }
    
    /**
     * A BLOCKING method that may return <ul>
     * <li> the next answer associated with this query</li>
     * <li> null if there are no more answers and the query has expired</>
     * </ul>
     * @return a {@link EchonetAnswer object} or null
     */
    public synchronized EchonetAnswer getNextAnswer(){
        waitForAnAnswer();
        return getNextAnswerNonBlocking();
    }
    
    /**
     * Returns the next answer associated with this query if there is an answer
     * available, or null if no answer is available without blocking
     * @return an answer, or null if no answer is available
     */
    public synchronized EchonetAnswer getNextAnswerNonBlocking(){
        EchonetAnswer answer = null;
        if(!this.isEmpty())
        {
            answer = this.get(0);
            this.remove(answer);
        }
        return answer;
    }
    
    private synchronized void waitForAnAnswer(){
          
            try {
                while(this.isEmpty()) {
                    long elapsedtime = System.currentTimeMillis() - when;
                    if(elapsedtime < EchonetProtocol.AWAITRESPONSE)
                        this.wait(EchonetProtocol.AWAITRESPONSE - elapsedtime);
                        
                    else
                        return;// timeout occured
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(RemoteEchonetObject.class.getName()).log(Level.SEVERE, null, ex);
                }
    }
    
    /**
     * Returns the asynchronous answer listener associated with this query
     * @return the asynchronous answer listener associated with this query
     */
    public EchoEventListener getListener(){
        return this.listener;
    }

    
}
