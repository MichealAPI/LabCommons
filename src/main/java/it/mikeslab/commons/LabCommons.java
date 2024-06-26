package it.mikeslab.commons;

import it.mikeslab.commons.api.chat.ChatMessagingHandler;
import it.mikeslab.commons.api.chat.ChatMessagingListener;
import lombok.Getter;
import org.apache.logging.log4j.core.config.Configurator;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class LabCommons extends JavaPlugin {

    public static String PLUGIN_NAME = "LabCommons"; // default value if not initialized

    public static boolean PLACEHOLDER_API_ENABLED = false;

    private ChatMessagingHandler chatMessagingHandler;

    public void initialize(JavaPlugin instance) {
        PLUGIN_NAME = instance
                .getDescription()
                .getName();

        this.chatMessagingHandler = new ChatMessagingListener(instance);

        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            PLACEHOLDER_API_ENABLED = true;
        }
    }

    public void disable() {
        // close all open customGui inventories

        for(Player player : Bukkit.getOnlinePlayers()) {

            player.closeInventory();

        }
    }


    /**
     * Set the log level to WARN for the MongoDB driver
     */
    public static void disableMongoInfoLogging() {

        // Set the log level to WARN for the MongoDB driver
        Configurator.setLevel(
                "org.mongodb.driver",
                org.apache.logging.log4j.Level.WARN
        );

    }

}
