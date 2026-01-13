package org.clockworx.battlearena.feature.items;

import org.clockworx.battlearena.BattleArena;
import org.clockworx.battlearena.config.ItemStackParser;
import org.clockworx.battlearena.config.ParseException;
import org.clockworx.battlearena.config.SingularValueParser;
import org.clockworx.battlearena.feature.FeatureInstance;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.ApiStatus;

/**
 * Main entrypoint items holograms. Implementing plugins
 * should implement this interface to provide custom item
 * support.
 */
@ApiStatus.Experimental
public interface ItemsFeature extends FeatureInstance {

    /**
     * Creates an {@link ItemStack} from the given key.
     *
     * @param key the key of the item
     * @return the created item
     */
    ItemStack createItem(NamespacedKey key);

    /**
     * Creates an {@link ItemStack} from the given key and arguments.
     *
     * @param key the key of the item
     * @param arguments the arguments to create the item with
     * @return the created item
     */
    default ItemStack createItem(NamespacedKey key, SingularValueParser.ArgumentBuffer arguments) {
        try {
            ItemStack itemStack = this.createItem(key);
            if (itemStack == null) {
                BattleArena.getInstance().error("Failed to create item from key {}", key);
                return new ItemStack(Material.AIR);
            }

            return ItemStackParser.applyItemProperties(itemStack, arguments, (itemMeta, argument) -> this.onUnknownArgument(itemStack, itemMeta, argument.key(), argument.value()));
        } catch (ParseException e) {
            ParseException.handle(e);

            return new ItemStack(Material.AIR);
        }
    }

    default void onUnknownArgument(ItemStack itemStack, ItemMeta itemMeta, String key, String value) {
    }
}
