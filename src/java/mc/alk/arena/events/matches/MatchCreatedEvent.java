package mc.alk.arena.events.matches;

import mc.alk.arena.competition.match.Match;
import mc.alk.arena.objects.joining.WaitingObject;

/**
 * Event that is called when a match is created
 *
 */
public class MatchCreatedEvent extends MatchEvent {

    WaitingObject originalObject;

    public MatchCreatedEvent(Match match, WaitingObject originalObject) {
        super(match);
        this.originalObject = originalObject;
    }

    /**
     * Returns the original waiting object
     *
     * @return The original waiting object
     */
    public WaitingObject getOriginalObject() {
        return originalObject;
    }
}
