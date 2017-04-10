package jaist.echonet;

/**
 * Main interface defining an access scheme for echonet objects by other objects.
 * Mainly implemented by the interface of remote echonet objects.
 * 
 * @author Sioutis Marios
 */

public interface QueryableByOthers {
    public byte[] readProperty(AbstractEchonetObject whoasks, byte property);
    public byte[] readProperty(AbstractEchonetObject whoasks, EchonetProperty copyfrom);
    public boolean writeProperty(AbstractEchonetObject whoasks, EchonetProperty copyfrom);   
}
