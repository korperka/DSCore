package hi.korperka.dscore.files.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import hi.korperka.dscore.logging.DSLogger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class ConfigLoader {
    private static final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    public static <T extends Config> T loadConfig(Path path, Class<T> clazz) {
        File file = path.toFile();

        if (!file.exists()) {
            return null;
        }

        try {
            return mapper.readValue(file, clazz);
        } catch (IOException e) {
            DSLogger.error("Error loading configuration from file: " + e.getMessage());
            return null;
        }
    }

    public static void generateDefaultConfig(Config config, Path path) {
        File file = path.toFile();

        if (file.exists()) {
            return;
        }

        try {
            mapper.writeValue(file, config);
            DSLogger.info("Config created in path " + path);
        } catch (IOException e) {
            DSLogger.error("Cannot create configuration file: " + e.getMessage());
        }
    }

    public static <T extends Config> T initConfig(Path path, Class<T> clazz) {
        T config = loadConfig(path, clazz);

        if (config == null) {
            try {
                config = clazz.getDeclaredConstructor().newInstance();
                generateDefaultConfig(config, path);
            } catch (Exception e) {
                DSLogger.error("Failed to initialize configuration: " + e.getMessage());
                throw new RuntimeException("Cannot initialize configuration", e);
            }
        }

        return config;
    }
}