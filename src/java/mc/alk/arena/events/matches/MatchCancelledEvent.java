package mc.alk.arena.events.matches;

import mc.alk.arena.competition.match.Match;

/**
 * Event that is called when a match is cancelled
 *
 */
public class MatchCancelledEvent extends MatchEvent {

    public MatchCancelledEvent(Match match){
        super(match);
    }
}
