package mc.alk.arena.events.events;

import mc.alk.arena.competition.events.Event;
import org.bukkit.event.Cancellable;

/**
 * Event that is called when a BattleArena game event is opened
 *
 */
public class EventOpenEvent extends EventEvent implements Cancellable {

	/// Cancel status
    boolean cancelled = false;

    public EventOpenEvent(Event event){
        super(event);
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
