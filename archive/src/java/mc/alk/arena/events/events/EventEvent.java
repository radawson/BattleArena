package mc.alk.arena.events.events;

import mc.alk.arena.competition.events.Event;
import mc.alk.arena.events.CompetitionEvent;

/**
 * Main event class for any BattleArena game event
 *
 */
public class EventEvent extends CompetitionEvent {

    public EventEvent(Event event) {
        super();
        setCompetition(event);
    }

    /**
     * Returns the match for this event
	 *
     * @return The match for this event
     */
    public Event getEvent() {
        return (Event) getCompetition();
    }
}
