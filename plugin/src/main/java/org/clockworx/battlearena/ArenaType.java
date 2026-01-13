package org.clockworx.battlearena;

import org.bukkit.plugin.Plugin;

record ArenaType(Plugin plugin, Class<? extends Arena> arenaClass) {
}
