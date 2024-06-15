package it.mikeslab.commons.api.inventory.config;

import it.mikeslab.commons.LabCommons;
import it.mikeslab.commons.api.component.ComponentsUtil;
import it.mikeslab.commons.api.inventory.GuiType;
import it.mikeslab.commons.api.inventory.event.GuiInteractEvent;
import it.mikeslab.commons.api.inventory.pojo.GuiDetails;
import it.mikeslab.commons.api.inventory.pojo.GuiElement;
import it.mikeslab.commons.api.inventory.util.config.FileUtil;
import it.mikeslab.commons.api.logger.LoggerUtil;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.logging.Level;

@RequiredArgsConstructor
public class GuiConfigImpl implements GuiConfig {

    // required
    private final JavaPlugin instance;

    private GuiDetails guiDetails;
    private FileConfiguration config;

    @Override
    public void loadConfig(String fileName, boolean isResource) {

        // Check if the file exists, if not, save it
        File file = new File(instance.getDataFolder(), fileName);
        if(!file.exists() && isResource) {
            instance.saveResource(fileName, false);
        }

        Optional<FileConfiguration> config = new FileUtil(instance).getConfig(fileName);
        if(!config.isPresent()) {
            LoggerUtil.log(
                    LabCommons.PLUGIN_NAME,
                    Level.WARNING,
                    LoggerUtil.LogSource.CONFIG,
                    String.format("Config '%s' not found", fileName)
            );

            return;
        }

        this.config = config.get();
    }





    @Override
    public GuiDetails getGuiDetails(Optional<String> key, Optional<Map<String, Consumer<GuiInteractEvent>>> consumers) {

        if(config == null) {
            LoggerUtil.log(
                    LabCommons.PLUGIN_NAME,
                    Level.WARNING,
                    LoggerUtil.LogSource.CONFIG,
                    "Config not loaded, load it first"
            );
            return null;
        }

        ConfigurationSection section = config;

        if(key.isPresent()) {
            section = config.getConfigurationSection(key.get());
        }

        this.parseDetails(section, consumers);

        return guiDetails;
    }


    @Override
    public void parseDetails(ConfigurationSection section, Optional<Map<String, Consumer<GuiInteractEvent>>> consumers) {

        // all checks are done in the GuiConfig#getGuiDetails method
        if(section == null) {
            LoggerUtil.log(
                    LabCommons.PLUGIN_NAME,
                    Level.WARNING,
                    LoggerUtil.LogSource.CONFIG,
                    String.format("Invalid section '%s'", section.getName())
            );
            return;
        }

        Component guiTitle = ComponentsUtil.getComponent(section, ConfigField.TITLE.getField());
        String[] layout = section.getStringList(ConfigField.LAYOUT.getField())
                .toArray(new String[0]);

        GuiType guiType = GuiType.valueOf(
                section.getString(
                        ConfigField.TYPE.getField(),
                        GuiType.CHEST.name()
                )
        );

        int size = section.getInt(
                ConfigField.SIZE.getField()
        );

        this.guiDetails = new GuiDetails(
                layout,
                guiType
        );

        this.guiDetails.setInventoryName(guiTitle);
        this.guiDetails.setInventorySize(size);
        this.guiDetails.setClickActions(consumers.orElse(new HashMap<>())); // default empty map for consumers

        this.loadElements(section, consumers);

    }

    /**
     * Load the elements of the gui
     * @param section The section of the config from which to load the elements
     * @param consumers Optional consumers to attach to the elements of the gui
     */
    private void loadElements(ConfigurationSection section, Optional<Map<String, Consumer<GuiInteractEvent>>> consumers) {

        ConfigurationSection elements = section.getConfigurationSection(ConfigField.ELEMENTS.getField());

        boolean areActionConsumersEnabled = consumers.isPresent();

        for(String charKey : elements.getKeys(false)) {

            ConfigurationSection element = elements.getConfigurationSection(charKey);

            Component displayName = ComponentsUtil.getComponent(element, ConfigField.DISPLAYNAME.getField());
            List<Component> lore = ComponentsUtil.getComponentList(element, ConfigField.LORE.getField());

            Material material = Material.getMaterial(
                    element.getString(ConfigField.MATERIAL.getField(), "AIR")
            );

            int amount = element.getInt(ConfigField.AMOUNT.getField(), 1);

            boolean glowing = element.getBoolean(ConfigField.GLOWING.getField(), false);

            int customModelData = element.getInt(ConfigField.CUSTOM_MODEL_DATA.getField(), -1);

            String internalValue = element.getString(ConfigField.INTERNAL_VALUE.getField(), null);

            List<String> actions = element.getStringList(ConfigField.ACTIONS.getField());

            boolean isGroupElement = element.getBoolean(ConfigField.IS_GROUP_ELEMENT.getField(), false);

            // int order = isGrouped ? Integer.parseInt(charKey.split("-")[1]) : -1;

            GuiElement guiElement = GuiElement.builder()
                    .customModelData(customModelData)
                    .displayName(displayName)
                    .lore(lore)
                    .material(material)
                    .amount(amount)
                    .glow(glowing)
                    .internalValue(internalValue)
                    .actions(actions)
                    .isGroupElement(isGroupElement)
                    //.isGrouped(isGrouped)
                    //.order(order)
                    .build();

            if(areActionConsumersEnabled) {
                this.parseConsumers(element, consumers, guiElement);
            }

            guiDetails.addElement(charKey.charAt(0), guiElement);
        }

    }

    private void parseConsumers(ConfigurationSection section, Optional<Map<String, Consumer<GuiInteractEvent>>> consumers, GuiElement guiElement) {

        // To avoid another param in the method, we will stringify the enum
        String internalValue = section.getString(ConfigField.INTERNAL_VALUE.getField(), "");

        for(Map.Entry<String, Consumer<GuiInteractEvent>> entry : consumers.get().entrySet()) {

            String actionKey = entry.getKey();
            Consumer<GuiInteractEvent> actionConsumer = entry.getValue();

            if(internalValue == null) continue;

            if(internalValue.equalsIgnoreCase(actionKey)) {
                guiElement.setOnClick(actionConsumer);
            }

        }

    }






}
