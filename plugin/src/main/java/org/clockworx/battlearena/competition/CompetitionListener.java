package org.clockworx.battlearena.competition;

import org.clockworx.battlearena.Arena;
import org.clockworx.battlearena.ArenaPlayer;
import org.clockworx.battlearena.BattleArena;
import org.clockworx.battlearena.competition.map.MapType;
import org.clockworx.battlearena.competition.phase.phases.VictoryPhase;
import org.clockworx.battlearena.event.ArenaEventHandler;
import org.clockworx.battlearena.event.ArenaListener;
import org.clockworx.battlearena.event.arena.ArenaPhaseCompleteEvent;
import org.clockworx.battlearena.event.player.ArenaDeathEvent;
import org.clockworx.battlearena.event.player.ArenaKillEvent;
import org.clockworx.battlearena.event.player.ArenaLeaveEvent;
import org.clockworx.battlearena.event.player.ArenaRespawnEvent;
import org.clockworx.battlearena.storage.StorageAdapter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.Set;

class CompetitionListener<T extends Competition<T>> implements ArenaListener, CompetitionLike<T> {

    private final LiveCompetition<T> competition;

    public CompetitionListener(LiveCompetition<T> competition) {
        this.competition = competition;
    }

    @ArenaEventHandler(priority = EventPriority.HIGHEST)
    public void onPhaseComplete(ArenaPhaseCompleteEvent event) {
        if (!(event.getPhase() instanceof VictoryPhase<?>)) {
            return;
        }

        // Kick all spectators once the game is over
        if (event.getCompetition() instanceof LiveCompetition<?> liveCompetition) {
            for (ArenaPlayer spectator : Set.copyOf(liveCompetition.getSpectators())) {
                spectator.getCompetition().leave(spectator, ArenaLeaveEvent.Cause.GAME);
            }
        }

        if (event.getCompetition().getMap().getType() == MapType.DYNAMIC) {
            Arena arena = event.getArena();

            // Teardown if we are in a dynamic map
            arena.getPlugin().removeCompetition(arena, event.getCompetition());

            if (arena.getType() == CompetitionType.EVENT) {
                arena.getPlugin().getEventScheduler().eventEnded(arena, event.getCompetition());
            }
        }
    }

    @ArenaEventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent event, ArenaPlayer player) {
        // Persist data before disconnect (block to ensure data is saved)
        BattleArena plugin = player.getArena().getPlugin();
        if (plugin != null && plugin.getStorageAdapter() != null) {
            try {
                plugin.getStorageAdapter().persist(player.getStorage()).join();
            } catch (Exception e) {
                plugin.error("Failed to persist player data on disconnect for " + player.getPlayer().getName(), e);
            }
        }
        
        player.getStorage().markDisconnected();
        player.getCompetition().leave(player, ArenaLeaveEvent.Cause.DISCONNECT);
    }

    @ArenaEventHandler(priority = EventPriority.HIGHEST)
    public void onDeath(PlayerDeathEvent event, ArenaPlayer player) {
        if (event.isCancelled()) {
            return;
        }

        // Call the death event
        this.competition.getArena().getEventManager().callEvent(new ArenaDeathEvent(player));

        // Now see if the player was killed by another player
        // in this same arena
        Player killer = player.getPlayer().getKiller();
        if (killer == null) {
            return;
        }

        ArenaPlayer killerPlayer = ArenaPlayer.getArenaPlayer(killer);
        if (killerPlayer == null) {
            return;
        }

        // Check if the killer is in the same arena
        if (killerPlayer.getCompetition().equals(this.competition)) {
            this.competition.getArena().getEventManager().callEvent(new ArenaKillEvent(killerPlayer, player));
        }
    }

    @ArenaEventHandler(priority = EventPriority.HIGHEST)
    public void onRespawn(PlayerRespawnEvent event, ArenaPlayer player) {
        // Call the respawn event
        this.competition.getArena().getEventManager().callEvent(new ArenaRespawnEvent(player));
    }

    @SuppressWarnings("unchecked")
    @Override
    public T getCompetition() {
        return (T) this.competition;
    }
}
