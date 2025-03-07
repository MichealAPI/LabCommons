package it.mikeslab.commons.api.config.impl;

import it.mikeslab.commons.api.config.Configurable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class ConfigurableImpl implements Configurable {

    private YamlConfiguration configuration;

    @Getter
    private final File dataFolder;

    @Getter
    private final String configName;

    public ConfigurableImpl(File dataFolder, String configName) {
        this.dataFolder = dataFolder;
        this.configName = configName;

        this.loadConfiguration();
    }

    @Override
    public ConfigurableImpl loadConfiguration() {
        this.configuration = YamlConfiguration.loadConfiguration(this.buildFile());
        return this;
    }

    public YamlConfiguration getConfiguration() {

        if (this.configuration == null) {

            ConfigurableImpl inst = this.loadConfiguration();

            if (inst.configuration == null) {
                throw new IllegalStateException("Configuration is null");
            }

            return inst.configuration;

        }

        return this.configuration;
    }

}
