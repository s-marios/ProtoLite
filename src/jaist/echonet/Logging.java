/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaist.echonet;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is the global logger for all things regarding the ECHONET Lite
 * networking stack.
 *
 * Use this to log any information needed during the processing of any ECHONET
 * Lite related information (packet parsing, property reads/writes etc.)
 *
 * Any debug messages should be set to Level.FINE or less.
 *
 *
 * @author s-marios
 */
public class Logging {

    static private Logger logger;

    /**
     * get the global logger singleton
     *
     * @return the global logger (create one if necessary)
     */
    static public Logger getLogger() {

        if (logger == null) {
            logger = Logger.getLogger("jaist.echonet.Logging");

        }
        return logger;
    }

    /**
     * set the default logger
     *
     * @param alogger the logger to be set as the default.
     */
    static public void setLogger(Logger alogger) {
        logger = alogger;
    }

    /**
     * Get the current logging level
     *
     * @return current logging level
     */
    static public Level getLoggingLevel() {
        return getLogger().getLevel();
    }

    /**
     * Set the current logging level.
     *
     * @param level the log level. Messages of this level and above will be
     * recorded
     */
    static public void setLoggingLevel(Level level) {
        getLogger().setLevel(level);
    }
}
