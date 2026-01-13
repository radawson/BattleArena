package org.clockworx.battlearena.competition.phase.phases;

import org.clockworx.battlearena.competition.LiveCompetition;
import org.clockworx.battlearena.competition.phase.LiveCompetitionPhase;
import org.clockworx.battlearena.event.ArenaEventHandler;
import org.clockworx.battlearena.event.player.ArenaJoinEvent;
import org.clockworx.battlearena.options.Teams;
import org.clockworx.battlearena.util.IntRange;

public class WaitingPhase<T extends LiveCompetition<T>> extends LiveCompetitionPhase<T> {

    @Override
    public void onStart() {
    }

    @Override
    public void onComplete() {
    }

    @ArenaEventHandler
    public void onJoin(ArenaJoinEvent event) {
        if (this.hasEnoughPlayersToStart()) {
            this.advanceToNextPhase();
        }
    }

    public boolean hasEnoughPlayersToStart() {
        Teams teams = this.competition.getArena().getTeams();
        IntRange teamAmount = teams.getTeamAmount();
        IntRange teamSize = teams.getTeamSize();

        int minPlayers = teamAmount.getMin() * teamSize.getMin();
        return this.competition.getPlayers().size() >= minPlayers;
    }
}
