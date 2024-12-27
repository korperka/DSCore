package hi.korperka.dscore.files;

import hi.korperka.dscore.files.config.Config;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.MinecraftServer;
import net.minestom.server.extras.MojangAuth;
import net.minestom.server.world.Difficulty;

@Getter @Setter
public class PropertiesConfig implements Config {
    private String ip = "0.0.0.0";
    private int port = 25565;
    private String worldName = "world";
    private String brandName = "DuckShield";
    private boolean onlineMode = true;
    private int chunkViewDistance = 8;
    private int entityViewDistance = 5;
    private int compressionThreshold = 256;
    private Difficulty difficulty = Difficulty.NORMAL;

    public void applyServerSettings() {
        if(onlineMode) {
            MojangAuth.init();
        }

        //TODO: proxyyy
        MinecraftServer.setCompressionThreshold(compressionThreshold);
        MinecraftServer.setBrandName(brandName);
        MinecraftServer.setDifficulty(difficulty);
    }
}
