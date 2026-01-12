package mc.alk.arena.events.events;

import mc.alk.arena.competition.events.Event;
import mc.alk.arena.objects.teams.ArenaTeam;
import org.bukkit.event.Cancellable;

/**
 * Event that is called when a team joins a BattleArena event
 *
 */
public class TeamJoinedEvent extends EventEvent implements Cancellable {

    final ArenaTeam team;

    /// Cancel status
    boolean cancelled = false;

    public TeamJoinedEvent(Event event,ArenaTeam team) {
        super(event);
        this.team = team;
    }

	/**
	 * Get the team that joined
	 *
	 * @return The team that joined
	 */
    public ArenaTeam getTeam() {
        return team;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

	@Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
