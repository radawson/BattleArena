package org.clockworx.battlearena.event.action.types;

import org.clockworx.battlearena.ArenaPlayer;
import org.clockworx.battlearena.event.action.EventAction;
import org.clockworx.battlearena.competition.PlayerStorage;
import org.clockworx.battlearena.resolver.Resolvable;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Restores previously stored player data.
 * <p>
 * This action restores player data that was previously saved using {@link StoreAction}.
 * It should typically be used in {@code on-leave} events to restore the player's
 * original state when they exit a competition.
 * <p>
 * <b>Parameters:</b>
 * <ul>
 *   <li>{@code types} (required): Comma-separated list of data types to restore.
 *   Options: {@code INVENTORY}, {@code HEALTH}, {@code EXPERIENCE}, {@code GAMEMODE},
 *   {@code ATTRIBUTES}, {@code FLIGHT}, {@code EFFECTS}, {@code LOCATION}, or {@code all}</li>
 * </ul>
 * <p>
 * <b>Example usage:</b>
 * <pre>{@code
 * on-leave:
 *   - restore{types=all}
 *   - restore{types=INVENTORY,HEALTH}
 * }</pre>
 * <p>
 * The types specified must match what was stored. If no data was stored for a type,
 * that type will be skipped during restoration.
 *
 * @see StoreAction
 * @see org.clockworx.battlearena.competition.PlayerStorage
 */
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
