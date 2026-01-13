package mc.alk.arena.events.matches;

import mc.alk.arena.competition.match.Match;

/**
 * Event that is called when a match is completed
 *
 */
public class MatchCompletedEvent extends MatchEvent {

    public MatchCompletedEvent(Match match){
        super(match);
    }
}
