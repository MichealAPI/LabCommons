package it.mikeslab.commons.api.various;

import it.mikeslab.commons.api.logger.LogUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public class LabYaml extends YamlConfiguration {

    public static boolean isValidYaml(File file) {
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.load(file);
            return true;
        } catch (InvalidConfigurationException e) {
            LogUtils.severe(
                    LogUtils.LogSource.CONFIG,
                    "Error loading YAML configuration file: " + file.getName()
            );

            LogUtils.severe(
                    LogUtils.LogSource.CONFIG,
                    "Ensure that your config is formatted correctly:"
            );

            LogUtils.warn(
                    LogUtils.LogSource.CONFIG,
                    e.getMessage()
            );
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    @NotNull
    public static YamlConfiguration loadConfiguration(@NotNull File file) throws RuntimeException {
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.load(file);
        } catch (InvalidConfigurationException e) {
            // Handle the exception here (e.g., log the error)
            LogUtils.severe(
                    LogUtils.LogSource.CONFIG,
                    "Error loading YAML configuration file: " + file.getName()
            );

            LogUtils.severe(
                    LogUtils.LogSource.CONFIG,
                    "Ensure that your config is formatted correctly:"
            );

            LogUtils.warn(
                    LogUtils.LogSource.CONFIG,
                    e.getMessage()
            );
            //Logs.logWarning(Arrays.toString(e.getStackTrace()));
            // You can choose to do nothing and keep the existing data in the file
            // or provide default values and continue.
        } catch (IOException e) {
            e.printStackTrace();
        }
        return config;
    }

    @Override
    public void load(@NotNull File file) {
        try {
            super.load(file);
        } catch (Exception e) {
            // Handle the exception here (e.g., log the error)
            LogUtils.severe(
                    LogUtils.LogSource.CONFIG,
                    "Error loading YAML configuration file: " + file.getName()
            );
            //if (Settings.DEBUG.toBool()) Logs.logWarning(e.getMessage()); // todo add debug option
            // You can choose to do nothing and keep the existing data in the file
            // or provide default values and continue.
        }
    }

    public static void saveConfig(@NotNull File file, @NotNull ConfigurationSection section) {
        try {
            YamlConfiguration config = loadConfiguration(file);
            config.set(section.getCurrentPath(), section);
            config.save(file);
        } catch (Exception e) {
            // Handle the exception here (e.g., log the error)
            LogUtils.severe(
                    LogUtils.LogSource.CONFIG,
                    "Error saving YAML configuration file: " + file.getName()
            );
            //if (Settings.DEBUG.toBool()) Logs.logWarning(e.getMessage()); // todo add debug option
            // You can choose to do nothing and keep the existing data in the file
            // or provide default values and continue.
        }
    }


}