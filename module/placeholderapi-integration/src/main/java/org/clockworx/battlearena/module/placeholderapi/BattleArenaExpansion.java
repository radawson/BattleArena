package org.clockworx.battlearena.module.placeholderapi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.kyori.adventure.text.Component;
import org.clockworx.battlearena.Arena;
import org.clockworx.battlearena.ArenaPlayer;
import org.clockworx.battlearena.BattleArena;
import org.clockworx.battlearena.competition.Competition;
import org.clockworx.battlearena.competition.LiveCompetition;
import org.clockworx.battlearena.competition.phase.CompetitionPhaseType;
import org.clockworx.battlearena.messages.Messages;
import org.clockworx.battlearena.resolver.Resolver;
import org.clockworx.battlearena.resolver.ResolverKey;
import org.clockworx.battlearena.resolver.ResolverKeys;
import org.clockworx.battlearena.team.ArenaTeam;
import org.clockworx.battlearena.util.Util;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BattleArenaExpansion extends PlaceholderExpansion {
    private final BattleArena plugin;

    public BattleArenaExpansion(BattleArena plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "ba";
    }

    @Override
    public @NotNull String getAuthor() {
        return "BattlePlugins";
    }

    @Override
    public @NotNull String getVersion() {
        return this.plugin.getPluginMeta().getVersion();
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        String[] split = params.split("_");

        // No data for us to parse
        if (split.length < 2) {
            return null;
        }

        ArenaPlayer arenaPlayer = ArenaPlayer.getArenaPlayer(player);
        if (arenaPlayer != null && params.startsWith("competition")) {
            String placeholder = String.join("_", split).substring("competition_".length());

            Resolver resolver = arenaPlayer.resolve();
            ResolverKey<?> resolverKey = ResolverKeys.get(placeholder.replace("_", "-"));
            if (resolverKey != null && resolver.has(resolverKey)) {
                return resolver.resolveToString(resolverKey);
            }

            // Additional placeholders for competition
            switch (placeholder) {
                case "team_color": {
                    ArenaTeam team = arenaPlayer.getTeam();
                    if (team != null) {
                        return team.getTextColor().asHexString();
                    }
                }
                case "team_color_legacy": {
                    ArenaTeam team = arenaPlayer.getTeam();
                    if (team != null) {
                        Component teamColor = Component.empty().color(team.getTextColor());
                        return Util.serializeToLegacy(teamColor);
                    }
                }
                case "team_name_formatted": {
                    ArenaTeam team = arenaPlayer.getTeam();
                    if (team != null) {
                        Component teamName = team.getFormattedName();
                        return Messages.wrap(teamName).asPlainText();
                    }
                }
                case "team_name_formatted_legacy": {
                    ArenaTeam team = arenaPlayer.getTeam();
                    if (team != null) {
                        Component teamName = team.getFormattedName();
                        return Util.serializeToLegacy(teamName);
                    }
                }
            }
        }

        // If player is null or no other placeholder resolvers have made it to this point,
        // handle more general placeholders

        String arenaName = split[0];
        Arena arena = this.plugin.getArena(arenaName);

        // No arena, so not much we can do here
        if (arena == null) {
            return null;
        }

        // Remaining text in split array
        String placeholder = String.join("_", split).substring(arenaName.length() + 1);
        if (placeholder.startsWith("map")) {
            placeholder = placeholder.substring("map_".length());

            // Next value after map_ is the actual placeholder
            String mapName = placeholder.split("_")[0];
            List<Competition<?>> competitions = this.plugin.getCompetitions(arena, mapName);
            if (competitions.isEmpty()) {
                return null;
            }

            placeholder = placeholder.substring(mapName.length() + 1);

            // Just get the first competition for now
            Competition<?> competition = competitions.get(0);
            if (!(competition instanceof LiveCompetition<?> liveCompetition)) {
                return null;
            }

            Resolver resolver = liveCompetition.resolve();
            ResolverKey<?> resolverKey = ResolverKeys.get(placeholder.replace("_", "-"));
            if (resolverKey != null && resolver.has(resolverKey)) {
                return resolver.resolveToString(resolverKey);
            }
        }

        switch (placeholder) {
            case "active_competitions": {
                return String.valueOf(this.plugin.getCompetitions(arena).size());
            }
            case "online_players": {
                int players = 0;
                for (Competition<?> competition : this.plugin.getCompetitions(arena)) {
                    players += competition.getAlivePlayerCount() + competition.getSpectatorCount();
                }

                return String.valueOf(players);
            }
            case "alive_players": {
                int online = 0;
                for (Competition<?> competition : this.plugin.getCompetitions(arena)) {
                    online += competition.getAlivePlayerCount();
                }

                return String.valueOf(online);
            }
            case "spectators": {
                int spectators = 0;
                for (Competition<?> competition : this.plugin.getCompetitions(arena)) {
                    spectators += competition.getSpectatorCount();
                }

                return String.valueOf(spectators);
            }
            case "waiting_competitions": {
                int waitingCompetitions = 0;
                for (Competition<?> competition : this.plugin.getCompetitions(arena)) {
                    CompetitionPhaseType<?, ?> phase = competition.getPhase();
                    if (CompetitionPhaseType.WAITING.equals(phase)) {
                        waitingCompetitions++;
                    }
                }
                return String.valueOf(waitingCompetitions);
            }
            case "ingame_competitions": {
                int ingameCompetitions = 0;
                for (Competition<?> competition : this.plugin.getCompetitions(arena)) {
                    CompetitionPhaseType<?, ?> phase = competition.getPhase();
                    if (CompetitionPhaseType.INGAME.equals(phase)) {
                        ingameCompetitions++;
                    }
                }
                return String.valueOf(ingameCompetitions);
            }
        }

        return null;
    }
}
