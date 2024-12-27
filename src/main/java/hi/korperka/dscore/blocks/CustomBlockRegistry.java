package hi.korperka.dscore.blocks;

import hi.korperka.dscore.DSCore;
import hi.korperka.dscore.blocks.instances.ChestBlock;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerBlockPlaceEvent;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockHandler;

import java.util.HashMap;
import java.util.Map;

public class CustomBlockRegistry {
    private static final Map<Short, BlockHandler> BLOCK_HANDLERS = new HashMap<>();

    public static void registerAll() {
        // Register our custom barrel block
        Block.CHEST.withHandler(new ChestBlock());

        // Add event listener for block placement
        EventNode<Event> node = EventNode.all("custom-blocks");
        node.addListener(PlayerBlockPlaceEvent.class, event -> {
            Block block = event.getBlock();
            BlockHandler handler = BLOCK_HANDLERS.get((short)block.stateId());
            if (handler != null) {
                event.setBlock(block.withHandler(handler));
            }
        });

        DSCore.getEventNode().addChild(node);
    }

    public static void register(Block block, BlockHandler handler) {
        BLOCK_HANDLERS.put((short)block.stateId(), handler);
    }
}