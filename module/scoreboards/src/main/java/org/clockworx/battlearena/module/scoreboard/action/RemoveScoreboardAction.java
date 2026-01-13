package org.clockworx.battlearena.module.scoreboard.action;

import org.clockworx.battlearena.ArenaPlayer;
import org.clockworx.battlearena.event.action.EventAction;
import org.clockworx.battlearena.module.scoreboard.ScoreboardHandler;
import org.clockworx.battlearena.module.scoreboard.Scoreboards;
import org.clockworx.battlearena.resolver.Resolvable;

import java.util.Map;
import java.util.Optional;

public class RemoveScoreboardAction extends EventAction {

    public RemoveScoreboardAction(Map<String, String> params) {
        super(params);
    }

    @Override
    public void call(ArenaPlayer arenaPlayer, Resolvable resolvable) {
        if (!arenaPlayer.getArena().isModuleEnabled(Scoreboards.ID)) {
            return;
        }

        Optional<Scoreboards> moduleOpt = arenaPlayer.getArena()
                .getPlugin()
                .<Scoreboards>module(Scoreboards.ID)
                .map(module -> module.initializer(Scoreboards.class));

        // No scoreboard module (should never happen)
        if (moduleOpt.isEmpty()) {
            return;
        }

        ScoreboardHandler previous = arenaPlayer.getMetadata(ScoreboardHandler.class);
        if (previous != null) {
            previous.removeScoreboard();
        }

        arenaPlayer.removeMetadata(ScoreboardHandler.class);
    }
}
