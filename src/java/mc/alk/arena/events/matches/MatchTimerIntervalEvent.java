package mc.alk.arena.events.matches;

import mc.alk.arena.competition.match.Match;

/**
 * Event that is called when a BattleArena match interval is changed
 *
 */
public class MatchTimerIntervalEvent extends MatchEvent {

    final int secondsRemaining;

    public MatchTimerIntervalEvent(Match match, int remaining) {
        super(match);
        this.secondsRemaining = remaining;
    }

    /**
     * Reutns the seconds remaining in the match
     *
     * @return The seconds remaining in the match
     *
     */
    public int getSecondsRemaining(){
        return secondsRemaining;
    }

}
