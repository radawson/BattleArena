package org.clockworx.battlearena.competition;

import org.clockworx.battlearena.ArenaPlayer;
import org.clockworx.battlearena.event.ArenaEventHandler;
import org.clockworx.battlearena.event.ArenaListener;
import org.clockworx.battlearena.event.player.ArenaDeathEvent;
import org.clockworx.battlearena.event.player.ArenaKillEvent;
import org.clockworx.battlearena.event.player.ArenaLifeDepleteEvent;
import org.clockworx.battlearena.event.player.ArenaLivesExhaustEvent;
import org.clockworx.battlearena.event.player.ArenaStatChangeEvent;
import org.clockworx.battlearena.stat.ArenaStats;
import org.bukkit.event.EventPriority;

class StatListener<T extends Competition<T>> implements ArenaListener, CompetitionLike<T> {
    private final LiveCompetition<T> competition;

    public StatListener(LiveCompetition<T> competition) {
        this.competition = competition;
    }

    @ArenaEventHandler(priority = EventPriority.LOWEST)
    public void onDeath(ArenaDeathEvent event) {
        event.getArenaPlayer().computeStat(ArenaStats.DEATHS, old -> (old == null ? 0 : old) + 1);
        if (event.getArena().isLivesEnabled()) {
            event.getArenaPlayer().computeStat(ArenaStats.LIVES, old -> (old == null ? 0 : old) - 1);
        }
    }

    @ArenaEventHandler(priority = EventPriority.LOWEST)
    public void onKill(ArenaKillEvent event) {
        event.getKiller().computeStat(ArenaStats.KILLS, old -> (old == null ? 0 : old) + 1);
    }

    @ArenaEventHandler(priority = EventPriority.LOWEST)
    public void onStatChange(ArenaStatChangeEvent<?> event) {
        if (event.getStat() == ArenaStats.LIVES && event.getStatHolder() instanceof ArenaPlayer player) {
            int newValue = (int) event.getNewValue();
            if (event.getOldValue() != null && (int) event.getOldValue() < newValue) {
                return;
            }

            if (newValue == 0) {
                this.competition.getArena().getEventManager().callEvent(new ArenaLivesExhaustEvent(this.competition.getArena(), player));
                return;
            }

            if (newValue < 0) {
                return;
            }

            this.competition.getArena().getEventManager().callEvent(new ArenaLifeDepleteEvent(this.competition.getArena(), player, newValue));
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public T getCompetition() {
        return (T) this.competition;
    }
}
