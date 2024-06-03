package it.mikeslab.widencommons.api.config;

import it.mikeslab.widencommons.WidenCommons;
import it.mikeslab.widencommons.api.component.ComponentsUtil;
import it.mikeslab.widencommons.api.config.impl.ConfigurableImpl;
import it.mikeslab.widencommons.api.logger.LoggerUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
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

        if(!validateConfig()) {
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

        if(!validateConfig()) {
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

        if(!validateConfig()) {
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

        if(!validateConfig()) {
            return (int) defaultValue;
        }

        return getConfiguration().getInt(path, (int) defaultValue);
    }

    default boolean getBoolean(ConfigurableEnum configurableEnum) {
        String path = configurableEnum.getPath();
        Object defaultValue = configurableEnum.getDefaultValue();

        if(!validateConfig()) {
            return (boolean) defaultValue;
        }

        return getConfiguration().getBoolean(path, (boolean) defaultValue);
    }

    default double getDouble(ConfigurableEnum configurableEnum) {
        String path = configurableEnum.getPath();
        Object defaultValue = configurableEnum.getDefaultValue();

        if(!validateConfig()) {
            return (double) defaultValue;
        }

        return getConfiguration().getDouble(path, (double) defaultValue);
    }

    default String getSerializedString(ConfigurableEnum configurableEnum) {
        Object defaultValue = configurableEnum.getDefaultValue();

        if(!validateConfig()) {
            return (String) defaultValue;
        }

        return LegacyComponentSerializer.legacySection().serialize(
                this.getComponent(configurableEnum)
        );
    }

    /**
     * Get a component list from the configuration
     */
    default List<Component> getComponentList(ConfigurableEnum configurableEnum, TagResolver.Single... placeholders) {

        String path = configurableEnum.getPath();
        Object defaultValue = configurableEnum.getDefaultValue();

        if(!validateConfig()) {
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
                    WidenCommons.PLUGIN_NAME,
                    Level.WARNING,
                    LoggerUtil.LogSource.CONFIG,
                    "Configuration is null"
            );
        }

        // return true if the configuration is not null
        return !isNull;
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
