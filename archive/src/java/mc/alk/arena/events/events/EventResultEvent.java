package mc.alk.arena.events.events;

import mc.alk.arena.competition.events.Event;
import mc.alk.arena.objects.CompetitionResult;

/**
 * Event that is called when a BattleArena game event result is set
 *
 */
public class EventResultEvent extends EventEvent {

    final CompetitionResult result;

    public EventResultEvent(Event event, CompetitionResult result) {
        super(event);
        this.result = result;
    }

	/**
	 * Returns the current result
	 *
	 * @return The current result
	 */
    public CompetitionResult getResult(){
        return result;
    }
}
