package com.adera;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

public class Logger {

    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(Logger.class.getName());

    static {
        try {
            LOGGER.setLevel(Level.ALL);

            Handler consoleHandler = new ConsoleHandler();
            consoleHandler.setLevel(Level.ALL);

            Handler fileHandler = new FileHandler(generateLogFileName(), true);
            fileHandler.setLevel(Level.ALL);

            fileHandler.setFormatter(new SimpleFormatter());

            LOGGER.addHandler(consoleHandler);
            LOGGER.addHandler(fileHandler);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String generateLogFileName() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = dateFormat.format(new Date());
        return "logs/log_" + dateStr + ".txt";
    }

    public static void logInfo(String message) {
        LOGGER.info(message);
    }

    public static void logWarning(String message) {
        LOGGER.warning(message);
    }

    public static void logError(String message, Throwable throwable) {
        LOGGER.log(Level.SEVERE, message, throwable);
    }
}
