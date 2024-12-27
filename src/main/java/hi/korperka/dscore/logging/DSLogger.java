package hi.korperka.dscore.logging;

import org.jetbrains.annotations.NotNull;

import java.util.logging.*;

public class DSLogger {
    private static class LogFormatter extends Formatter {
        private static final String RESET = "\u001B[0m";
        private static final String GREEN = "\u001B[32m";
        private static final String YELLOW = "\u001B[33m";
        private static final String RED = "\u001B[31m";

        @Override
        public String format(LogRecord record) {
            String color = getColorCode(record);

            return color
                    + "[" + record.getLevel().getLocalizedName() + "] "
                    + RESET
                    + record.getMessage()
                    + RESET + System.lineSeparator();
        }

        private @NotNull String getColorCode(LogRecord record) {
            if (record.getLevel() == Level.INFO) {
                return GREEN;
            }
            else if (record.getLevel() == Level.WARNING) {
                return YELLOW;
            }
            else if (record.getLevel() == Level.SEVERE) {
                return RED;
            }

            return RESET;
        }
    }

    private static final Logger logger = Logger.getLogger(DSLogger.class.getName());

    static {
        Logger rootLogger = Logger.getLogger("");
        Handler[] handlers = rootLogger.getHandlers();
        for (Handler handler : handlers) {
            rootLogger.removeHandler(handler);
        }

        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new LogFormatter());
        logger.addHandler(handler);

        logger.setLevel(Level.ALL);
        handler.setLevel(Level.ALL);
    }

    public static void info(String message) {
        logger.log(Level.INFO, message);
    }

    public static void warn(String message) {
        logger.log(Level.WARNING, message);
    }

    public static void error(String message) {
        logger.log(Level.SEVERE, message);
    }
}