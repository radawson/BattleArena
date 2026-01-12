package mc.alk.arena.events.matches;

import mc.alk.arena.competition.match.Match;
import org.bukkit.event.Cancellable;

/**
 * Event that is called when a match is opened
 *
 */
public class MatchOpenEvent extends MatchEvent implements Cancellable {

    /// Cancel status
    boolean cancelled = false;

    public MatchOpenEvent(Match match){
        super(match);
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
