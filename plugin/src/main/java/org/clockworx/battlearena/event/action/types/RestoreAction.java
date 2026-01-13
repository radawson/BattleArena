package org.clockworx.battlearena.event.action.types;

import org.clockworx.battlearena.ArenaPlayer;
import org.clockworx.battlearena.event.action.EventAction;
import org.clockworx.battlearena.competition.PlayerStorage;
import org.clockworx.battlearena.resolver.Resolvable;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class RestoreAction extends EventAction {
    private static final String TYPES_KEY = "types";

    public RestoreAction(Map<String, String> params) {
        super(params, TYPES_KEY);
    }

    @Override
    public void call(ArenaPlayer arenaPlayer, Resolvable resolvable) {
        String[] types = this.get(TYPES_KEY).split(",");
        PlayerStorage.Type[] toStore = new PlayerStorage.Type[types.length];
        for (int i = 0; i < types.length; i++) {
            toStore[i] = PlayerStorage.Type.valueOf(types[i].toUpperCase(Locale.ROOT));
        }

        arenaPlayer.getStorage().restore(Set.of(toStore));
    }
}
