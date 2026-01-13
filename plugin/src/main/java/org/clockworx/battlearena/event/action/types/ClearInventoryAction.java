package org.clockworx.battlearena.event.action.types;

import org.clockworx.battlearena.ArenaPlayer;
import org.clockworx.battlearena.event.action.EventAction;
import org.clockworx.battlearena.resolver.Resolvable;

import java.util.Map;

public class ClearInventoryAction extends EventAction {

    public ClearInventoryAction(Map<String, String> params) {
        super(params);
    }

    @Override
    public void call(ArenaPlayer arenaPlayer, Resolvable resolvable) {
        arenaPlayer.getPlayer().getInventory().clear();
    }
}
