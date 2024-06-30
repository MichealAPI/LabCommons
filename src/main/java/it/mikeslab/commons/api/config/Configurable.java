package it.mikeslab.commons.api.config;

import it.mikeslab.commons.api.component.ComponentsUtil;
import it.mikeslab.commons.api.config.impl.ConfigurableImpl;
import it.mikeslab.commons.api.logger.LogUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

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

    default <T> T getConfigValue(ConfigurableEnum configurableEnum, Function<String, T> transformer, T defaultValue) {
        String path = configurableEnum.getPath();

        if (!validateConfig() || !this.checkEntry(configurableEnum)) {
            return defaultValue;
        }

        String configValue = getConfiguration().getString(path);
        return transformer.apply(configValue);
    }

    default Component getComponent(ConfigurableEnum configurableEnum, TagResolver.Single... placeholders) {
        return getConfigValue(configurableEnum, value -> ComponentsUtil.getComponent(value, placeholders), ComponentsUtil.getComponent(String.valueOf(configurableEnum.getDefaultValue()), placeholders));
    }

    default Component getComponent(ConfigurableEnum configurableEnum) {
        return getConfigValue(configurableEnum, ComponentsUtil::getComponent, ComponentsUtil.getComponent(String.valueOf(configurableEnum.getDefaultValue())));
    }

    default List<Component> getComponentList(ConfigurableEnum configurableEnum, TagResolver.Single... placeholders) {
        return getConfigValue(configurableEnum, value -> ComponentsUtil.getComponentList(getConfiguration(), value, placeholders), ComponentsUtil.getComponentList((List<String>) configurableEnum.getDefaultValue(), placeholders));
    }

    /**
     * Validate the configuration
     */
    default boolean validateConfig() {

        boolean isNull = getConfiguration() == null;

        if(isNull) {
            LogUtils.warn(
                    LogUtils.LogSource.CONFIG,
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
            LogUtils.warn(
                    LogUtils.LogSource.CONFIG,
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
     * Reload the configuration
     */
    default Configurable reload() {
        return this.loadConfiguration(getFile());
    }

    default void save() {
        try {
            getConfiguration().save(getFile());
        } catch (Exception e) {
            LogUtils.warn(
                    LogUtils.LogSource.CONFIG,
                    "Error during save: " + e
            );
        }
    }

    /**
     * Create a new instance of the ConfigurableImpl class
     */
    static ConfigurableImpl newInstance() {
        return new ConfigurableImpl();
    }

}
