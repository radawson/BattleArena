package mc.alk.arena.events.players;

import mc.alk.arena.objects.ArenaPlayer;
import mc.alk.arena.objects.teams.ArenaTeam;

import org.bukkit.event.entity.PlayerDeathEvent;

/**
 * Event that is called when a player dies in an arena
 *
 */
public class ArenaPlayerDeathEvent extends ArenaPlayerEvent {

    final ArenaTeam team;
    PlayerDeathEvent event;
    boolean exiting = false;

    public ArenaPlayerDeathEvent(ArenaPlayer arenaPlayer, ArenaTeam team) {
        super(arenaPlayer);
        this.team = team;
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
     * Returns if the death is a true death
     *
     * @return If the death is a true death
     */
    public boolean isTrueDeath() {
        return event != null;
    }

    /**
     * Returns if the player is existing the arena
     *
     * @return If the player is existing the arena
     */
    public boolean isExiting() {
        return exiting;
    }

    /**
     * Sets if the player is existing the arena
     *
     * @param exiting Set the player to exist the arena
     */
    public void setExiting(boolean exiting) {
        this.exiting = exiting;
    }

}
