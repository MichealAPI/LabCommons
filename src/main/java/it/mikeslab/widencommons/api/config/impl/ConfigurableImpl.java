package it.mikeslab.widencommons.api.config.impl;

import it.mikeslab.widencommons.api.config.Configurable;
import it.mikeslab.widencommons.api.config.ConfigurableEnum;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;

public class ConfigurableImpl implements Configurable {

    private YamlConfiguration configuration;

    @Override
    public ConfigurableImpl loadConfiguration(File file) {
        this.configuration = YamlConfiguration.loadConfiguration(file);
        return this;
    }

    @Override
    public YamlConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public Component getComponent(ConfigurableEnum configurableEnum) {
        return Configurable.super.getComponent(configurableEnum);
    }

    @Override
    public List<Component> getComponentList(ConfigurableEnum configurableEnum) {
        return Configurable.super.getComponentList(configurableEnum);
    }

    @Override
    public boolean validateConfig() {
        return Configurable.super.validateConfig();
    }

    @Override
    public String getString(ConfigurableEnum configurableEnum) {
        return Configurable.super.getString(configurableEnum);
    }

}
