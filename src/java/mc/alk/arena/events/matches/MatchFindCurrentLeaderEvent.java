package mc.alk.arena.events.matches;

import mc.alk.arena.competition.match.Match;
import mc.alk.arena.objects.MatchResult;
import mc.alk.arena.objects.WinLossDraw;
import mc.alk.arena.objects.teams.ArenaTeam;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;

/**
 * Event that is called for finding a match leader
 *
 */
public class MatchFindCurrentLeaderEvent extends MatchEvent {

    final List<ArenaTeam> teams;
    MatchResult result = new MatchResult();
    final boolean matchEnding;

    public MatchFindCurrentLeaderEvent(Match match) {
        this(match, match.getTeams(), false);
    }

    public MatchFindCurrentLeaderEvent(Match match, List<ArenaTeam> teams) {
        this(match, teams, false);
    }

    public MatchFindCurrentLeaderEvent(Match match, List<ArenaTeam> teams, boolean matchEnding) {
        super(match);
        this.teams = teams;
        this.matchEnding = matchEnding;
    }

    /**
     * Returns the teams
     *
     * @return The teams
     */
    public List<ArenaTeam> getTeams() {
        return teams;
    }

    /**
     * Returns the current leaders
     *
     * @return The current leaders
     */
    public Set<ArenaTeam> getCurrentLeaders() {
        return result.getVictors();
    }

    /**
     * Sets the current leader
     *
     * @param currentLeader The current leader you want to set
     */
    public void setCurrentLeader(ArenaTeam currentLeader) {
        result.setVictor(currentLeader);
        result.setResult(WinLossDraw.WIN);
    }

    /**
     * Sets the current leaders
     *
     * @param currentLeaders The current leaders you want to set
     */
    public void setCurrentLeaders(Collection<ArenaTeam> currentLeaders) {
        result.setVictors(currentLeaders);
        result.setResult(WinLossDraw.WIN);
    }

    /**
     * Sets the current drawers
     *
     * @param currentDrawers The current drawers you want to set
     */
    public void setCurrentDrawers(Collection<ArenaTeam> currentDrawers) {
        result.setDrawers(currentDrawers);
        result.setResult(WinLossDraw.DRAW);
    }

    /**
     * Sets the current losers
     *
     * @param currentLosers The current losers you want to set
     */
    public void setCurrentLosers(Collection<ArenaTeam> currentLosers) {
        result.setLosers(currentLosers);
    }

    /**
     * Returns the match result
     *
     * @return The match result
     */
    public MatchResult getResult(){
        return result;
    }

    /**
     * Sets the match result
     *
     * @param result The match result you want to set
     */
    public void setResult(MatchResult result){
        this.result = result;
    }

    /**
     * Returns if the match is ending
     *
     * @return If the match is ending
     */
    public boolean isMatchEnding(){
        return matchEnding;
    }

    /**
     * Returns a map of the current rankings
     *
     * @return A map of the current rankings
     */
    public SortedMap<Integer,Collection<ArenaTeam>> getRanking() {
        return result.getRanking();
    }
}
