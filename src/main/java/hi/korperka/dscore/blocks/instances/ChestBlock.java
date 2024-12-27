package hi.korperka.dscore.blocks.instances;

import hi.korperka.dscore.logging.DSLogger;
import hi.korperka.dscore.utils.Namespaces;
import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.ItemEntity;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockHandler;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.tag.Tag;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ChestBlock implements BlockHandler {
    private static final Tag<List<ItemStack>> TAG_ITEMS = Tag.ItemStack(Namespaces.CHEST_ITEMS).list();
    private final InventoryType type = InventoryType.CHEST_3_ROW;
    private final Component title = Component.text("Chest");

    @Override
    public void onPlace(@NotNull Placement placement) {
        Block block = placement.getBlock();
        Instance instance = placement.getInstance();
        Point pos = placement.getBlockPosition();

        // Initialize empty inventory
        ItemStack[] itemsArray = new ItemStack[type.getSize()];
        Arrays.fill(itemsArray, ItemStack.AIR);

        // Set the initial inventory state in the block's tag
        Block blockToSet = block.withTag(TAG_ITEMS, List.of(itemsArray));
        instance.setBlock(pos, blockToSet);
    }

    @Override
    public boolean onInteract(@NotNull Interaction interaction) {
        Instance instance = interaction.getInstance();
        Point pos = interaction.getBlockPosition();
        Player player = interaction.getPlayer();
        Block block = interaction.getBlock();

        // Check if chest can be opened (no solid block above)
        Block above = instance.getBlock(pos.blockX(), pos.blockY() + 1, pos.blockZ());
        if (above.isSolid()) {
            return false;
        }

        // Create inventory
        Inventory inventory = new Inventory(type, title);

        // Load items from block tag
        List<ItemStack> items = block.getTag(TAG_ITEMS);
        if (items != null) {
            for (int i = 0; i < items.size(); i++) {
                inventory.setItemStack(i, items.get(i));
            }
        }

        // Add listener to save inventory contents when modified
        inventory.addInventoryCondition((p, slot, clickType, result) -> {
            List<ItemStack> newItems = new ArrayList<>();
            for (int i = 0; i < type.getSize(); i++) {
                newItems.add(inventory.getItemStack(i));
            }
            instance.setBlock(pos, block.withTag(TAG_ITEMS, newItems));
        });

        player.openInventory(inventory);
        return true;
    }

    @Override
    public void onTouch(@NotNull Touch touch) {
        BlockHandler.super.onTouch(touch);
    }

    @Override
    public void tick(@NotNull Tick tick) {
        BlockHandler.super.tick(tick);
    }

    @Override
    public boolean isTickable() {
        return BlockHandler.super.isTickable();
    }

    @Override
    public @NotNull Collection<Tag<?>> getBlockEntityTags() {
        return BlockHandler.super.getBlockEntityTags();
    }

    @Override
    public byte getBlockEntityAction() {
        return BlockHandler.super.getBlockEntityAction();
    }

    @Override
    public @NotNull NamespaceID getNamespaceId() {
        return NamespaceID.from(Namespaces.CHEST);
    }

    @Override
    public void onDestroy(@NotNull Destroy destroy) {
        Instance instance = destroy.getInstance();
        Point pos = destroy.getBlockPosition();
        Block block = destroy.getBlock();

        // Drop items when chest is destroyed
        List<ItemStack> items = block.getTag(TAG_ITEMS);
        if (items != null) {
            Random random = new Random();
            for (ItemStack item : items) {
                if (item == null || item.isAir()) {
                    continue;
                }

                ItemEntity itemEntity = new ItemEntity(item);
                itemEntity.setInstance(instance);
                itemEntity.teleport(new Pos(
                        pos.x() + random.nextDouble(),
                        pos.y() + 0.5,
                        pos.z() + random.nextDouble()
                ));
            }
        }
    }
}