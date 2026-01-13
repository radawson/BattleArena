package org.clockworx.battlearena.competition.phase.phases;

import org.clockworx.battlearena.ArenaPlayer;
import org.clockworx.battlearena.competition.LiveCompetition;
import org.clockworx.battlearena.competition.phase.LiveCompetitionPhase;
import org.clockworx.battlearena.messages.Messages;
import org.clockworx.battlearena.options.Lives;
import org.clockworx.battlearena.stat.ArenaStats;

public class IngamePhase<T extends LiveCompetition<T>> extends LiveCompetitionPhase<T> {

    @Override
    public void onStart() {
        Lives lives = this.getCompetition().getArena().getLives();
        if (lives != null && lives.isEnabled()) {
            for (ArenaPlayer player : this.getCompetition().getPlayers()) {
                player.setStat(ArenaStats.LIVES, lives.getLives());
            }
        }

        for (ArenaPlayer player : this.getCompetition().getPlayers()) {
            Messages.FIGHT.send(player.getPlayer());
        }
    }

    @Override
    public void onComplete() {

    }
}
