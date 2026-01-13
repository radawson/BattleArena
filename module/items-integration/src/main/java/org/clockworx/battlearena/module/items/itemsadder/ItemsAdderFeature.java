package org.clockworx.battlearena.module.items.itemsadder;

import dev.lone.itemsadder.api.CustomStack;
import org.clockworx.battlearena.feature.PluginFeature;
import org.clockworx.battlearena.feature.items.ItemsFeature;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

public class ItemsAdderFeature extends PluginFeature<ItemsFeature> implements ItemsFeature {

    public ItemsAdderFeature() {
        super("ItemsAdder");
    }

    @Override
    public ItemStack createItem(NamespacedKey key) {
        CustomStack customStack = CustomStack.getInstance(key.value());
        return customStack == null ? null : customStack.getItemStack();
    }
}
