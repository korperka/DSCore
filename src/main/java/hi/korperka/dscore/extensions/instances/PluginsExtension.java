package hi.korperka.dscore.extensions.instances;

import hi.korperka.dscore.extensions.Extension;
import hi.korperka.dscore.extensions.ExtensionLoader;
import hi.korperka.dscore.plugins.PluginLoader;
import net.minestom.server.instance.InstanceContainer;

public class PluginsExtension extends Extension {
    public PluginsExtension() {
        super(ExtensionLoader.getExtensionsConfig().getPlugins(), "plugins");
    }

    @Override
    protected void load(InstanceContainer container) {
        PluginLoader.loadPlugins();
    }

    @Override
    protected void unload(InstanceContainer container) {
        PluginLoader.unloadPlugins();
    }
}
