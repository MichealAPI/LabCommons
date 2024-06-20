package it.mikeslab.commons.api.logger;

import it.mikeslab.commons.LabCommons;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;

import java.util.logging.Level;

@UtilityClass
public class LoggerUtil {

    public void log(Level logLevel, LogSource logSource, Exception exception) {
        Bukkit.getLogger().log(logLevel, "[" + LabCommons.PLUGIN_NAME + "] -> (" + logSource.sourceDisplayName + "): " + exception.getMessage());
    }

    public void log(Level logLevel, LogSource logSource, String message) {
        Bukkit.getLogger().log(logLevel, "[" + LabCommons.PLUGIN_NAME + "] -> (" + logSource.sourceDisplayName + "): " + message);
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


    private enum LogType {
        EXCEPTION,
        MESSAGE
    }

}
