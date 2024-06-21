package it.mikeslab.commons;

import it.mikeslab.commons.api.chat.ChatMessagingHandler;
import it.mikeslab.commons.api.chat.ChatMessagingListener;
import it.mikeslab.commons.api.inventory.CustomGui;
import lombok.Getter;
import org.apache.logging.log4j.core.config.Configurator;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class LabCommons extends JavaPlugin {

    public static String PLUGIN_NAME = "LabCommons"; // default value if not initialized

    private ChatMessagingHandler chatMessagingHandler;

    public void initialize(JavaPlugin instance) {
        PLUGIN_NAME = instance
                .getDescription()
                .getName();

        this.chatMessagingHandler = new ChatMessagingListener(instance);
    }

    public void disable() {
        // close all open customGui inventories

        for(Player player : Bukkit.getOnlinePlayers()) {

            Inventory inventory = player.getOpenInventory().getTopInventory();
            InventoryHolder holder = inventory.getHolder();

            if(holder instanceof CustomGui || inventory.getType() == InventoryType.ANVIL) {
                player.closeInventory();
            }

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

    @Override
    public void onDisable() {

    }

    @Override
    public void onEnable() {
        super.onEnable();
    }
}
