package Runnables;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Log42jTester
{
    static { System.setProperty("appName", Log42jTester.class.getSimpleName()); }
    final static protected Logger LOGGER = LogManager.getLogger(Log42jTester.class);
    public static void main(String[] args)
    {
        LOGGER.info("Message from info");
        LOGGER.debug("Message from debug");
        LOGGER.warn("Message from warn");
        LOGGER.error("Message from error");
        LOGGER.fatal("Message from fatal");
    }
}
