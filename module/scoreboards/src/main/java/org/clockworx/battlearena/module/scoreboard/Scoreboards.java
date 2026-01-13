package org.clockworx.battlearena.module.scoreboard;

import org.clockworx.battlearena.BattleArena;
import org.clockworx.battlearena.config.ArenaConfigParser;
import org.clockworx.battlearena.config.ParseException;
import org.clockworx.battlearena.event.ArenaListener;
import org.clockworx.battlearena.event.BattleArenaPostInitializeEvent;
import org.clockworx.battlearena.event.BattleArenaReloadedEvent;
import org.clockworx.battlearena.event.action.EventActionType;
import org.clockworx.battlearena.event.arena.ArenaInitializeEvent;
import org.clockworx.battlearena.module.ArenaModule;
import org.clockworx.battlearena.module.ArenaModuleContainer;
import org.clockworx.battlearena.module.ArenaModuleInitializer;
import org.clockworx.battlearena.module.scoreboard.action.ApplyScoreboardAction;
import org.clockworx.battlearena.module.scoreboard.action.RemoveScoreboardAction;
import org.clockworx.battlearena.module.scoreboard.config.ScoreboardLineCreatorContextProvider;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * A module that adds scoreboards to the arena.
 */
@ArenaModule(id = Scoreboards.ID, name = "Scoreboards", description = "Adds scoreboards to BattleArena.", authors = "BattlePlugins")
public class Scoreboards implements ArenaModuleInitializer, ArenaListener {
    public static final String ID = "scoreboards";

    public static final EventActionType<ApplyScoreboardAction> APPLY_SCOREBOARD_ACTION = EventActionType.create("apply-scoreboard", ApplyScoreboardAction.class, ApplyScoreboardAction::new);
    public static final EventActionType<RemoveScoreboardAction> REMOVE_SCOREBOARD_ACTION = EventActionType.create("remove-scoreboard", RemoveScoreboardAction.class, RemoveScoreboardAction::new);

    private ScoreboardsConfig config;

    public Scoreboards() {
        ArenaConfigParser.registerContextProvider(ScoreboardLineCreatorContextProvider.class, new ScoreboardLineCreatorContextProvider());
    }

    @EventHandler
    public void onPostInitialize(BattleArenaPostInitializeEvent event) {
        this.onLoad(event.getBattleArena(), true);
    }

    @EventHandler
    public void onReloaded(BattleArenaReloadedEvent event) {
        this.onLoad(event.getBattleArena(), false);
    }

    private void onLoad(BattleArena plugin, boolean initial) {
        ArenaModuleContainer<Scoreboards> container = plugin
                .<Scoreboards>module(ID)
                .orElseThrow();

        Path dataFolder = plugin.getDataFolder().toPath();
        Path scoreboardsPath = dataFolder.resolve("scoreboards.yml");
        if (Files.notExists(scoreboardsPath)) {
            InputStream inputStream = container.getResource("scoreboards.yml");
            try {
                Files.copy(inputStream, scoreboardsPath);
            } catch (Exception e) {
                plugin.error("Failed to copy scoreboards.yml to data folder!", e);

                if (initial) {
                    container.disable("Failed to copy scoreboards.yml to data folder!");
                }
                return;
            }
        }

        Configuration scoreboardsConfig = YamlConfiguration.loadConfiguration(scoreboardsPath.toFile());
        try {
            this.config = ArenaConfigParser.newInstance(scoreboardsPath, ScoreboardsConfig.class, scoreboardsConfig);
        } catch (ParseException e) {
            ParseException.handle(e);

            if (initial) {
                container.disable("Failed to parse scoreboards.yml!");
            }
        }
    }

    @EventHandler
    public void onArenaInitialize(ArenaInitializeEvent event) {
        if (!event.getArena().isModuleEnabled(ID)) {
            return;
        }

        event.getArena().getEventManager().registerEvents(this);
    }

    public ScoreboardsConfig getConfig() {
        return this.config;
    }
}
