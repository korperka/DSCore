package hi.korperka.dscore.plugins;

import hi.korperka.dscore.logging.DSLogger;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PluginLoader {
    private static final File pluginsDirectory = new File("plugins");
    private static final List<MinestomPlugin> loadedPlugins = new ArrayList<>();
    private static final String errorMessage = "Error during loading plugin %s!";

    static {
        if(!pluginsDirectory.exists()) {
            pluginsDirectory.mkdirs();
        }
    }

    public static void loadPlugins() {
        File[] files = pluginsDirectory.listFiles((dir, name) -> name.endsWith(".jar"));

        if(files == null) {
            DSLogger.info("No plugins found");
            return;
        }

        for(File file : files) {
            try {
                MinestomPlugin plugin = loadPlugin(file);
                loadedPlugins.add(plugin);
                plugin.onEnable();
            } catch (Exception e) {
                DSLogger.error(String.format(errorMessage, file.getName()));
                throw new RuntimeException(e);
            }
        }
    }

    public static void unloadPlugins() {
        loadedPlugins.forEach(MinestomPlugin::onDisable);
        loadedPlugins.clear();
    }

    public static MinestomPlugin loadPlugin(File file) throws Exception {
        URL[] URLs = {file.toURI().toURL()};
        URLClassLoader loader = new URLClassLoader(URLs);

        Properties properties = getProperties(file);
        String mainClassName = properties.getProperty("main");

        if(mainClassName == null) {
            DSLogger.error(String.format(errorMessage, file.getName()));
            throw new IOException("Main class is absent!");
        }

        Class<?> pluginClass = loader.loadClass(mainClassName);
        Object instance = pluginClass.getDeclaredConstructor().newInstance();

        if(!(instance instanceof MinestomPlugin)) {
            DSLogger.error(String.format(errorMessage, file.getName()));
            throw new IOException("Main class is not implement plugin interface!");
        }

        return (MinestomPlugin) instance;
    }

    private static Properties getProperties(File file) throws IOException {
        try (JarFile jarFile = new JarFile(file)){
            JarEntry entry = jarFile.getJarEntry("plugin.yml");

            if(entry == null) {
                DSLogger.error(String.format(errorMessage, file.getName()));
                throw new IOException("plugin.yml not found");
            }

            Properties properties = new Properties();
            properties.load(jarFile.getInputStream(entry));

            return properties;
        }
    }
}
