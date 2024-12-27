package hi.korperka.dscore.extensions.instances;

import hi.korperka.dscore.DSCore;
import hi.korperka.dscore.extensions.Extension;
import hi.korperka.dscore.extensions.ExtensionLoader;
import hi.korperka.dscore.extensions.LoadPriority;
import hi.korperka.dscore.logging.DSLogger;
import hi.korperka.dscore.worldgen.NuclearWastelandGenerator;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.anvil.AnvilLoader;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.generator.Generator;
import net.minestom.server.timer.TaskSchedule;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executors;

public class WorldExtension extends Extension {
    public WorldExtension() {
        super(ExtensionLoader.getExtensionsConfig().getWorld(), "world", LoadPriority.HIGH);
    }

    @Override
    public void load(InstanceContainer container) {
        String path = "worlds/" + DSCore.getPropertiesConfig().getWorldName();
        Path worldPath = Paths.get(path);
        Path regionPath = worldPath.resolve("region");

        if(Files.notExists(regionPath)) {
            DSLogger.info("Missing region folder, generating world...");
            regionPath.toFile().mkdirs();
        }
        else {
            DSLogger.info(String.format("Found world in %s. Loading...", path));
        }

        container.setChunkLoader(new AnvilLoader(path));
        container.setGenerator(new NuclearWastelandGenerator());

        //TODO: Extension method
        MinecraftServer.getSchedulerManager().buildTask(() -> {
            Executors.newCachedThreadPool().submit(() -> {
                container.getChunks().forEach(chunk -> {
                    try {
                        container.saveChunkToStorage(chunk);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            });
        }).repeat(TaskSchedule.tick(600)).schedule();
    }

//    private Generator getGenerator(WorldGenerationType type) {
//    }
}
