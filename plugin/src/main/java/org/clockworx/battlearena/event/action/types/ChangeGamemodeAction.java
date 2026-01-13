package org.clockworx.battlearena.event.action.types;

import org.clockworx.battlearena.ArenaPlayer;
import org.clockworx.battlearena.event.action.EventAction;
import org.clockworx.battlearena.resolver.Resolvable;
import org.bukkit.GameMode;

import java.util.Locale;
import java.util.Map;

public class ChangeGamemodeAction extends EventAction {
    private static final String GAMEMODE_KEY = "gamemode";

    public ChangeGamemodeAction(Map<String, String> params) {
        super(params, GAMEMODE_KEY);
    }

    @Override
    public void call(ArenaPlayer arenaPlayer, Resolvable resolvable) {
        arenaPlayer.getPlayer().setGameMode(GameMode.valueOf(this.get(GAMEMODE_KEY).toUpperCase(Locale.ROOT)));
    }
}
