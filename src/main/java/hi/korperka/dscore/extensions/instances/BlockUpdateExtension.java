package hi.korperka.dscore.extensions.instances;

import hi.korperka.dscore.DSCore;
import hi.korperka.dscore.blockupdate.BlockUpdateManager;
import hi.korperka.dscore.extensions.Extension;
import hi.korperka.dscore.extensions.ExtensionLoader;
import hi.korperka.dscore.extensions.LoadPriority;
import hi.korperka.dscore.files.serializable.ExtensionSector;
import net.minestom.server.instance.InstanceContainer;

public class BlockUpdateExtension extends Extension {
    public BlockUpdateExtension() {
        super(ExtensionLoader.getExtensionsConfig().getBlockUpdate(), "block_update");
    }

    @Override
    protected void load(InstanceContainer container) {
        BlockUpdateManager.init(DSCore.getEventNode());
    }
}
