package it.mikeslab.widencommons.api.config.impl;

import it.mikeslab.widencommons.api.config.Configurable;
import it.mikeslab.widencommons.api.config.ConfigurableEnum;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.file.YamlConfiguration;

import javax.annotation.processing.Generated;
import java.io.File;
import java.util.List;

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
