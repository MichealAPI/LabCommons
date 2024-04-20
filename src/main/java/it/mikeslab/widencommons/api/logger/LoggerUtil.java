package it.mikeslab.widencommons.api.logger;

import io.sentry.Sentry;
import org.bukkit.Bukkit;

import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class LoggerUtil {
    protected static String pluginName;

    public static void setPluginName(String pluginName) {
        LoggerUtil.pluginName = pluginName;
    }

    public static void log(Level logLevel, LogSource logSource, Exception exception) {
        Bukkit.getLogger().log(logLevel, "[" + pluginName + "] -> (" + logSource.sourceDisplayName + "): " + exception.getMessage());

        sentryLog(LogType.EXCEPTION, exception, logLevel, logSource);
    }

    public static void log(Level logLevel, LogSource logSource, String message) {
        Bukkit.getLogger().log(logLevel, "[" + pluginName + "] -> (" + logSource.sourceDisplayName + "): " + message);

        sentryLog(LogType.MESSAGE, message, logLevel, logSource);

    }


    private static void sentryLog(LogType logType, Object logObject, Level logLevel, LogSource logSource) {

        CompletableFuture.runAsync(() -> {
            Sentry.setTag("level", logLevel.getName());
            Sentry.setTag("plugin", pluginName);
            Sentry.setTag("source", logSource.sourceDisplayName);
            Sentry.setTag("server", Bukkit.getServer().getName());

            switch (logType) {
                case MESSAGE -> Sentry.captureMessage((String) logObject);
                case EXCEPTION -> Sentry.captureException((Exception) logObject);
            }
        });
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
