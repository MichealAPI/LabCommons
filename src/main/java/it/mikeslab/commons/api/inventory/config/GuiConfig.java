package it.mikeslab.commons.api.inventory.config;


import it.mikeslab.commons.api.inventory.event.GuiInteractEvent;
import it.mikeslab.commons.api.inventory.pojo.GuiDetails;
import it.mikeslab.commons.api.inventory.pojo.GuiElement;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public interface GuiConfig {

    /**
     * Load the config file
     * @param relativePath The relative path of the config file
     */
    void loadConfig(Path relativePath, boolean isResource);

    /**
     * Get the details of the gui
     * @param key The key of the gui
     * @return The details of the gui
     */
    GuiDetails getGuiDetails(Optional<String> key, Optional<Map<String, Consumer<GuiInteractEvent>>> consumers);

    /**
     * Parse the details of the gui
     * @param section The section of the config
     */
    void parseDetails(ConfigurationSection section, Optional<Map<String, Consumer<GuiInteractEvent>>> consumers);

    /**
     * Load an element from the config
     * @param section The section of the config
     * @return The element
     */
    GuiElement loadElement(ConfigurationSection section);

}
