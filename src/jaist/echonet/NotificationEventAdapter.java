package jaist.echonet;

/**
 * Adapter for notification Events only. Use instead of an
 * {@link EchoEventListener} when only notification event processing is desired
 *
 * @author Sioutis Marios
 */
public abstract class NotificationEventAdapter implements EchoEventListener {

    @Override
    public boolean processWriteEvent(EchonetProperty property) {
        return false;
    }

    @Override
    public void processAnswer(EchonetAnswer answer) {
    }

}
