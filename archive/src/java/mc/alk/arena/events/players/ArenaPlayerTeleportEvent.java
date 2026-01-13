package mc.alk.arena.events.players;

import mc.alk.arena.objects.ArenaLocation;
import mc.alk.arena.objects.ArenaPlayer;
import mc.alk.arena.objects.LocationType;
import mc.alk.arena.objects.TeleportDirection;
import mc.alk.arena.objects.arenas.ArenaType;
import mc.alk.arena.objects.teams.ArenaTeam;

/**
 * Event that is called when a player teleports in an arena
 *
 */
public class ArenaPlayerTeleportEvent extends ArenaPlayerEvent {

    final ArenaTeam team;
    final ArenaLocation src;
    final ArenaLocation dest;
    final TeleportDirection direction;
    final ArenaType arenaType;

    public ArenaPlayerTeleportEvent(ArenaType at, ArenaPlayer arenaPlayer, ArenaTeam team,
            ArenaLocation src, ArenaLocation dest, TeleportDirection direction) {
        super(arenaPlayer);
        this.arenaType = at;
        this.team = team;
        this.src = src;
        this.dest = dest;
        this.direction = direction;
    }

    /**
     * Returns the arena type
     *
     * @return The arena type
     */
    public ArenaType getArenaType(){
        return arenaType;
    }

    /**
     * Returns the team the player who died is on
     *
     * @return The team the player who died is on
     */
    public ArenaTeam getTeam() {
        return team;
    }

    /**
     * Returns the team the teleport direction
     *
     * @return The teleport direction
     */
    public TeleportDirection getDirection(){
        return direction;
    }

    /**
     * Returns the source type
     *
     * @return The source type
     */
    public LocationType getSrcType(){
        return src.getType();
    }

    /**
     * Returns the destination type
     *
     * @return The destomatopm ty[e
     */
    public LocationType getDestType(){
        return dest.getType();
    }

    /**
     * Returns the arena source location
     *
     * @return The arena source location
     */
    public ArenaLocation getSrcLocation() {
        return src;
    }

    /**
     * Returns the arena destination
     *
     * @return The arena destination location
     */
    public ArenaLocation getDestLocation() {
        return dest;
    }
}
