package it.mikeslab.commons.api.logger;

import io.sentry.Sentry;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;

import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

@UtilityClass
public class LoggerUtil {

    public void log(final String pluginName, Level logLevel, LogSource logSource, Exception exception) {
        Bukkit.getLogger().log(logLevel, "[" + pluginName + "] -> (" + logSource.sourceDisplayName + "): " + exception.getMessage());

        sentryLog(new SentryLogObject(
                pluginName,
                logLevel,
                logSource,
                exception,
                LogType.EXCEPTION));
    }

    public void log(final String pluginName, Level logLevel, LogSource logSource, String message) {
        Bukkit.getLogger().log(logLevel, "[" + pluginName + "] -> (" + logSource.sourceDisplayName + "): " + message);

        sentryLog(new SentryLogObject(
                pluginName,
                logLevel,
                logSource,
                message,
                LogType.MESSAGE));

    }


    private void sentryLog(SentryLogObject sentryLogObject) {

        CompletableFuture.runAsync(() -> {
            Sentry.setTag("level", sentryLogObject.logLevel.getName());
            Sentry.setTag("plugin", sentryLogObject.pluginName);
            Sentry.setTag("source", sentryLogObject.logSource.sourceDisplayName);
            Sentry.setTag("server", Bukkit.getServer().getName());

            switch (sentryLogObject.logType) {
                case MESSAGE -> Sentry.captureMessage((String) sentryLogObject.logObject);
                case EXCEPTION -> Sentry.captureException((Exception) sentryLogObject.logObject);
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

    @RequiredArgsConstructor
    private class SentryLogObject {
        private final String pluginName;
        private final Level logLevel;
        private final LogSource logSource;
        private final Object logObject;
        private final LogType logType;
    }
}
