package jaist.echonet;

/**
 * The main interface used to control query objects, such as
 * {@link EchonetQuery}
 *
 * @author Sioutis Marios
 * @see EchonetQuery
 */
public interface Query {

    int getResponseAvailable();

    void setProcessed();

    void setUnprocessed();

    boolean getProcessed();

    void setSubmissionTime(long when);

    long getSubmissionTime();

    long getTimoutInMillis();

    boolean hasExpired();
}
