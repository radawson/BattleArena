package org.clockworx.battlearena.event.action.types;

import org.clockworx.battlearena.ArenaPlayer;
import org.clockworx.battlearena.competition.team.TeamManager;
import org.clockworx.battlearena.event.action.EventAction;
import org.clockworx.battlearena.resolver.Resolvable;
import org.clockworx.battlearena.team.ArenaTeam;

import java.util.Map;

public class JoinRandomTeamAction extends EventAction {
    public JoinRandomTeamAction(Map<String, String> params) {
        super(params);
    }

    @Override
    public void call(ArenaPlayer arenaPlayer, Resolvable resolvable) {
        if (arenaPlayer.getTeam() == null) {
            TeamManager teamManager = arenaPlayer.getCompetition().getTeamManager();

            ArenaTeam suitableTeam = teamManager.findSuitableTeam();
            if (suitableTeam == null) {
                arenaPlayer.getArena().getPlugin().warn("A suitable team could not be found for player {}!", arenaPlayer.getPlayer().getName());
                return;
            }

            teamManager.joinTeam(arenaPlayer, suitableTeam);
        }
    }
}
