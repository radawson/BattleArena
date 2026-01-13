package org.clockworx.battlearena.event.action.types;

import org.clockworx.battlearena.Arena;
import org.clockworx.battlearena.ArenaPlayer;
import org.clockworx.battlearena.competition.Competition;
import org.clockworx.battlearena.event.action.EventAction;
import org.clockworx.battlearena.resolver.Resolvable;

import java.util.Map;

public class TeardownAction extends EventAction {

    public TeardownAction(Map<String, String> params, String... requiredKeys) {
        super(params, requiredKeys);
    }

    @Override
    public void call(ArenaPlayer arenaPlayer, Resolvable resolvable) {
    }

    @Override
    public void postProcess(Arena arena, Competition<?> competition, Resolvable resolvable) {
        arena.getPlugin().removeCompetition(arena, competition);
    }
}
