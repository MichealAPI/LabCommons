package it.mikeslab.commons;

import it.mikeslab.commons.api.chat.ChatMessagingHandler;
import it.mikeslab.commons.api.chat.ChatMessagingListener;
import it.mikeslab.commons.api.config.ConfigurableEnum;
import it.mikeslab.commons.api.config.impl.ConfigurableImpl;
import it.mikeslab.commons.api.formatter.FormatUtil;
import it.mikeslab.commons.api.logger.LogUtils;
import lombok.Getter;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.apache.logging.log4j.core.config.Configurator;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Getter
public final class LabCommons extends JavaPlugin {

    @Getter
    private static LabCommons instance;

    public static String PLUGIN_NAME = "LabCommons"; // default value if not initialized

    public static boolean PLACEHOLDER_API_ENABLED = false;

    private static final Map<String, ConfigurableImpl> CONFIGURABLE_MAP = new HashMap<>();

    private ChatMessagingHandler chatMessagingHandler;

    private BukkitAudiences audiences;

    @Override
    public void onEnable() {

        this.chatMessagingHandler = new ChatMessagingListener(this);

        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            PLACEHOLDER_API_ENABLED = true;
        }

        this.audiences = BukkitAudiences.create(this);

        instance = this;

        FormatUtil.printStartupInfos(
                this,
                "FFA500"
        );
    }

    @Override
    public void onDisable() {
        // close all open customGui inventories

        for(Player player : Bukkit.getOnlinePlayers()) {

            player.closeInventory();

        }

    }

    public static void enableDebuggingMode() {
        LogUtils.DEBUG = true;
    }

    public static void disableDebuggingMode() {
        LogUtils.DEBUG = false;
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

    /**
     * Register a ConfigurableImpl instance
     * @param dataFolder the data folder
     * @param enumClass the class that implements ConfigurableEnum
     * @return the ConfigurableImpl instance
     */
    public static ConfigurableImpl registerConfigurable(File dataFolder, File targetFile, Class<? extends ConfigurableEnum> enumClass) {
        ConfigurableImpl configurable = new ConfigurableImpl(dataFolder, targetFile);

        CONFIGURABLE_MAP.put(enumClass.getSimpleName(), configurable);

        return configurable;
    }

    /**
     * Get the ConfigurableImpl instance from the class name
     * @param className the class name
     * @return the ConfigurableImpl instance
     */
    public static ConfigurableImpl fromClassName(String className) {
        return CONFIGURABLE_MAP.getOrDefault(className, null);
    }

}
