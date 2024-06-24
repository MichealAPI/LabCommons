package it.mikeslab.commons.api.inventory.config;

import com.cryptomorin.xseries.XMaterial;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import it.mikeslab.commons.LabCommons;
import it.mikeslab.commons.api.component.ComponentsUtil;
import it.mikeslab.commons.api.inventory.GuiType;
import it.mikeslab.commons.api.inventory.event.GuiInteractEvent;
import it.mikeslab.commons.api.inventory.pojo.GuiDetails;
import it.mikeslab.commons.api.inventory.pojo.GuiElement;
import it.mikeslab.commons.api.inventory.util.config.FileUtil;
import it.mikeslab.commons.api.inventory.util.frame.FrameColorUtil;
import it.mikeslab.commons.api.logger.LoggerUtil;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Level;

@RequiredArgsConstructor
public class GuiConfigImpl implements GuiConfig {

    // required
    private final JavaPlugin instance;

    private GuiDetails guiDetails;
    private FileConfiguration config;

    @Override
    public void loadConfig(Path relativePath, boolean isResource) {

        // Check if the file exists, if not, save it
        String relativePathAsString = relativePath.toString();
        File file = new File(instance.getDataFolder(), relativePathAsString);
        String fileName = relativePath.getFileName().toString();

        if(!file.exists() && isResource) {
            instance.saveResource(relativePathAsString, false);
        }

        Optional<FileConfiguration> config = new FileUtil(instance).getConfig(relativePath);
        if(!config.isPresent()) {
            LoggerUtil.log(
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

        // List<String> onOpenActions = section.getStringList(ConfigField.ON_OPEN_ACTIONS.getField());
        // List<String> onCloseActions = section.getStringList(ConfigField.ON_CLOSE_ACTIONS.getField());

        this.guiDetails = new GuiDetails(
                layout,
                guiType
        );

        this.guiDetails.setInventoryName(guiTitle);
        this.guiDetails.setInventorySize(size);
        this.guiDetails.setClickActions(consumers.orElse(new HashMap<>())); // default empty map for consumers
        // this.guiDetails.setOnCloseActions(onCloseActions);
        // this.guiDetails.setOnOpenActions(onOpenActions);

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

            ConfigurationSection elementSection = elements.getConfigurationSection(charKey);

            GuiElement guiElement = this.loadElement(elementSection);
            // this.postProcessElement(guiElement); // todo moved once again to CustomGui

            if(areActionConsumersEnabled) {
                this.parseConsumers(elementSection, consumers, guiElement);
            }

            guiDetails.addElement(charKey.charAt(0), guiElement);
        }

    }

    @Override
    public GuiElement loadElement(ConfigurationSection section) {

        String displayName = section.getString(ConfigField.DISPLAYNAME.getField());
        List<String> lore = section.getStringList(ConfigField.LORE.getField());

        // Optional<XMaterial> matchXMaterial = XMaterial.matchXMaterial(
        //        section.getString(ConfigField.MATERIAL.getField())
        //);

        Optional<XMaterial> matchXMaterial = XMaterial.matchXMaterial(
                section.getString(ConfigField.MATERIAL.getField())
        );

        if(!matchXMaterial.isPresent()) {
            throw new IllegalArgumentException("Invalid material for the element '" + section.getName() + "'");
        }

        Material material = matchXMaterial.get().parseMaterial();

        int amount = section.getInt(ConfigField.AMOUNT.getField(), 1);

        boolean glowing = section.getBoolean(ConfigField.GLOWING.getField(), false);

        int customModelData = section.getInt(ConfigField.CUSTOM_MODEL_DATA.getField(), -1);

        String internalValue = section.getString(ConfigField.INTERNAL_VALUE.getField(), null);

        List<String> actions = section.getStringList(ConfigField.ACTIONS.getField());

        boolean isGroupElement = section.getBoolean(ConfigField.IS_GROUP_ELEMENT.getField(), false);

        String headValue = section.getString(ConfigField.HEAD_VALUE.getField(), null);

        Optional<String> condition = Optional.ofNullable(section.getString(ConfigField.CONDITION.getField(), null));


        // int order = isGrouped ? Integer.parseInt(charKey.split("-")[1]) : -1;

        return GuiElement.builder()
                .customModelData(customModelData)
                .displayName(displayName)
                .lore(lore)
                .material(material)
                .amount(amount)
                .glow(glowing)
                .condition(condition)
                .internalValue(internalValue)
                .actions(actions)
                .headValue(headValue)
                .isGroupElement(isGroupElement)
                .frames(Optional.empty())
                //.isGrouped(isGrouped)
                //.order(order)
                .build();
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
