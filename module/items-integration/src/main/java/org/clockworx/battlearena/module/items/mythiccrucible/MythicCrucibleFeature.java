package org.clockworx.battlearena.module.items.mythiccrucible;

import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import org.clockworx.battlearena.feature.PluginFeature;
import org.clockworx.battlearena.feature.items.ItemsFeature;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

public class MythicCrucibleFeature extends PluginFeature<ItemsFeature> implements ItemsFeature {

    public MythicCrucibleFeature() {
        super("MythicCrucible");
    }

    @Override
    public ItemStack createItem(NamespacedKey key) {
        return MythicBukkit.inst().getItemManager().getItem(key.value())
                .map(item -> BukkitAdapter.adapt(item.generateItemStack(1)))
                .orElse(null);
    }
}
