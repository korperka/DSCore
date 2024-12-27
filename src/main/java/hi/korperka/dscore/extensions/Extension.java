package hi.korperka.dscore.extensions;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import hi.korperka.dscore.files.serializable.ExtensionSector;
import hi.korperka.dscore.logging.DSLogger;
import lombok.Getter;
import net.minestom.server.instance.InstanceContainer;

public abstract class Extension {
    protected final String name;
    protected final ExtensionSector config;
    @Getter
    protected final boolean enabled;
    @Getter
    protected final LoadPriority priority;

    public Extension(ExtensionSector config, String name, LoadPriority priority) {
        this.name = name;
        this.config = config;
        this.enabled = config.isEnabled();
        this.priority = priority;
    }

    public Extension(ExtensionSector config, String name) {
        this.name = name;
        this.config = config;
        this.enabled = config.isEnabled();
        this.priority = LoadPriority.MEDIUM;
    }

    protected void load(InstanceContainer container) {
        DSLogger.info(String.format("Loading %s extension", name));
    }

    protected void unload(InstanceContainer container) {
        DSLogger.info(String.format("Unloading %s extension", name));
    }
}
