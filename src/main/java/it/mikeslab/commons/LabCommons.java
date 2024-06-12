package it.mikeslab.commons;

import io.sentry.Sentry;
import it.mikeslab.commons.api.chat.ChatMessagingHandler;
import it.mikeslab.commons.api.chat.ChatMessagingListener;
import it.mikeslab.commons.api.formatter.FormatUtil;
import it.mikeslab.commons.api.inventory.CustomGui;
import lombok.Getter;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.apache.logging.log4j.core.config.Configurator;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class LabCommons extends JavaPlugin implements Listener {

    public static final String PLUGIN_NAME = "LabCommons";

    private ChatMessagingHandler chatMessagingHandler;

    private BukkitAudiences audiences;

    @Override
    public void onEnable() {

        saveDefaultConfig();

        FormatUtil.printStartupInfos(this, audiences, "00FF72");

        if(this.getConfig().getBoolean("sentry.enabled", false)) {
            this.initSentry();
        }

        if(!this.getConfig().getBoolean("mongo-info-logging", false)) {
            this.disableMongoInfoLogging();
        }

        this.initialize();

    }

    @Override
    public void onDisable() {

        // close all open customGui inventories

        for(Player player : Bukkit.getOnlinePlayers()) {

            Inventory inventory = player.getOpenInventory().getTopInventory();
            InventoryHolder holder = inventory.getHolder();

            if(holder instanceof CustomGui) {
                player.closeInventory();
            }

        }

    }

    private void initialize() {

        // Register Chat Messaging listener
        this.chatMessagingHandler = new ChatMessagingListener(this);

        // Initialize Adventure
        this.audiences = BukkitAudiences.create(this);

    }

    /**
     * Initialize Sentry
     */
    private void initSentry() {

        String version = this.getDescription().getVersion();
        String pluginName = this.getDescription().getName();

        Sentry.init(options -> {
            options.setDsn(this.getConfig().getString("sentry.dsn"));
            options.setRelease(pluginName + "@" + version);
            options.setEnvironment("development");

            options.setTracesSampleRate(1.0);

        });
    }


    /**
     * Set the log level to WARN for the MongoDB driver
     */
    private void disableMongoInfoLogging() {

        // Set the log level to WARN for the MongoDB driver
        Configurator.setLevel(
                "org.mongodb.driver",
                org.apache.logging.log4j.Level.WARN
        );

    }

}
