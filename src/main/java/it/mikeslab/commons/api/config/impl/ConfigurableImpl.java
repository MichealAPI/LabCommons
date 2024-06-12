package it.mikeslab.commons.api.config.impl;

import it.mikeslab.commons.api.config.Configurable;
import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

@Getter
public class ConfigurableImpl implements Configurable {

    private YamlConfiguration configuration;
    private File file;

    @Override
    public ConfigurableImpl loadConfiguration(File file) {
        this.file = file;
        this.configuration = YamlConfiguration.loadConfiguration(file);
        return this;
    }

}
