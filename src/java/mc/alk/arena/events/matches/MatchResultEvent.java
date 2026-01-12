package mc.alk.arena.events.matches;

import mc.alk.arena.competition.match.Match;
import mc.alk.arena.objects.CompetitionResult;
import mc.alk.arena.objects.MatchResult;

import org.bukkit.event.Cancellable;

/**
 * Event that is called when a BattleArena match result is set
 *
 */
public class MatchResultEvent extends MatchEvent implements Cancellable {

    Match match;
    CompetitionResult matchResult;
    boolean cancelled;
    final boolean matchEnding;

    public MatchResultEvent(Match match, CompetitionResult matchResult) {
        super(match);
        this.match = match;
        this.matchResult = matchResult;
        matchEnding = !match.alwaysOpen();
    }

    /**
     * Returns the current match result
     *
     * @return The current match result
     */
    public CompetitionResult getMatchResult() {
        return matchResult;
    }

    /**
     * Method fixed in BattleArena v3.9.10.16
     * @since v3.9.10.16
     * @param matchResult 
     */
    public void setMatchResult(CompetitionResult matchResult) {
        this.matchResult = matchResult;
        if (matchResult instanceof MatchResult) {
            this.match.setMatchResult((MatchResult)matchResult);
        }
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    /**
     * Returns if the match is ending
     *
     * @return If the match is ending
     */
    public boolean isMatchEnding() {
        return matchEnding && !cancelled;
    }
}
