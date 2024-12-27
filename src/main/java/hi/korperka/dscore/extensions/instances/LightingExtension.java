package hi.korperka.dscore.extensions.instances;

import hi.korperka.dscore.extensions.Extension;
import hi.korperka.dscore.extensions.ExtensionLoader;
import hi.korperka.dscore.files.config.ConfigLoader;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.utils.chunk.ChunkUtils;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class LightingExtension extends Extension {
    public LightingExtension() {
        super(ExtensionLoader.getExtensionsConfig().getLighting(), "lighting");
    }

    @Override
    public void load(InstanceContainer container) {
        container.setChunkSupplier(LightingChunk::new);

        var chunks = new ArrayList<CompletableFuture<Chunk>>();
        ChunkUtils.forChunksInRange(0, 0, 32, (x, z) -> chunks.add(container.loadChunk(x, z)));

        CompletableFuture.runAsync(() -> {
            CompletableFuture.allOf(chunks.toArray(CompletableFuture[]::new)).join();
            LightingChunk.relight(container, container.getChunks());
        });
    }
}
