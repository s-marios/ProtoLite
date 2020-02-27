package jaist.echonet;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The resulting object of an ECHONET query. Used for synchronization purposes
 * and to access answers related to this query in a synchronous, serialized
 * manner.
 *
 * @author Sioutis Marios
 * @see EchonetAnswer
 */
public class EchonetQuery extends ArrayList<EchonetAnswer> implements Query {

    private final AbstractEchonetObject querysource;
    private boolean processed = false;
    private long when = System.currentTimeMillis();
    private final EchoEventListener listener;
    private final ServiceCode code;
    private long timeout;

    EchonetQuery(ServiceCode code, AbstractEchonetObject source, EchoEventListener listener) {
        this.querysource = source;
        this.listener = listener;
        this.code = code;
        //default timeout for unicasts
        this.timeout = EchonetProtocol.AWAITRESPONSE;
    }

    /**
     * Returns the query echonet object that represents the target of this query
     *
     * @return the target echonet object
     */
    public AbstractEchonetObject getSource() {
        return this.querysource;
    }

    /**
     * Gets the number of responses available
     *
     * @return the number of available responses
     */
    @Override
    public int getResponseAvailable() {
        return this.size();
    }

    /**
     * Returns if this query has been processed
     *
     * @return true if it has been processed, false otherwise
     */
    @Override
    public boolean getProcessed() {
        return this.processed;
    }

    /**
     * Gets the submission time of this query
     *
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
     *
     * @param when the submission time as a long
     */
    @Override
    public void setSubmissionTime(long when) {
        this.when = when;
    }

    /**
     * Gets the associated time out value for this query
     *
     * @return the timeout value, in milliseconds
     */
    @Override
    public long getTimeout() {
        return this.timeout;
    }

    /**
     * Set the timeout for this query, in milliseconds. After timeout
     * milliseconds have elapsed, this query will have expired.
     *
     * @param timeout the elapsed time after which the query will expire
     */
    @Override
    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    /**
     * Returns if this query has expired
     *
     * @return true if this query has expired, false otherwise
     */
    @Override
    public boolean hasExpired() {
        return (System.currentTimeMillis() - this.when > getTimeout());
    }

    /**
     * A BLOCKING method that may return <ul>
     * <li> the next answer associated with this query</li>
     * <li> null if there are no more answers and the query has expired</li>
     * </ul>
     *
     * @return an {@link EchonetAnswer EchonetAnswer object} or null
     */
    public synchronized EchonetAnswer getNextAnswer() {
        waitForAnAnswer();
        return getNextAnswerNonBlocking();
    }

    /**
     * Returns the next answer associated with this query if there is an answer
     * available, or null if no answer is available without blocking
     *
     * @return an answer, or null if no answer is available
     */
    public synchronized EchonetAnswer getNextAnswerNonBlocking() {
        EchonetAnswer answer = null;
        if (!this.isEmpty()) {
            answer = this.get(0);
            this.remove(answer);
        }
        return answer;
    }

    private synchronized void waitForAnAnswer() {

        try {
            while (this.isEmpty()) {
                long elapsedtime = System.currentTimeMillis() - when;
                if (elapsedtime < this.getTimeout()) {
                    this.wait(this.getTimeout() - elapsedtime);
                } else {
                    return;// timeout occured
                }
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(RemoteEchonetObject.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Returns the asynchronous answer listener associated with this query
     *
     * @return the asynchronous answer listener associated with this query
     */
    public EchoEventListener getListener() {
        return this.listener;
    }

}
