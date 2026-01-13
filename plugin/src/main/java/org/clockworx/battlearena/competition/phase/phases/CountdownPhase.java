package org.clockworx.battlearena.competition.phase.phases;

import org.clockworx.battlearena.ArenaPlayer;
import org.clockworx.battlearena.competition.LiveCompetition;
import org.clockworx.battlearena.competition.phase.LiveCompetitionPhase;
import org.clockworx.battlearena.config.ArenaOption;
import org.clockworx.battlearena.event.ArenaEventHandler;
import org.clockworx.battlearena.event.player.ArenaLeaveEvent;
import org.clockworx.battlearena.messages.Messages;
import org.clockworx.battlearena.resolver.Resolver;
import org.clockworx.battlearena.resolver.ResolverKeys;
import org.clockworx.battlearena.resolver.ResolverProvider;
import org.clockworx.battlearena.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class CountdownPhase<T extends LiveCompetition<T>> extends LiveCompetitionPhase<T> {

    @ArenaOption(name = "revert-phase", description = "Whether the phase should revert if there are not enough players to start.")
    private boolean revertPhase = true;

    @ArenaOption(name = "countdown-time", description = "The time to countdown for the competition to start.", required = true)
    private Duration countdownTime;

    @ArenaOption(name = "sound", description = "The sound to play when a countdown number is broadcasted.")
    private String sound;

    private long countdown;
    private BukkitTask countdownTask;

    @Override
    public void onStart() {
        this.countdown = this.countdownTime.toSeconds();
        this.countdownTask = Bukkit.getScheduler().runTaskTimer(this.competition.getArena().getPlugin(), () -> {
            if (this.countdown == 0) {
                this.advanceToNextPhase();
                return;
            }

            this.onCountdown();
            this.countdown--;
        }, 0L, 20L);
    }

    @ArenaEventHandler
    public void onLeave(ArenaLeaveEvent event) {
        if (!this.revertPhase || !(this.previousPhase instanceof WaitingPhase<T> waitingPhase)) {
            return;
        }

        if (!waitingPhase.hasEnoughPlayersToStart()) {
            this.countdownTask.cancel();

            this.setPhase(this.previousPhase.getType(), false);
            for (ArenaPlayer player : this.competition.getPlayers()) {
                Messages.ARENA_START_CANCELLED.send(player.getPlayer());
            }
        }
    }

    private void onCountdown() {
        if (this.countdown % 60 == 0 || this.countdown == 30 || this.countdown == 15 || this.countdown == 10 || this.countdown <= 5) {
            for (ArenaPlayer arenaPlayer : this.getCompetition().getPlayers()) {
                Player player = arenaPlayer.getPlayer();
                String timeToStart = Util.toUnitString(this.countdown, TimeUnit.SECONDS);

                Messages.ARENA_STARTS_IN.send(player, this.competition.getArena().getName(), timeToStart);

                if (this.sound != null) {
                    player.playSound(player.getLocation(), this.sound, 1, 1);
                }
            }
        }
    }

    @Override
    public void onComplete() {
        this.countdownTask.cancel();
    }

    @Override
    public Resolver resolve() {
        return super.resolve().toBuilder()
                .define(ResolverKeys.REMAINING_START_TIME, ResolverProvider.simple(Duration.ofSeconds(this.countdown + 1), Util::toTimeString))
                .build();
    }
}
