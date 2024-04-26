package it.mikeslab.widencommons.api.inventory.config;


import it.mikeslab.widencommons.api.inventory.pojo.GuiDetails;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public interface GuiConfig {

    /**
     * Load the config file
     * @param fileName The name of the file
     */
    void loadConfig(String fileName, boolean isResource);

    /**
     * Get the details of the gui
     * @param key The key of the gui
     * @return The details of the gui
     */
    GuiDetails getGuiDetails(Optional<String> key, Optional<Map<String, Consumer<InventoryClickEvent>>> consumers);

    /**
     * Parse the details of the gui
     * @param section The section of the config
     */
    void parseDetails(ConfigurationSection section, Optional<Map<String, Consumer<InventoryClickEvent>>> consumers);

}
