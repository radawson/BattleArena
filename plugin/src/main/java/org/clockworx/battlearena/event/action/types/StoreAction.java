package org.clockworx.battlearena.event.action.types;

import org.clockworx.battlearena.ArenaPlayer;
import org.clockworx.battlearena.event.action.EventAction;
import org.clockworx.battlearena.competition.PlayerStorage;
import org.clockworx.battlearena.resolver.Resolvable;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Stores player data (inventory, health, etc.) for later restoration.
 * <p>
 * This action saves the player's current state to {@link org.clockworx.battlearena.competition.PlayerStorage}
 * so it can be restored later (typically when the player leaves the competition).
 * <p>
 * <b>Parameters:</b>
 * <ul>
 *   <li>{@code types} (required): Comma-separated list of data types to store.
 *   Options: {@code INVENTORY}, {@code HEALTH}, {@code EXPERIENCE}, {@code GAMEMODE},
 *   {@code ATTRIBUTES}, {@code FLIGHT}, {@code EFFECTS}, {@code LOCATION}, or {@code all}</li>
 *   <li>{@code clear-state} (optional): Whether to clear the player's current state
 *   after storing. Default: {@code true}</li>
 * </ul>
 * <p>
 * <b>Example usage:</b>
 * <pre>{@code
 * on-join:
 *   - store{types=all}
 *   - store{types=INVENTORY,HEALTH,EXPERIENCE;clear-state=false}
 * }</pre>
 * <p>
 * This action should typically be used in {@code on-join} events to save the player's
 * state before modifying it. The stored data can then be restored in {@code on-leave}
 * events using {@link RestoreAction}.
 *
 * @see RestoreAction
 * @see org.clockworx.battlearena.competition.PlayerStorage
 */
public class StoreAction extends EventAction {
    private static final String TYPES_KEY = "types";
    private static final String CLEAR_STATE = "clear-state";

    public StoreAction(Map<String, String> params) {
        super(params, TYPES_KEY);
    }

    @Override
    public void call(ArenaPlayer arenaPlayer, Resolvable resolvable) {
        String[] types = this.get(TYPES_KEY).split(",");
        PlayerStorage.Type[] toStore = new PlayerStorage.Type[types.length];
        for (int i = 0; i < types.length; i++) {
            toStore[i] = PlayerStorage.Type.valueOf(types[i].toUpperCase(Locale.ROOT));
        }

        boolean clearState = Boolean.parseBoolean(this.getOrDefault(CLEAR_STATE, "true"));
        arenaPlayer.getStorage().store(Set.of(toStore), clearState);
    }
}
