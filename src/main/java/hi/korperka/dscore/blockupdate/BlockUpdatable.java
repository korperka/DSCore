package hi.korperka.dscore.blockupdate;

import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.NotNull;

public interface BlockUpdatable {
    void blockUpdate(@NotNull Instance instance, @NotNull Point pos, @NotNull BlockUpdateInfo info);
}