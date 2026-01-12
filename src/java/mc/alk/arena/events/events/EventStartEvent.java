package mc.alk.arena.events.events;

import java.util.Collection;

import mc.alk.arena.competition.events.Event;
import mc.alk.arena.objects.teams.ArenaTeam;

/**
 * Event that is called when a BattleArena game event is started
 *
 */
public class EventStartEvent extends EventEvent {

    final Collection<ArenaTeam> teams;

    public EventStartEvent(Event event, Collection<ArenaTeam> teams) {
        super(event);
        this.teams = teams;
    }

    /**
     * Returns the teams for this event
	 *
     * @return The teams for this event
     */
    public Collection<ArenaTeam> getTeams() {
        return teams;
    }
}
