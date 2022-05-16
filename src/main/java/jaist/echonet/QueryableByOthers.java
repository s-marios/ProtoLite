package jaist.echonet;

/**
 * Main interface defining an access scheme for ECHOENT objects by other
 * objects. Mainly implemented by the interface of remote ECHONET objects.
 *
 * @author Sioutis Marios
 */
public interface QueryableByOthers {

    public byte[] readProperty(AbstractEchonetObject whoasks, byte property);

    public byte[] readProperty(AbstractEchonetObject whoasks, EchonetProperty copyfrom);

    public boolean writeProperty(AbstractEchonetObject whoasks, EchonetProperty copyfrom);
}
