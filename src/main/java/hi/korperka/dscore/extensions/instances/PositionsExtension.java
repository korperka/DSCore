package hi.korperka.dscore.extensions.instances;

import hi.korperka.dscore.extensions.Extension;
import hi.korperka.dscore.extensions.ExtensionLoader;
import hi.korperka.dscore.files.serializable.ExtensionSector;
import hi.korperka.dscore.files.serializable.PositionsExtensionSector;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.instance.InstanceContainer;

public class PositionsExtension extends Extension {
    private final PositionsExtensionSector positionsConfig;

    public PositionsExtension() {
        super(ExtensionLoader.getExtensionsConfig().getPositions(), "spawn");
        positionsConfig = (PositionsExtensionSector) config;
    }

    @Override
    public void load(InstanceContainer container) {
        int x = positionsConfig.getX();
        int y = positionsConfig.getY();
        int z = positionsConfig.getZ();

        GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
        globalEventHandler.addListener(AsyncPlayerConfigurationEvent.class, event -> {
            final Player player = event.getPlayer();
            event.setSpawningInstance(container);
            //TODO: add commands
            player.setGameMode(GameMode.CREATIVE);
            player.setRespawnPoint(new Pos(x, y, z));
        });
    }
}
