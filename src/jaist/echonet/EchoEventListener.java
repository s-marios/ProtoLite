package jaist.echonet;

/**
 * The interface for the various events in the implementation. There are three
 * possible events:
 * <ul>
 * <li>a write event</li>
 * <li>a notification event</li>
 * <li>an asynchronous answer event</li>
 * </ul>
 *
 * For information on how to register listeners, see the "See Also" section
 * below
 *
 * @see EchonetNode#registerForNotifications(java.net.InetAddress,
 * java.lang.Byte, java.lang.Byte, java.lang.Byte, java.lang.Byte,
 * jaist.echonet.EchoEventListener)
 * @see EchonetNode#makeQuery(jaist.echonet.AbstractEchonetObject,
 * jaist.echonet.AbstractEchonetObject, jaist.echonet.ServiceCode,
 * java.util.List, java.util.List, jaist.echonet.EchoEventListener)
 * @see AbstractEchonetObject#registerListener(jaist.echonet.EchoEventListener)
 *
 * @author Sioutis Marios
 */
public interface EchoEventListener {

    /**
     * This method of this listener will be called when a write event occurs.
     * Register this listener with the object of interest by using
     * {@link AbstractEchonetObject#registerListener(jaist.echonet.EchoEventListener) }
     *
     * @param property the property to be written. Data as well as property code
     * is represented in this parameter
     * @return true if the write event was processed correctly, false otherwise
     */
    boolean processWriteEvent(EchonetProperty property);

    /**
     * This method will be called to process a notification event. Register by
     * using {@link EchonetNode#makeNotification(jaist.echonet.AbstractEchonetObject, jaist.echonet.EchonetProperty)
     * }
     *
     *
     * @param robject the remote object that sent the notification
     * @param property the property included in the notification event
     * @return whether the notification event was processed successfully or not
     */
    boolean processNotificationEvent(RemoteEchonetObject robject, EchonetProperty property);

    /**
     * This method will be called to process an answer to a query in case of
     * asynchronous processing. Register this listener when making a query as
     * the last argument of {@link EchonetNode#makeQuery(jaist.echonet.AbstractEchonetObject, jaist.echonet.AbstractEchonetObject, jaist.echonet.ServiceCode, java.util.List, java.util.List, jaist.echonet.EchoEventListener)
     * }
     *
     * @param answer the answer to the query, null if the query timed out
     * without receiving an answer
     * @see EchonetAnswer
     */
    public void processAnswer(EchonetAnswer answer);
}
