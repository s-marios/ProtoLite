package jaist.echonet;

import java.util.List;

/**
 * Represents the answer to an {@link EchonetQuery}
 *
 * @author Sioutis Marios
 */
public class EchonetAnswer {

    private Query query;
    private RemoteEchonetObject responder;
    private List<EchonetProperty> properties;
    private List<EchonetProperty> secondaryproperties;
    private ServiceCode code;

    private long when = System.currentTimeMillis();

    /**
     * Constructor.
     *
     * @param query The original query
     * @param responder the origin of this response ( a remote echonet object)
     * @param code the service code of this response
     * @see ServiceCode
     */
    public EchonetAnswer(
            Query query,
            RemoteEchonetObject responder,
            ServiceCode code) {
        this.query = query;
        this.responder = responder;
        this.code = code;
    }

    /**
     *
     * @param query The original query
     * @param responder the origin of this response ( a remote echonet object)
     * @param code the service code of this response
     * @param mainproperties the main property list. In case of a SET* , GET
     * query, the results will be here, as a list of {@link EchonetProperty}
     * objects
     * @param secondaryproperties this list is used in responses of SETGET
     * requests to return the results for the properties that where specified as
     * the GET part of the query. Otherwise is null
     */
    public EchonetAnswer(
            Query query,
            RemoteEchonetObject responder,
            ServiceCode code,
            List<EchonetProperty> mainproperties,
            List<EchonetProperty> secondaryproperties) {

        this.query = query;
        this.responder = responder;
        this.code = code;
        this.properties = mainproperties;
        this.secondaryproperties = secondaryproperties;
    }

    /**
     * Gets the original query
     *
     * @return the original query this answer corresponds to
     */
    public Query getQuery() {
        return query;
    }

    /**
     * sets the query (not sure why this is public)
     *
     * @param query the query to set
     */
    public void setQuery(Query query) {
        this.query = query;
    }

    /**
     * Gets the remote echonet object that was the origin of this response
     *
     * @return the responder
     */
    public RemoteEchonetObject getResponder() {
        return responder;
    }

    /**
     * Sets the responder of this answer (not sure why this is public)
     *
     * @param responder the responder to set
     */
    public void setResponder(RemoteEchonetObject responder) {
        this.responder = responder;
    }

    /**
     * Gets the main property list. This list is the main data that where
     * returned as result of a query i.e. the results of SET or GET queries
     *
     * @return the properties
     */
    public List<EchonetProperty> getProperties() {
        return properties;
    }

    /**
     * Sets the data (not sure why this is public)
     *
     * @param properties the properties to set
     */
    public void setProperties(List<EchonetProperty> properties) {
        this.properties = properties;
    }

    /**
     * Gets the secondary list in the case of a SETGET query. This is the GET
     * list of properties in the case of this answer being an answer to a SETGET
     * query. Otherwise it is null. The SET results are accessible through the {@link EchonetAnswer#getProperties()
     * }
     * method
     *
     * @return the secondaryproperties
     */
    public List<EchonetProperty> getSecondaryProperties() {
        return secondaryproperties;
    }

    /**
     *
     * Sets the secondary data list (not sure why this is public)
     *
     * @param secondaryproperties the secondaryproperties to set
     */
    public void setSecondaryProperties(List<EchonetProperty> secondaryproperties) {
        this.secondaryproperties = secondaryproperties;
    }

    /**
     * Gets the response code associated with this answer
     *
     * @return the response code
     * @see ServiceCode
     */
    public ServiceCode getResponseCode() {
        return code;
    }

    /**
     * Sets the resopnse code associated with this answer (not sure why this is
     * public)
     *
     * @param code the code to set
     */
    public void setResponseCode(ServiceCode code) {
        this.code = code;
    }

    /**
     * Gets a long that represents when this answer was received. This long is
     * the result of <code>System.currentTimeMillis()</code> that is called when
     * this object is created
     *
     * @return when the object was created
     */
    public long getWhen() {
        return when;
    }

    /**
     * Sets the creation time (not sure why this is public)
     *
     * @param when when the object was created
     */
    public void setWhen(long when) {
        this.when = when;
    }
}
