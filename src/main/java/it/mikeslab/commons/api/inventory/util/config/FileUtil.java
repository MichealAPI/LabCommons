package it.mikeslab.commons.api.inventory.util.config;

import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

@RequiredArgsConstructor
public class FileUtil {

    private final JavaPlugin instance;

    public Optional<FileConfiguration> getConfig(Path relativePath) {

        File file = new File(instance.getDataFolder(), relativePath.toString());

        return Optional.of(
                YamlConfiguration.loadConfiguration(file)
        );
    }

}
