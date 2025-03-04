package it.mikeslab.commons.api.config.impl;

import it.mikeslab.commons.api.config.Configurable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

@RequiredArgsConstructor
public class ConfigurableImpl implements Configurable {

    private final File dataFolder;
    private YamlConfiguration configuration;


    @Getter
    private File file;

    @Override
    public ConfigurableImpl loadConfiguration(File file) {
        this.file = file;
        this.configuration = YamlConfiguration.loadConfiguration(new File(
                this.dataFolder,
                file.getName()
        ));
        return this;
    }

    public YamlConfiguration getConfiguration() {

        if (this.configuration == null) {

            // Returns inner-plugin configuration if not loaded
            return YamlConfiguration.loadConfiguration(this.file);

        }

        return this.configuration;
    }

}
