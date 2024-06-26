package it.mikeslab.commons.api.logger;

import it.mikeslab.commons.LabCommons;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;

import java.util.logging.Level;

@UtilityClass
public class LogUtils {

    public void log(Level logLevel, LogSource logSource, Exception exception) {
        Bukkit.getLogger().log(logLevel, "[" + LabCommons.PLUGIN_NAME + "] -> (" + logSource.sourceDisplayName + "): " + exception.getMessage());
    }

    public void log(Level logLevel, LogSource logSource, String message) {
        Bukkit.getLogger().log(logLevel, "[" + LabCommons.PLUGIN_NAME + "] -> (" + logSource.sourceDisplayName + "): " + message);
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

    public enum LogSource {


        PLUGIN("Plugin"),
        DATABASE("Database"),
        COMMAND("Command"),
        EVENT("Event"),
        CONFIG("Config"),
        UTIL("Util"),
        API("API"),
        OTHER("Other");

        private final String sourceDisplayName;

        LogSource(String sourceDisplayName) {
            this.sourceDisplayName = sourceDisplayName;
        }

    }

}
