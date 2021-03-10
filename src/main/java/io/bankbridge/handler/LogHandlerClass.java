package io.bankbridge.handler;

import java.io.IOException;
import java.util.logging.*;

public class LogHandlerClass {

    private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);


    public static void setupLogger() {
        LogManager.getLogManager().reset();
        logger.setLevel(Level.ALL);

        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.SEVERE);
        logger.addHandler(consoleHandler);


        try {
            FileHandler fileHandler = new FileHandler("main-log.log", true);
            fileHandler.setLevel(Level.ALL);
            logger.addHandler(fileHandler);
        } catch (IOException ioException) {
            logger.log(Level.SEVERE, "Error: file logger not working.", ioException);
        }
    }
}
