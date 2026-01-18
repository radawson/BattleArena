package org.clockworx.battlearena.event.action.types;

import org.clockworx.battlearena.ArenaPlayer;
import org.clockworx.battlearena.competition.Competition;
import org.clockworx.battlearena.competition.map.options.TeamSpawns;
import org.clockworx.battlearena.event.action.EventAction;
import org.clockworx.battlearena.resolver.Resolvable;
import org.clockworx.battlearena.util.PositionWithRotation;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Teleports a player to a specified location.
 * <p>
 * This action moves the player to predefined locations within the competition map.
 * The location must be configured in the map's spawn settings.
 * <p>
 * <b>Parameters:</b>
 * <ul>
 *   <li>{@code location} (required): The location to teleport to. Options:
 *   <ul>
 *     <li>{@code waitroom} - Waiting area spawn</li>
 *     <li>{@code spectator} - Spectator area spawn</li>
 *     <li>{@code team_spawn} - Team spawn point (requires player to be on a team)</li>
 *     <li>{@code last_location} - Player's last saved location</li>
 *   </ul>
 *   </li>
 *   <li>{@code random} (optional): For {@code team_spawn}, whether to randomly select
 *   from available spawns. If {@code false}, spawns are assigned in round-robin order.
 *   Default: {@code false}</li>
 * </ul>
 * <p>
 * <b>Example usage:</b>
 * <pre>{@code
 * on-join:
 *   - teleport{location=waitroom}
 * on-start:
 *   - join-random-team
 *   - teleport{location=team_spawn;random=true}
 * }</pre>
 * <p>
 * <b>Important notes:</b>
 * <ul>
 *   <li>For {@code team_spawn}, the player must be on a team. Use {@link JoinRandomTeamAction}
 *   before teleporting if needed.</li>
 *   <li>Team spawns must be defined in the map configuration for the player's team.</li>
 *   <li>If {@code random=false} and multiple spawns exist, spawns are assigned in
 *   round-robin order per competition.</li>
 * </ul>
 *
 * @see JoinRandomTeamAction
 */
public class TeleportAction extends EventAction {
    private static final String LOCATION_KEY = "location";
    private static final String RANDOM = "random";

    private final Map<Competition<?>, Integer> spawnTeleportIndexQueue = new WeakHashMap<>();

    public TeleportAction(Map<String, String> params) {
        super(params, LOCATION_KEY);
    }

    @Override
    public void call(ArenaPlayer arenaPlayer, Resolvable resolvable) {
        Player player = arenaPlayer.getPlayer();
        TeleportLocation location = TeleportLocation.valueOf(this.get(LOCATION_KEY).toUpperCase(Locale.ROOT));
        boolean randomized = Boolean.parseBoolean(this.getOrDefault(RANDOM, "false"));

        PositionWithRotation pos = switch (location) {
            case LAST_LOCATION:
                Location lastLocation = arenaPlayer.getStorage().getLastLocation();
                if (lastLocation != null) {
                    yield new PositionWithRotation(lastLocation.getX(), lastLocation.getY(), lastLocation.getZ(), lastLocation.getYaw(), lastLocation.getPitch());
                } else {
                    yield null;
                }
            case WAITROOM:
                yield arenaPlayer.getCompetition().getMap().getSpawns().getWaitroomSpawn();
            case SPECTATOR:
                yield arenaPlayer.getCompetition().getMap().getSpawns().getSpectatorSpawn();
            case TEAM_SPAWN:
                Map<String, TeamSpawns> teamSpawns = arenaPlayer.getCompetition().getMap().getSpawns().getTeamSpawns();
                if (teamSpawns == null) {
                    throw new IllegalArgumentException("Team spawns not defined for competition");
                }

                if (arenaPlayer.getTeam() == null) {
                    throw new IllegalArgumentException("Team not defined for player. Ensure that the 'join-random-team' action is specified so players that are not on a team get placed on one.");
                }

                String teamName = arenaPlayer.getTeam().getName();
                if (!teamSpawns.containsKey(teamName)) {
                    throw new IllegalArgumentException("Team spawns not defined for team " + teamName);
                }

                List<PositionWithRotation> spawns = teamSpawns.get(teamName).getSpawns();

                // Fast track if there is only one spawn
                if (spawns.size() == 1) {
                    yield spawns.get(0);
                }

                if (randomized) {
                    yield spawns.get(ThreadLocalRandom.current().nextInt(spawns.size()));
                }

                // Get the spawn index for the team and increment it
                int spawnIndex = this.spawnTeleportIndexQueue.getOrDefault(arenaPlayer.getCompetition(), 0);
                this.spawnTeleportIndexQueue.put(arenaPlayer.getCompetition(), spawnIndex + 1);

                // Get the spawn at the index
                yield spawns.get(spawnIndex % spawns.size());
        };

        if (pos == null) {
            throw new IllegalArgumentException("Position not defined for location " + location);
        }

        player.teleport(pos.toLocation(arenaPlayer.getCompetition().getMap().getWorld()));
    }

    public enum TeleportLocation {
        WAITROOM,
        SPECTATOR,
        TEAM_SPAWN,
        LAST_LOCATION
    }
}
