package it.mikeslab.widencommons.api.logger;

import org.bukkit.Bukkit;

import java.util.logging.Level;

public class LoggerUtil {
    protected static String pluginName;

    public static void setPluginName(String pluginName) {
        LoggerUtil.pluginName = pluginName;
    }

    public static void log(Level logLevel, LogSource logSource, Exception exception) {
        Bukkit.getLogger().log(logLevel, "[" + pluginName + "] -> (" + logSource.sourceDisplayName + "): " + exception.getMessage());
    }

    public static void log(Level logLevel, LogSource logSource, String message) {
        Bukkit.getLogger().log(logLevel, "[" + pluginName + "] -> (" + logSource.sourceDisplayName + "): " + message);
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
