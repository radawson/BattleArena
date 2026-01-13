package org.clockworx.battlearena.module.tournaments.algorithm;

import org.clockworx.battlearena.module.tournaments.Contestant;
import org.clockworx.battlearena.module.tournaments.ContestantPair;

import java.util.List;

/**
 * Represents a tournament type.
 */
public interface TournamentCalculator {

    /**
     * Advances the round of the tournament.
     *
     * @param contestants the contestants in the tournament
     * @return the result of the match
     */
    MatchResult advanceRound(List<Contestant> contestants);

    /**
     * Represents the result of a match.
     */
    record MatchResult(boolean complete, List<ContestantPair> contestantPairs) {
    }
}
