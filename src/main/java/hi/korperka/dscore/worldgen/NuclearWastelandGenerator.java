package hi.korperka.dscore.worldgen;

import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.generator.GenerationUnit;
import net.minestom.server.instance.generator.Generator;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class NuclearWastelandGenerator implements Generator {

    private final Random random = new Random();

    @Override
    public void generate(@NotNull GenerationUnit unit) {
        var modifier = unit.modifier();

        // Get the bounds of this unit
        var start = unit.absoluteStart();
        var end = unit.absoluteEnd();

        // Iterate through all coordinates within the chunk
        for (int x = start.blockX(); x < end.blockX(); x++) {
            for (int z = start.blockZ(); z < end.blockZ(); z++) {
                // Determine the terrain height
                int baseHeight = 20 + random.nextInt(5);

                for (int y = start.blockY(); y <= baseHeight; y++) {
                    Block block;
                    if (y == baseHeight) {
                        // Surface layer
                        block = random.nextInt(100) < 5 ? Block.SAND : Block.DIRT;
                    } else if (y >= baseHeight - 3) {
                        // Sub-surface layer
                        block = Block.STONE;
                    } else {
                        // Deeper layer
                        block = Block.COBBLESTONE;
                    }
                    // Set block in the modifier
                    modifier.setBlock(x, y, z, block);
                }

                // Add random dead bushes above the surface
                if (random.nextInt(100) < 3) {
                    modifier.setBlock(x, baseHeight + 1, z, Block.DEAD_BUSH);
                }
            }
        }
    }
}