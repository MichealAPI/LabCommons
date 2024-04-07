package it.mikeslab.widencommons.api.inventory.util.config;

import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Optional;

@RequiredArgsConstructor
public class FileUtil {

    private final JavaPlugin instance;

    public Optional<FileConfiguration> getConfig(String fileName) {

        File file = new File(instance.getDataFolder(), fileName);

        return Optional.of(
                YamlConfiguration.loadConfiguration(file)
        );
    }

}
