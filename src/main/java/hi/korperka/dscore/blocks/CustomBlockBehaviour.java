package hi.korperka.dscore.blocks;

import hi.korperka.dscore.utils.Namespaces;
import lombok.Getter;
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

public class CustomBlockBehaviour implements BlockHandler {
    private static final Tag<List<ItemStack>> TAG_ITEMS = Tag.ItemStack(Namespaces.ITEMS).list();
    private final InventoryType type;
    private final Component title;
    private final NamespaceID namespaceID;

    public CustomBlockBehaviour(NamespaceID namespaceID, InventoryType type, Component title) {
        this.namespaceID = namespaceID;
        this.type = type;
        this.title = title;
    }

    @Override
    public void onPlace(@NotNull Placement placement) {
        Block block = placement.getBlock();
        Instance instance = placement.getInstance();
        Point pos = placement.getBlockPosition();

        // Initialize empty inventory if not exists
        List<ItemStack> items = block.getTag(TAG_ITEMS);
        if (items == null) {
            ItemStack[] itemsArray = new ItemStack[type.getSize()];
            Arrays.fill(itemsArray, ItemStack.AIR);

            // Set initial inventory state
            Block blockToPlace = block.withTag(TAG_ITEMS, List.of(itemsArray));
            instance.setBlock(pos, blockToPlace);
        }
    }

    @Override
    public boolean onInteract(@NotNull Interaction interaction) {
        Instance instance = interaction.getInstance();
        Point pos = interaction.getBlockPosition();
        Player player = interaction.getPlayer();
        Block block = interaction.getBlock();

        // Check if block above is solid
        Block above = instance.getBlock(pos.blockX(), pos.blockY() + 1, pos.blockZ());
        if (above.isSolid()) {
            return false;
        }

        // Create inventory and load items
        Inventory inventory = new Inventory(type, title);
        List<ItemStack> items = block.getTag(TAG_ITEMS);
        if (items != null) {
            for (int i = 0; i < items.size(); i++) {
                inventory.setItemStack(i, items.get(i));
            }
        }

        // Save inventory contents when modified
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
    public boolean isTickable() {
        return false;
    }

    @Override
    public @NotNull Collection<Tag<?>> getBlockEntityTags() {
        return List.of(TAG_ITEMS);
    }

//    @Override
//    public byte getBlockEntityAction() {
//        return BlockHandler.super.getBlockEntityAction();
//    }

    @Override
    public @NotNull NamespaceID getNamespaceId() {
        return namespaceID;
    }

    @Override
    public void onDestroy(@NotNull Destroy destroy) {
        Instance instance = destroy.getInstance();
        Point pos = destroy.getBlockPosition();
        Block block = destroy.getBlock();

        // Drop items when destroyed
        List<ItemStack> items = block.getTag(TAG_ITEMS);
        if (items != null) {
            Random rng = new Random();
            for (ItemStack item : items) {
                if (item == null || item.isAir()) continue;

                ItemEntity itemEntity = new ItemEntity(item);
                itemEntity.setInstance(instance);
                itemEntity.teleport(new Pos(
                        pos.x() + rng.nextDouble(),
                        pos.y() + 0.5,
                        pos.z() + rng.nextDouble()
                ));
            }
        }
    }
}