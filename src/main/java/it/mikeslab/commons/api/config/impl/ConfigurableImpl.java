package it.mikeslab.commons.api.config.impl;

import it.mikeslab.commons.api.config.Configurable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

@Getter
public class ConfigurableImpl implements Configurable {

    private YamlConfiguration configuration;

    private final File dataFolder;

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

}
