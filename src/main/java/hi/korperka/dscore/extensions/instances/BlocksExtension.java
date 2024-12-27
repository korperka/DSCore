package hi.korperka.dscore.extensions.instances;

import hi.korperka.dscore.DSCore;
import hi.korperka.dscore.blocks.CustomBlockRegistry;
import hi.korperka.dscore.blocks.instances.ChestBlock;
import hi.korperka.dscore.extensions.Extension;
import hi.korperka.dscore.extensions.ExtensionLoader;
import net.minestom.server.MinecraftServer;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.block.Block;

public class BlocksExtension extends Extension {
    public BlocksExtension() {
        super(ExtensionLoader.getExtensionsConfig().getBlocks(), "blocks");
    }

    @Override
    protected void load(InstanceContainer container) {
        CustomBlockRegistry.register(Block.CHEST, new ChestBlock());

        CustomBlockRegistry.registerAll();
    }
}
