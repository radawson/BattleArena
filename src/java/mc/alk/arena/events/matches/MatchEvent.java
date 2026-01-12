package mc.alk.arena.events.matches;

import mc.alk.arena.competition.match.Match;
import mc.alk.arena.events.CompetitionEvent;

/**
 * Main event class for any BattleArena match
 *
 */
public class MatchEvent extends CompetitionEvent {

    public MatchEvent(Match match) {
        super();
        setCompetition(match);
    }

    /**
     * Returns the match for this event
     *
     * @return The match for this event
     */
    public Match getMatch() {
        return (Match) getCompetition();
    }
}
