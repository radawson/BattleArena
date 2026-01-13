package org.clockworx.battlearena.event.action.types;

import org.clockworx.battlearena.ArenaPlayer;
import org.clockworx.battlearena.BattleArena;
import org.clockworx.battlearena.event.action.EventAction;
import org.clockworx.battlearena.resolver.Resolvable;
import org.bukkit.Bukkit;

import java.util.Map;

public class RespawnAction extends EventAction {

    public RespawnAction(Map<String, String> params) {
        super(params);
    }

    @Override
    public void call(ArenaPlayer arenaPlayer, Resolvable resolvable) {
        Bukkit.getScheduler().runTaskLater(BattleArena.getInstance(), arenaPlayer.getPlayer().spigot()::respawn, 1L);
    }
}
