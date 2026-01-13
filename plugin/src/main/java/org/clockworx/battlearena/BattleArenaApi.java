package org.clockworx.battlearena;

import org.clockworx.battlearena.competition.Competition;
import org.clockworx.battlearena.competition.CompetitionResult;
import org.clockworx.battlearena.competition.PlayerRole;
import org.clockworx.battlearena.competition.event.EventScheduler;
import org.clockworx.battlearena.competition.map.CompetitionMap;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * Main API entrypoint for BattleArena.
 */
public interface BattleArenaApi {

    /**
     * Returns whether the given {@link Player} is in an {@link Arena}.
     *
     * @param player the player to check
     * @return whether the player is in an arena
     */
    boolean isInArena(Player player);

    /**
     * Returns the {@link Arena} from the given name.
     *
     * @param name the name of the arena
     * @return the arena from the given name
     */
    Optional<Arena> arena(String name);

    /**
     * Returns the {@link Arena} from the given name.
     *
     * @param name the name of the arena
     * @return the arena from the given name, or null if not found
     */
    @Nullable
    Arena getArena(String name);

    /**
     * Returns all the {@link Arena}s for the plugin.
     *
     * @return all the arenas for the plugin
     */
    List<Arena> getArenas();

    /**
     * Registers the given {@link Arena}.
     *
     * @param plugin the plugin registering the arena
     * @param name the name of the arena
     * @param arenaClass the arena type to register
     */
    <T extends Arena> void registerArena(Plugin plugin, String name, Class<T> arenaClass);

    /**
     * Registers the given {@link Arena}.
     *
     * @param plugin the plugin registering the arena
     * @param name the name of the arena
     * @param arenaClass the arena type to register
     * @param arenaFactory the factory to create the arena
     */
    <T extends Arena> void registerArena(Plugin plugin, String name, Class<T> arenaClass, Supplier<T> arenaFactory);

    /**
     * Returns all the available maps for the given {@link Arena}.
     *
     * @param arena the arena to get the maps for
     * @return all the available maps for the given arena
     */
    List<? extends CompetitionMap> getMaps(Arena arena);

    /**
     * Returns the map from the given {@link Arena} and map name.
     *
     * @param arena the arena to get the map from
     * @param name the name of the map
     * @return the map from the given arena and name
     */
    Optional<? extends CompetitionMap> map(Arena arena, String name);

    /**
     * Returns the map from the given {@link Arena} and map name.
     *
     * @param arena the arena to get the map from
     * @param name the name of the map
     * @return the map from the given arena and name, or null if not found
     */
    @Nullable
    CompetitionMap getMap(Arena arena, String name);

    /**
     * Returns all the {@link Competition}s for the given {@link Arena}.
     *
     * @param arena the arena to get the competitions for
     * @return all the competitions for the given arena
     */
    List<Competition<?>> getCompetitions(Arena arena);

    /**
     * Returns all the {@link Competition}s for the given {@link Arena} and
     * specified map name.
     *
     * @param arena the arena to get the competitions for
     * @param name the name of the competition
     * @return all the competitions for the given arena and name
     */
    List<Competition<?>> getCompetitions(Arena arena, String name);

    /**
     * Finds a joinable {@link Competition} for the given {@link Player} and {@link PlayerRole}.
     *
     * @param competitions the competitions to find from
     * @param player the player to find the competition for
     * @param role the role of the player
     * @return the competition result
     */
    CompletableFuture<CompetitionResult> findJoinableCompetition(List<Competition<?>> competitions, Player player, PlayerRole role);

    /**
     * Finds a joinable {@link Competition} for the given {@link Player}s and {@link PlayerRole}.
     *
     * @param competitions the competitions to find from
     * @param players the players to find the competition for
     * @param role the role of the player
     * @return the competition result
     */
    CompletableFuture<CompetitionResult> findJoinableCompetition(List<Competition<?>> competitions, Collection<Player> players, PlayerRole role);

    /**
     * Returns the {@link EventScheduler}, which is responsible for scheduling events.
     *
     * @return the event scheduler
     */
    EventScheduler getEventScheduler();

    /**
     * Returns the path to the maps directory.
     *
     * @return the path to the maps directory
     */
    Path getMapsPath();

    /**
     * Returns whether the plugin is in debug mode.
     *
     * @return whether the plugin is in debug mode
     */
    boolean isDebugMode();

    /**
     * Sets whether the plugin is in debug mode.
     *
     * @param debugMode whether the plugin is in debug mode
     */
    void setDebugMode(boolean debugMode);

    /**
     * Returns the BattleArena API instance.
     *
     * @return the BattleArena API instance
     */
    static BattleArenaApi get() {
        return BattleArena.getInstance();
    }
}
