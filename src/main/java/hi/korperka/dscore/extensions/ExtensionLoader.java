package hi.korperka.dscore.extensions;

import com.typesafe.config.ConfigFactory;
import hi.korperka.dscore.DSCore;
import hi.korperka.dscore.files.ExtensionsConfig;
import hi.korperka.dscore.files.config.Config;
import hi.korperka.dscore.logging.DSLogger;
import io.github.classgraph.ClassGraph;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.instance.InstanceContainer;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

public class ExtensionLoader {
    @Getter
    private final static String configName = "extensions.conf";
    @Getter
    private final static Path extensionsPath = DSCore.getRootPath().resolve(configName);
    @Getter @Setter
    private static ExtensionsConfig extensionsConfig;
    @Setter
    private static List<Extension> extensions;

    public static void load(InstanceContainer container) {
        extensions = new ClassGraph()
                .enableClassInfo()
                .scan()
                .getSubclasses(Extension.class)
                .loadClasses(Extension.class)
                .stream()
                .map(cls -> {
                    try {
                        return cls.getDeclaredConstructor().newInstance();
                    } catch (Exception e) {
                        DSLogger.error(String.format("Error during loading extension %s", cls.getName()));
                        throw new RuntimeException(e);
                    }
                })
                .filter(Extension::isEnabled)
                .sorted(Comparator.comparing(Extension::getPriority))
                .toList();

        extensions.forEach(extension -> extension.load(container));
    }

    public static void unload(InstanceContainer container) {
        extensions.forEach(extension -> extension.unload(container));
    }
}
