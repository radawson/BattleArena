package org.clockworx.battlearena.event.action.types;

import org.clockworx.battlearena.ArenaPlayer;
import org.clockworx.battlearena.event.action.EventAction;
import org.clockworx.battlearena.resolver.Resolvable;

import java.util.Map;

/**
 * Adds a delay before executing the next action.
 * <p>
 * This action pauses execution for a specified number of ticks before continuing
 * with the next action in the sequence. Useful for timing effects, respawns, or
 * sequenced actions.
 * <p>
 * <b>Parameters:</b>
 * <ul>
 *   <li>{@code ticks} (required): Number of ticks to delay. 20 ticks = 1 second</li>
 * </ul>
 * <p>
 * <b>Example usage:</b>
 * <pre>{@code
 * on-death:
 *   - clear-inventory      # Executes immediately
 *   - respawn              # Executes immediately after
 *   - delay{ticks=20}      # Waits 1 second
 *   - teleport{location=waitroom}  # Executes after delay
 * }</pre>
 * <p>
 * The delay is applied globally - all subsequent actions in the event's action list
 * are delayed, not just the next one. After the delay, execution continues with the
 * next action in sequence.
 */
public class DelayAction extends EventAction {
    private static final String TICKS_KEY = "ticks";

    public DelayAction(Map<String, String> params) {
        super(params, TICKS_KEY);
    }

    @Override
    public void call(ArenaPlayer arenaPlayer, Resolvable resolvable) {
        // No-op; this is simply used to mark when a delay
        // should occur between other actions
    }

    /**
     * Gets the number of ticks to delay.
     *
     * @return the delay in ticks
     */
    public int getTicks() {
        return Integer.parseInt(this.get(TICKS_KEY));
    }
}
