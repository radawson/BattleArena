package org.clockworx.battlearena.competition;

/**
 * Represents a competition-like object.
 */
public interface CompetitionLike<T extends Competition<T>> {

    /**
     * Gets the competition.
     *
     * @return the competition
     */
    T getCompetition();
}
