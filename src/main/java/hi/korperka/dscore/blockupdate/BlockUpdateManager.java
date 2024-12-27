package hi.korperka.dscore.blockupdate;

import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.instance.InstanceChunkLoadEvent;
import net.minestom.server.event.instance.InstanceTickEvent;
import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.event.player.PlayerBlockPlaceEvent;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.WeakHashMap;

public class BlockUpdateManager {
    private static final Map<Instance, BlockUpdateManager> instance2BlockUpdateManager =
            Collections.synchronizedMap(new WeakHashMap<>());
    private static final Short2ObjectMap<BlockUpdatable> blockUpdatables = new Short2ObjectOpenHashMap<>();

    public static void registerUpdatable(short stateId, @NotNull BlockUpdatable updatable) {
        synchronized (blockUpdatables) {
            blockUpdatables.put(stateId, updatable);
        }
    }

    public static void init(@NotNull EventNode<Event> eventNode) {
        eventNode.addListener(InstanceTickEvent.class, BlockUpdateManager::instanceTick);
        eventNode.addListener(PlayerBlockBreakEvent.class, event ->
                BlockUpdateManager.from(event.getPlayer().getInstance())
                        .scheduleNeighborsUpdate(event.getBlockPosition(),
                                BlockUpdateInfo.DESTROY_BLOCK())
        );
        eventNode.addListener(PlayerBlockPlaceEvent.class, event ->
                BlockUpdateManager.from(event.getPlayer().getInstance())
                        .scheduleNeighborsUpdate(event.getBlockPosition(),
                                BlockUpdateInfo.PLACE_BLOCK())
        );
        eventNode.addListener(InstanceChunkLoadEvent.class, event -> {
            Chunk chunk = event.getChunk();
            int minY = chunk.getMinSection() * Chunk.CHUNK_SECTION_SIZE;
            int maxY = chunk.getMaxSection() * Chunk.CHUNK_SECTION_SIZE;
            int minX = chunk.getChunkX() * Chunk.CHUNK_SIZE_X;
            int minZ = chunk.getChunkZ() * Chunk.CHUNK_SIZE_Z;

            Instance instance = event.getInstance();
            BlockUpdateManager.from(instance);

            synchronized (blockUpdatables) {
                for (int x = minX; x < minX + Chunk.CHUNK_SIZE_X; x++) {
                    for (int z = minZ; z < minZ + Chunk.CHUNK_SIZE_Z; z++) {
                        for (int y = minY; y < maxY; y++) {
                            Block block = chunk.getBlock(x, y, z);
                            BlockUpdatable updatable = blockUpdatables.get((short) block.stateId());
                            if (updatable == null) {
                                continue;
                            }
                            updatable.blockUpdate(instance, new Vec(x, y, z), BlockUpdateInfo.CHUNK_LOAD());
                        }
                    }
                }
            }
        });
    }

    private static void instanceTick(InstanceTickEvent event) {
        Instance instance = event.getInstance();
        from(instance).tick(event.getDuration());
    }

    public static @NotNull BlockUpdateManager from(@NotNull Instance instance) {
        return instance2BlockUpdateManager.computeIfAbsent(instance, BlockUpdateManager::new);
    }

    private final Map<Point, BlockUpdateInfo> updateNeighbors = Collections.synchronizedMap(new LinkedHashMap<>());
    private final UpdateHandler updateHandler;

    private BlockUpdateManager(@NotNull Instance instance) {
        this.updateHandler = (pos, info) -> {
            if (instance.getBlock(pos).handler() instanceof BlockUpdatable updatable) {
                updatable.blockUpdate(instance, pos, info);
            }
        };
    }

    public interface UpdateHandler {
        void update(@NotNull Point pos, @NotNull BlockUpdateInfo info);
    }

    public void scheduleNeighborsUpdate(Point pos, BlockUpdateInfo info) {
        updateNeighbors.put(pos, info);
    }

    private void tick(int duration) {
        updateNeighbors(duration);
    }

    private void updateNeighbors(int duration) {
        if (updateNeighbors.isEmpty()) {
            return;
        }

        for (Map.Entry<Point, BlockUpdateInfo> entry : updateNeighbors.entrySet()) {
            Point pos = entry.getKey();
            BlockUpdateInfo info = entry.getValue();

            int x = pos.blockX();
            int y = pos.blockY();
            int z = pos.blockZ();

            for (int offsetX = -1; offsetX < 2; offsetX++) {
                for (int offsetY = -1; offsetY < 2; offsetY++) {
                    for (int offsetZ = -1; offsetZ < 2; offsetZ++) {
                        if (offsetX == 0 && offsetY == 0 && offsetZ == 0) {
                            continue;
                        }
                        Point blockPos = new Pos(x + offsetX, y + offsetY, z + offsetZ);
                        updateHandler.update(blockPos, info);
                    }
                }
            }
        }

        updateNeighbors.clear();
    }
}