package org.clockworx.battlearena.competition.map.options;

import org.clockworx.battlearena.config.ArenaOption;
import org.clockworx.battlearena.util.PositionWithRotation;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Represents the spawn options for a team.
 */
public class TeamSpawns {

    @ArenaOption(name = "spawns", description = "The spawns for this team.")
    private List<PositionWithRotation> spawns;

    public TeamSpawns() {
    }

    public TeamSpawns(@Nullable List<PositionWithRotation> spawns) {
        this.spawns = spawns;
    }

    @Nullable
    public final List<PositionWithRotation> getSpawns() {
        return this.spawns;
    }
}
