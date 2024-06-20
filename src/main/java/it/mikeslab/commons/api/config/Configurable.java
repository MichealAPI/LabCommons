package it.mikeslab.commons.api.config;

import it.mikeslab.commons.LabCommons;
import it.mikeslab.commons.api.component.ComponentsUtil;
import it.mikeslab.commons.api.config.impl.ConfigurableImpl;
import it.mikeslab.commons.api.logger.LoggerUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public interface Configurable {

    /**
     * Get the YAML configuration file
     * @param file the configuration file
     */
    ConfigurableImpl loadConfiguration(File file);

    /**
     * Get the YAML configuration file
     */
    YamlConfiguration getConfiguration();

    /**
     * Get component from the configuration
     */
    default Component getComponent(ConfigurableEnum configurableEnum) {

        String path = configurableEnum.getPath();
        Object defaultValue = configurableEnum.getDefaultValue();

        if(!validateConfig() || !this.checkEntry(configurableEnum)) {
            return ComponentsUtil.getComponent(
                    String.valueOf(defaultValue)
            );
        }

        return ComponentsUtil.getComponent(
                getConfiguration(),
                path
        );
    }

    default Component getComponent(ConfigurableEnum configurableEnum, TagResolver.Single... placeholders) {

        String path = configurableEnum.getPath();
        Object defaultValue = configurableEnum.getDefaultValue();

        if(!validateConfig() || !this.checkEntry(configurableEnum)) {
            return ComponentsUtil.getComponent(
                    String.valueOf(defaultValue),
                    placeholders
            );
        }

        return ComponentsUtil.getComponent(
                getConfiguration().getString(path),
                placeholders
        );
    }

    /**
     * Get a string from the configuration
     */
    default String getString(ConfigurableEnum configurableEnum) {
        String path = configurableEnum.getPath();
        Object defaultValue = configurableEnum.getDefaultValue();

        if(!validateConfig() || !this.checkEntry(configurableEnum)) {
            return String.valueOf(defaultValue);
        }

        return getConfiguration().getString(path, String.valueOf(defaultValue));
    }

    default String getString(ConfigurableEnum configurableEnum, Map<String, String> placeholders) {
        String result = this.getString(configurableEnum);

        for(Map.Entry<String, String> entry : placeholders.entrySet()) {
            result = result.replace("<" + entry.getKey() + ">", entry.getValue());
        }

        return result;
    }


    default int getInt(ConfigurableEnum configurableEnum) {
        String path = configurableEnum.getPath();
        Object defaultValue = configurableEnum.getDefaultValue();

        if(!validateConfig() || !this.checkEntry(configurableEnum)) {
            return (int) defaultValue;
        }

        return getConfiguration().getInt(path, (int) defaultValue);
    }

    default boolean getBoolean(ConfigurableEnum configurableEnum) {
        String path = configurableEnum.getPath();
        Object defaultValue = configurableEnum.getDefaultValue();

        if(!validateConfig() || !this.checkEntry(configurableEnum)) {
            return (boolean) defaultValue;
        }

        return getConfiguration().getBoolean(path, (boolean) defaultValue);
    }

    default double getDouble(ConfigurableEnum configurableEnum) {
        String path = configurableEnum.getPath();
        Object defaultValue = configurableEnum.getDefaultValue();

        if(!validateConfig() || !this.checkEntry(configurableEnum)) {
            return (double) defaultValue;
        }

        return getConfiguration().getDouble(path, (double) defaultValue);
    }

    default String getSerializedString(ConfigurableEnum configurableEnum) {
        Object defaultValue = configurableEnum.getDefaultValue();

        if(!validateConfig() || !this.checkEntry(configurableEnum)) {
            return (String) defaultValue;
        }

        return MiniMessage.miniMessage().serialize(
                this.getComponent(configurableEnum)
        );
    }

    default String getSerializedString(ConfigurableEnum configurableEnum, TagResolver.Single... placeholders) {
        Object defaultValue = configurableEnum.getDefaultValue();

        if(!validateConfig() || !this.checkEntry(configurableEnum)) {
            return (String) defaultValue;
        }

        return MiniMessage.miniMessage().serialize(
                this.getComponent(configurableEnum, placeholders)
        );
    }

    /**
     * Get a component list from the configuration
     */
    default List<Component> getComponentList(ConfigurableEnum configurableEnum, TagResolver.Single... placeholders) {

        String path = configurableEnum.getPath();
        Object defaultValue = configurableEnum.getDefaultValue();

        if(!validateConfig() || !this.checkEntry(configurableEnum)) {
            return ComponentsUtil.getComponentList(

                    // todo unchecked
                    (List<String>) defaultValue,
                    placeholders
            );
        }

        return ComponentsUtil.getComponentList(
                getConfiguration(),
                path,
                placeholders
        );
    }



    /**
     * Validate the configuration
     */
    default boolean validateConfig() {

        boolean isNull = getConfiguration() == null;

        if(isNull) {
            LoggerUtil.log(
                    LabCommons.PLUGIN_NAME,
                    Level.WARNING,
                    LoggerUtil.LogSource.CONFIG,
                    "Configuration is null"
            );
        }

        // return true if the configuration is not null
        return !isNull;
    }


    default boolean contains(ConfigurableEnum configurableEnum) {
        return getConfiguration().contains(configurableEnum.getPath());
    }

    default boolean checkEntry(ConfigurableEnum configurableEnum) {

        boolean contains = this.contains(configurableEnum);

        if(!contains) {
            LoggerUtil.log(
                    LabCommons.PLUGIN_NAME,
                    Level.WARNING,
                    LoggerUtil.LogSource.CONFIG,
                    String.format("Entry '%s' not found in the configuration, using the default value", configurableEnum.getPath())
            );
        }

        return contains;
    }

    /**
     * Get the file of the configuration
     */
    File getFile();


    /**
     * Create a new instance of the ConfigurableImpl class
     */
    static ConfigurableImpl newInstance() {
        return new ConfigurableImpl();
    }

}
