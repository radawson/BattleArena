package mc.alk.arena.events.players;

import mc.alk.arena.objects.ArenaPlayer;
import mc.alk.arena.objects.teams.ArenaTeam;

import org.bukkit.event.entity.PlayerDeathEvent;

/**
 * Event that is called when a player is killed in an arena
 *
 */
public class ArenaPlayerKillEvent extends ArenaPlayerEvent {

    final ArenaPlayer target;
    final ArenaTeam team;
    PlayerDeathEvent event;

    public ArenaPlayerKillEvent(ArenaPlayer arenaPlayer, ArenaTeam team, ArenaPlayer target) {
        super(arenaPlayer);
        this.team = team;
        this.target = target;
    }

    /**
     * Returns the team the killer is on
     *
     * @return The team the killer is on
     */
    public ArenaTeam getTeam() {
        return team;
    }

    /**
     * Returns the target that the killer killed
     *
     * @return The target that the killer killed
     */
    public ArenaPlayer getTarget(){
        return target;
    }

    /**
     * Sets the bukkit PlayerDeathEvent
     *
     * @param event The PlayerDeathEvent you want to call this on
     */
    public void setPlayerDeathEvent(PlayerDeathEvent event){
        this.event = event;
    }

    /**
     * Returns the bukkit PlayerDeathEvent
     *
     * @return The bukkit PlayerDeathEvent
     */
    public PlayerDeathEvent getPlayerDeathEvent() {
        return event;
    }

    /**
     * Returns the amount of points
     *
     * @return The amount of points
     */
    public int getPoints() {
        return 1;
    }
}
