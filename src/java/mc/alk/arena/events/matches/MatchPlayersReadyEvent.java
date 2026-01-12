package mc.alk.arena.events.matches;

import mc.alk.arena.competition.match.Match;

/**
 * Event that is called when players ready for a match
 *
 */
public class MatchPlayersReadyEvent extends MatchEvent {

    public MatchPlayersReadyEvent(Match match){
        super(match);
    }
}
