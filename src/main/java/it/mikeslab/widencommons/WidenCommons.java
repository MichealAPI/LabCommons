package it.mikeslab.widencommons;

import io.sentry.Sentry;
import it.mikeslab.widencommons.api.formatter.FormatUtil;
import it.mikeslab.widencommons.api.logger.LoggerUtil;
import lombok.Getter;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class WidenCommons extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        saveDefaultConfig();


        FormatUtil.printStartupInfos(this, "00FF72");

        LoggerUtil.setPluginName(this.getName());
        if(this.getConfig().getBoolean("sentry.enabled")) {
            this.initSentry();
        }

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

    }

    /**
     * Initialize Sentry
     */
    private void initSentry() {

        String version = this.getPluginMeta().getVersion();
        String pluginName = this.getPluginMeta().getName();

        Sentry.init(options -> {
            options.setDsn(this.getConfig().getString("sentry.dsn"));
            options.setRelease(pluginName + "@" + version);
            options.setEnvironment("development");

            options.setTracesSampleRate(1.0);

        });
    }

}
