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

}
