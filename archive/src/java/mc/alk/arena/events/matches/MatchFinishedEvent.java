package mc.alk.arena.events.matches;

import mc.alk.arena.competition.match.Match;
import mc.alk.arena.objects.MatchState;

/**
 * Event that is called when a match is finished
 *
 */
public class MatchFinishedEvent extends MatchEvent {

    final MatchState state;

    public MatchFinishedEvent(Match match){
        super(match);
        this.state = match.getState();
    }

    /**
     * Returns the match state
     *
     * @return The match state
     *
     */
    public MatchState getState() {
        return state;
    }
}
