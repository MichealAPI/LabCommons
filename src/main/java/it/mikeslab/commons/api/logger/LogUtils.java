package it.mikeslab.commons.api.logger;

import it.mikeslab.commons.LabCommons;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;

import java.util.logging.Level;

@UtilityClass
public class LogUtils {

    private Boolean IS_TEST;

    public void log(Level logLevel, LogSource logSource, Exception exception) {
        log(logLevel, "[" + LabCommons.PLUGIN_NAME + "] -> (" + logSource.sourceDisplayName + "): ");

        for(StackTraceElement element : exception.getStackTrace()) {
            log(logLevel, element.toString());
        }

    }

    public void log(Level logLevel, LogSource logSource, String message) {
        log(logLevel, "[" + LabCommons.PLUGIN_NAME + "] -> (" + logSource.sourceDisplayName + "): " + message);
    }

    private void log(Level logLevel, String message) {

        if(IS_TEST == null) {
            IS_TEST = isTest();
        }

        if(IS_TEST) {
            System.out.println(message);
        } else {
            Bukkit.getLogger().log(
                    logLevel,
                    message
            );
        }

    }

    // Shortcuts for logging

    public void warn(LogSource logSource, String message) {
        log(Level.WARNING, logSource, message);
    }

    public void warn(LogSource logSource, Exception exception) {
        log(Level.WARNING, logSource, exception);
    }

    public void info(LogSource logSource, String message) {
        log(Level.INFO, logSource, message);
    }

    public void info(LogSource logSource, Exception exception) {
        log(Level.INFO, logSource, exception);
    }

    public void severe(LogSource logSource, String message) {
        log(Level.SEVERE, logSource, message);
    }

    public void severe(LogSource logSource, Exception exception) {
        log(Level.SEVERE, logSource, exception);
    }

    private static boolean isTest() {

        try {
            Bukkit.getLogger();
            return false;
        } catch (NullPointerException e) {
            // If Bukkit is not initialized, we are in a test environment
            return true;
        }

    }

    public enum LogSource {


        PLUGIN("Plugin"),
        DATABASE("Database"),
        COMMAND("Command"),
        EVENT("Event"),
        CONFIG("Config"),
        UTIL("Util"),
        API("API"),
        OTHER("Other"),
        TEST("Test");

        private final String sourceDisplayName;

        LogSource(String sourceDisplayName) {
            this.sourceDisplayName = sourceDisplayName;
        }

    }

}
