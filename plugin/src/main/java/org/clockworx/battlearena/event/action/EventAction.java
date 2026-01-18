package org.clockworx.battlearena.event.action;

import org.clockworx.battlearena.Arena;
import org.clockworx.battlearena.ArenaPlayer;
import org.clockworx.battlearena.competition.Competition;
import org.clockworx.battlearena.resolver.Resolvable;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Represents an action that can be executed when an event is triggered.
 * <p>
 * Actions are the building blocks of BattleArena's event system. They define
 * what happens when specific events occur (e.g., teleporting players, giving items,
 * sending messages).
 * <p>
 * Actions are configured in arena YAML files using the format:
 * <pre>{@code
 * events:
 *   on-join:
 *     - action-name{param1=value1;param2=value2}
 * }</pre>
 * <p>
 * Action execution follows a three-phase lifecycle:
 * <ol>
 *   <li><b>Pre-process</b>: {@link #preProcess(Arena, Competition, Resolvable)} is called
 *   once globally before any player processing. Use this for setup that affects all players.</li>
 *   <li><b>Process</b>: {@link #call(ArenaPlayer, Resolvable)} is called for each affected player.
 *   This is where the main action logic should be implemented.</li>
 *   <li><b>Post-process</b>: {@link #postProcess(Arena, Competition, Resolvable)} is called
 *   once globally after all player processing. Use this for cleanup or global effects.</li>
 * </ol>
 * <p>
 * Actions receive parameters via the constructor's {@code params} map. Use
 * {@link #get(String)} or {@link #getOrDefault(String, String)} to access parameter values.
 * Required parameters should be validated in the constructor.
 * <p>
 * Actions can use resolver placeholders in parameter values (e.g., "{player}", "{arena}").
 * The resolver is provided via the {@code Resolvable} parameter in action methods.
 *
 * @see ArenaEvent
 * @see ArenaEventManager
 * @see EventActionType
 */
public abstract class EventAction {
    private final Map<String, String> params;

    public EventAction(Map<String, String> params, String... requiredKeys) {
        this.params = params;
        for (String key : requiredKeys) {
            if (!params.containsKey(key)) {
                throw new IllegalArgumentException("Missing required key: " + key);
            }
        }
    }

    /**
     * Gets the parameter with the given key.
     *
     * @param key the key to get the parameter from
     * @return the parameter with the given key
     */
    @Nullable
    public String get(String key) {
        return this.params.get(key);
    }

    /**
     * Gets the parameter with the given key or the default value if the key does not exist.
     *
     * @param key the key to get the parameter from
     * @param defaultValue the default value to return if the key does not exist
     * @return the parameter with the given key or the default value if the key does not exist
     */
    public String getOrDefault(String key, String defaultValue) {
        return this.params.getOrDefault(key, defaultValue);
    }

    /**
     * Called before the action is processed for any players.
     * <p>
     * This method is called once globally before {@link #call(ArenaPlayer, Resolvable)}
     * is invoked for any players. Use this for setup that affects all players or
     * for actions that should only run once (e.g., broadcasting messages, killing entities).
     * <p>
     * The default implementation does nothing. Override this method to add pre-processing logic.
     *
     * @param arena the arena the action is occurring in
     * @param competition the competition the action is occurring in
     * @param resolvable the resolvable providing context and resolver placeholders
     */
    public void preProcess(Arena arena, Competition<?> competition, Resolvable resolvable) {
    }

    /**
     * Called after the action has been processed for all players.
     * <p>
     * This method is called once globally after {@link #call(ArenaPlayer, Resolvable)}
     * has been invoked for all affected players. Use this for cleanup or global effects
     * that should only run once.
     * <p>
     * The default implementation does nothing. Override this method to add post-processing logic.
     *
     * @param arena the arena the action is occurring in
     * @param competition the competition the action is occurring in
     * @param resolvable the resolvable providing context and resolver placeholders
     */
    public void postProcess(Arena arena, Competition<?> competition, Resolvable resolvable) {
    }

    /**
     * Executes the action for a specific player.
     * <p>
     * This method is called once for each affected player. This is where the main
     * action logic should be implemented (e.g., teleporting, giving items, sending messages).
     * <p>
     * The resolver can be used to resolve placeholders in parameter values:
     * <pre>{@code
     * String message = resolvable.resolve().resolveToString(this.get("message"));
     * // message might be "Welcome {player}!" which gets resolved to "Welcome Steve!"
     * }</pre>
     *
     * @param arenaPlayer the player to execute the action for
     * @param resolvable the resolvable providing context and resolver placeholders
     */
    public abstract void call(ArenaPlayer arenaPlayer, Resolvable resolvable);
}
