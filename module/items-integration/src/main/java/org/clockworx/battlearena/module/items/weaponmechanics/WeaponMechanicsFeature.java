package org.clockworx.battlearena.module.items.weaponmechanics;

import me.deecaad.weaponmechanics.WeaponMechanicsAPI;
import org.clockworx.battlearena.feature.PluginFeature;
import org.clockworx.battlearena.feature.items.ItemsFeature;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

public class WeaponMechanicsFeature extends PluginFeature<ItemsFeature> implements ItemsFeature {

    public WeaponMechanicsFeature() {
        super("WeaponMechanics");
    }

    @Override
    public ItemStack createItem(NamespacedKey key) {
        return WeaponMechanicsAPI.generateWeapon(key.value());
    }
}
