package mc.alk.arena.events.players;

import mc.alk.arena.objects.ArenaPlayer;

import org.bukkit.event.Cancellable;

/**
 * Event that is called when a player joins an arena
 *
 * Signifies that the player has typed the command to Join the competition
 */
public class ArenaPlayerJoinEvent extends ArenaPlayerEvent implements Cancellable  {

    boolean cancelled = false;
    String message;

    public ArenaPlayerJoinEvent(ArenaPlayer arenaPlayer) {
        super(arenaPlayer);
    }

    /**
     * Returns the join message
     *
     * @return The join message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the join message
     *
     * @param message The join message you want to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    /**
     * Sets if you want to cancel the event with a certain message
     *
     * @param message The message you want to cancel the event with
     */
    public void cancelWithMessage(String message) {
        this.cancelled = true;
        this.message = message;
    }

}
