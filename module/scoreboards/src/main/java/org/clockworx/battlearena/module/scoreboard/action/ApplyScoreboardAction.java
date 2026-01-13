package org.clockworx.battlearena.module.scoreboard.action;

import org.clockworx.battlearena.ArenaPlayer;
import org.clockworx.battlearena.BattleArena;
import org.clockworx.battlearena.event.action.EventAction;
import org.clockworx.battlearena.module.scoreboard.ScoreboardHandler;
import org.clockworx.battlearena.module.scoreboard.ScoreboardTemplate;
import org.clockworx.battlearena.module.scoreboard.Scoreboards;
import org.clockworx.battlearena.resolver.Resolvable;

import java.util.Map;
import java.util.Optional;

public class ApplyScoreboardAction extends EventAction {
    private static final String SCOREBOARD_KEY = "scoreboard";

    public ApplyScoreboardAction(Map<String, String> params) {
        super(params, SCOREBOARD_KEY);
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

        String scoreboardTemplate = this.get(SCOREBOARD_KEY);
        ScoreboardTemplate template = moduleOpt.get().getConfig().getTemplates().get(scoreboardTemplate);
        if (template == null) {
            BattleArena.getInstance().warn("Invalid scoreboard template {} for arena {}. Not applying scoreboard to player.", scoreboardTemplate, arenaPlayer.getArena().getName());
            return;
        }

        ScoreboardHandler previous = arenaPlayer.getMetadata(ScoreboardHandler.class);
        if (previous != null) {
            previous.removeScoreboard();
        }

        ScoreboardHandler handler = new ScoreboardHandler(moduleOpt.get(), arenaPlayer, template);
        handler.createScoreboard();

        arenaPlayer.setMetadata(ScoreboardHandler.class, handler);
    }
}
