package org.clockworx.battlearena.module.hologram;

import org.clockworx.battlearena.event.BattleArenaPostInitializeEvent;
import org.clockworx.battlearena.feature.hologram.Holograms;
import org.clockworx.battlearena.module.ArenaModule;
import org.clockworx.battlearena.module.ArenaModuleInitializer;
import org.clockworx.battlearena.module.hologram.decentholograms.DecentHologramsFeature;
import org.clockworx.battlearena.module.hologram.fancyholograms.FancyHologramsFeature;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;

/**
 * A module that allows for hooking into various hologram plugins.
 */
@ArenaModule(id = HologramIntegration.ID, name = "Hologram", description = "Adds support for hooking into various Hologram plugins.", authors = "BattlePlugins")
public class HologramIntegration implements ArenaModuleInitializer {
    public static final String ID = "hologram";

    @EventHandler
    public void onPostInitialize(BattleArenaPostInitializeEvent event) {
        if (Bukkit.getPluginManager().isPluginEnabled("FancyHolograms")) {
            Holograms.register(new FancyHologramsFeature());

            event.getBattleArena().info("FancyHolograms found. Using FancyHolograms for hologram integration.");
        }

        if (Bukkit.getPluginManager().isPluginEnabled("DecentHolograms")) {
            Holograms.register(new DecentHologramsFeature());

            event.getBattleArena().info("DecentHolograms found. Using DecentHolograms for hologram integration.");
        }
    }
}
