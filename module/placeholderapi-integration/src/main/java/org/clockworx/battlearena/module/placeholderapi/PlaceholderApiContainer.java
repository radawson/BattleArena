package org.clockworx.battlearena.module.placeholderapi;

import org.clockworx.battlearena.BattleArena;

public class PlaceholderApiContainer {
    private final BattleArenaExpansion expansion;

    public PlaceholderApiContainer(BattleArena plugin) {
        this.expansion = new BattleArenaExpansion(plugin);
        this.expansion.register();
    }

    public void disable() {
        this.expansion.unregister();
    }
}
