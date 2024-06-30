package it.mikeslab.commons.api.inventory.util.config;

import lombok.experimental.UtilityClass;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

@UtilityClass
public class FileUtil {

    private final int BUFFER_SIZE = 4096;

    public Optional<FileConfiguration> getConfig(File dataFolder, Path relativePath) {

        File file = new File(dataFolder, relativePath.toString());

        return Optional.of(
                YamlConfiguration.loadConfiguration(file)
        );
    }

    /**
     * Copy a file from source to destination
     * @param source The source file
     * @param destination The destination file
     * @throws IOException If an I/O error occurs
     */
    public void copyFile(File source, File destination) throws IOException {
        Files.createDirectories(destination.getParentFile().toPath());
        try (InputStream in = Files.newInputStream(source.toPath());
             OutputStream out = Files.newOutputStream(destination.toPath())) {

            byte[] buffer = new byte[BUFFER_SIZE];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
        }
    }


    /**
     * Delete a folder recursively, including all its content
     * @param folder The folder to delete
     */
    public void deleteFolderRecursive(File folder) {

        try (Stream<Path> pathStream = Files.walk(folder.toPath())) {
            pathStream.sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
