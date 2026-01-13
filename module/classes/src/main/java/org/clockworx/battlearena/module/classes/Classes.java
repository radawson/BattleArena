package org.clockworx.battlearena.module.classes;

import org.clockworx.battlearena.BattleArena;
import org.clockworx.battlearena.config.ArenaConfigParser;
import org.clockworx.battlearena.config.ParseException;
import org.clockworx.battlearena.event.BattleArenaPostInitializeEvent;
import org.clockworx.battlearena.event.BattleArenaReloadedEvent;
import org.clockworx.battlearena.event.action.EventActionType;
import org.clockworx.battlearena.event.arena.ArenaCreateExecutorEvent;
import org.clockworx.battlearena.module.ArenaModule;
import org.clockworx.battlearena.module.ArenaModuleContainer;
import org.clockworx.battlearena.module.ArenaModuleInitializer;
import org.clockworx.battlearena.options.ArenaOptionType;
import org.clockworx.battlearena.options.types.BooleanArenaOption;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 * A module that adds classes to the arena.
 */
@ArenaModule(id = Classes.ID, name = "Classes", description = "Adds classes to BattleArena.", authors = "BattlePlugins")
public class Classes implements ArenaModuleInitializer {
    public static final String ID = "classes";

    public static final EventActionType<EquipClassAction> EQUIP_CLASS_ACTION = EventActionType.create("equip-class", EquipClassAction.class, EquipClassAction::new);
    public static final ArenaOptionType<BooleanArenaOption> CLASS_EQUIPPING_OPTION = ArenaOptionType.create("class-equipping", BooleanArenaOption::new);
    public static final ArenaOptionType<BooleanArenaOption> CLASS_EQUIP_ONLY_SELECTS_OPTION = ArenaOptionType.create("class-equip-only-selects", BooleanArenaOption::new);

    private ClassesConfig classes;

    @EventHandler
    public void onPostInitialize(BattleArenaPostInitializeEvent event) {
        this.onLoad(event.getBattleArena(), true);
    }

    @EventHandler
    public void onReloaded(BattleArenaReloadedEvent event) {
        this.onLoad(event.getBattleArena(), false);
    }

    private void onLoad(BattleArena plugin, boolean initial) {
        ArenaModuleContainer<Classes> container = plugin
                .<Classes>module(ID)
                .orElseThrow();

        Path dataFolder = plugin.getDataFolder().toPath();
        Path classesPath = dataFolder.resolve("classes.yml");
        if (Files.notExists(classesPath)) {
            InputStream inputStream = container.getResource("classes.yml");
            try {
                Files.copy(inputStream, classesPath);
            } catch (Exception e) {
                plugin.error("Failed to copy classes.yml to data folder!", e);

                if (initial) {
                    container.disable("Failed to copy classes.yml to data folder!");
                }
                return;
            }
        }

        Configuration classesConfig = YamlConfiguration.loadConfiguration(classesPath.toFile());
        try {
            this.classes = ArenaConfigParser.newInstance(classesPath, ClassesConfig.class, classesConfig, plugin);
        } catch (ParseException e) {
            ParseException.handle(e);

            if (initial) {
                container.disable("Failed to parse classes.yml!");
            }
        }
    }

    @EventHandler
    public void onCreateExecutor(ArenaCreateExecutorEvent event) {
        if (!event.getArena().isModuleEnabled(ID)) {
            return;
        }

        event.registerSubExecutor(new ClassesExecutor(this, event.getArena()));
    }

    @Nullable
    public ArenaClass getClass(String name) {
        return this.classes.getClasses().get(name);
    }

    public boolean isRequirePermission() {
        return this.classes.isRequirePermission();
    }

    public Map<String, ArenaClass> getClasses() {
        return this.classes.getClasses();
    }
}
