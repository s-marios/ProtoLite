package jaist.echonet;

/**
 * An adapter for echonet write events.
 *
 * @author Sioutis Marios
 * @see EchoEventListener
 */
public abstract class WriteEventAdapter implements EchoEventListener {

    @Override
    public boolean processNotificationEvent(RemoteEchonetObject robject, EchonetProperty property) {
        return false;
    }

    @Override
    public void processAnswer(EchonetAnswer answer) {
    }

}
