package org.clockworx.battlearena.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import org.clockworx.battlearena.Arena;
import org.clockworx.battlearena.BattleArena;
import org.clockworx.battlearena.competition.CompetitionType;
import org.clockworx.battlearena.competition.event.EventOptions;
import org.clockworx.battlearena.competition.event.EventType;
import org.clockworx.battlearena.messages.Messages;
import org.clockworx.battlearena.storage.PlayerSave;
import org.clockworx.battlearena.storage.StorageAdapter;
import org.clockworx.battlearena.storage.StorageManager;
import org.clockworx.battlearena.util.InventoryBackup;
import org.clockworx.battlearena.util.OptionSelector;
import org.clockworx.battlearena.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class BACommandExecutor extends BaseCommandExecutor {

    public BACommandExecutor(String parentCommand) {
        super(parentCommand);
    }

    @ArenaCommand(commands = "backups", description = "Shows backups that a player has saved.", permissionNode = "backups")
    public void backups(CommandSender sender, @Argument(name = "player") String playerName) {
        CompletableFuture.supplyAsync(() -> Bukkit.getServer().getOfflinePlayer(playerName)).thenAcceptAsync(target -> {
            if (target == null) {
                Messages.PLAYER_NOT_ONLINE.send(sender, playerName);
                return;
            }

            // Check if player has saved data
            StorageManager storageManager = BattleArena.getInstance().getStorageManager();
            if (storageManager == null || !storageManager.isAvailable()) {
                Messages.NO_BACKUPS.send(sender, target.getName());
                return;
            }

            storageManager.playerDataExists(target.getUniqueId()).thenAccept(exists -> {
                if (!exists) {
                    Messages.NO_BACKUPS.send(sender, target.getName());
                    return;
                }

                // Show that player has saved data (single backup in new system)
                Messages.HEADER.sendCentered(sender, Messages.INVENTORY_BACKUPS);
                Component message = Component.text("[1] ", Messages.SECONDARY_COLOR)
                        .append(Component.text("Current saved state", Messages.PRIMARY_COLOR))
                        .append(Component.text(" (Run: \"/ba restore " + target.getName() + " 1\" to restore)", NamedTextColor.WHITE));
                sender.sendMessage(message);
            });
        }, Bukkit.getScheduler().getMainThreadExecutor(BattleArena.getInstance()));
    }

    @ArenaCommand(commands = "restore", description = "Restores a backup for a player.", permissionNode = "restore")
    public void restore(CommandSender sender, Player target, int backupIndex) {
        // Only index 1 is supported in new system (single saved state)
        if (backupIndex != 1) {
            Messages.BACKUP_NOT_FOUND.send(sender);
            return;
        }

        StorageManager storageManager = BattleArena.getInstance().getStorageManager();
        StorageAdapter storageAdapter = BattleArena.getInstance().getStorageAdapter();
        
        if (storageManager == null || storageAdapter == null || !storageManager.isAvailable()) {
            Messages.BACKUP_NOT_FOUND.send(sender);
            return;
        }

        // Load and restore player data
        storageManager.loadPlayerData(target.getUniqueId()).thenAccept(opt -> {
            if (opt.isEmpty()) {
                Messages.BACKUP_NOT_FOUND.send(sender);
                return;
            }

            PlayerSave save = opt.get();
            Bukkit.getScheduler().runTask(BattleArena.getInstance(), () -> {
                // Restore inventory
                if (save.getInventory() != null) {
                    target.getInventory().setContents(save.getInventory());
                }
                
                // Restore other data
                if (save.getHealth() != null) {
                    target.setHealth(save.getHealth());
                }
                if (save.getHunger() != null) {
                    target.setFoodLevel(save.getHunger());
                }
                if (save.getGamemode() != null) {
                    target.setGameMode(save.getGamemode());
                }
                if (save.getTotalExp() != null) {
                    target.setTotalExperience(save.getTotalExp());
                }
                if (save.getExp() != null) {
                    target.setExp(save.getExp());
                }
                if (save.getExpLevels() != null) {
                    target.setLevel(save.getExpLevels());
                }
                if (save.getEffects() != null) {
                    for (org.bukkit.potion.PotionEffect effect : target.getActivePotionEffects()) {
                        target.removePotionEffect(effect.getType());
                    }
                    for (org.bukkit.potion.PotionEffect effect : save.getEffects()) {
                        target.addPotionEffect(effect);
                    }
                }
                
                Messages.BACKUP_RESTORED.send(sender, target.getName());
            });
        });
    }

    @ArenaCommand(commands = "backup", description = "Creates a manual backup of a player's inventory.", permissionNode = "backup")
    public void backup(CommandSender sender, Player target) {
        StorageManager storageManager = BattleArena.getInstance().getStorageManager();
        StorageAdapter storageAdapter = BattleArena.getInstance().getStorageAdapter();
        
        if (storageManager == null || storageAdapter == null || !storageManager.isAvailable()) {
            Messages.BACKUP_NOT_FOUND.send(sender); // Use generic error message
            return;
        }

        // Create PlayerSave from current player state
        PlayerSave save = new PlayerSave(target.getUniqueId(), target.getName());
        save.setInventory(target.getInventory().getContents());
        save.setHealth(target.getHealth());
        save.setHunger(target.getFoodLevel());
        save.setGamemode(target.getGameMode());
        save.setTotalExp(target.getTotalExperience());
        save.setExp(target.getExp());
        save.setExpLevels(target.getLevel());
        save.setEffects(new ArrayList<>(target.getActivePotionEffects()));
        save.setLocation(target.getLocation());

        // Save via StorageManager
        storageManager.savePlayerData(save).thenRun(() -> {
            Messages.BACKUP_CREATED.send(sender, target.getName());
        }).exceptionally(ex -> {
            BattleArena.getInstance().error("Failed to create backup for " + target.getName(), ex);
            return null;
        });
    }

    @ArenaCommand(commands = "modules", description = "Lists all modules.", permissionNode = "modules")
    public void modules(CommandSender sender) {
        Messages.HEADER.sendCentered(sender, Messages.MODULES);

        // All enabled modules
        BattleArena.getInstance().getModules()
                .stream()
                .sorted(Comparator.comparing(module -> module.module().name()))
                .forEach(module -> Messages.MODULE.send(sender, Messages.wrap(module.module().name()), Messages.ENABLED));

        // All failed modules
        BattleArena.getInstance().getFailedModules()
                .stream()
                .sorted(Comparator.comparing(e -> e.getModule().name()))
                .forEach(exception -> {
                    String moduleName = exception.getModule().name();
                    Component component = Messages.MODULE.withContext(Messages.wrap(moduleName), Messages.DISABLED)
                            .toComponent();

                    List<Component> hoverLines = new ArrayList<>();
                    hoverLines.add(Component.text("Module " + moduleName + " v" + exception.getModule().version() + " failed to load:"));
                    hoverLines.add(Component.empty());

                    for (StackTraceElement element : exception.getStackTrace()) {
                        hoverLines.add(Component.text(element.toString()));
                    }

                    component = component.hoverEvent(Component.join(JoinConfiguration.newlines(), hoverLines));
                    sender.sendMessage(component);
                });
    }

    @ArenaCommand(commands = "start", description = "Starts an event manually.", permissionNode = "start")
    public void event(CommandSender sender, Arena arena) {
        if (arena.getType() != CompetitionType.EVENT) {
            Messages.NOT_EVENT.send(sender);
            return;
        }

        arena.getPlugin().getEventScheduler().startEvent(arena, new EventOptions(
                EventType.MANUAL,
                Duration.ZERO,
                Duration.ZERO,
                Messages.MANUAL_EVENT_MESSAGE.toComponent(arena.getName(), arena.getName().toLowerCase(Locale.ROOT))
        ));
    }

    @ArenaCommand(commands = "stop", description = "Stops an event manually.", permissionNode = "stop")
    public void stop(CommandSender sender, Arena arena) {
        if (arena.getType() != CompetitionType.EVENT) {
            Messages.NOT_EVENT.send(sender);
            return;
        }

        arena.getPlugin().getEventScheduler().stopEvent(arena);
    }

    @ArenaCommand(commands = "stopall", description = "Stops all events manually.", permissionNode = "stopall")
    public void stopAll(CommandSender sender) {
        for (Arena scheduledEvent : BattleArena.getInstance().getEventScheduler().getScheduledEvents()) {
            BattleArena.getInstance().getEventScheduler().stopEvent(scheduledEvent);
        }
    }

    @ArenaCommand(commands = "schedule", description = "Schedules an event to start at the specified time.", permissionNode = "schedule")
    public void schedule(CommandSender sender, Arena arena, Duration interval) {
        if (arena.getType() != CompetitionType.EVENT) {
            Messages.NOT_EVENT.send(sender);
            return;
        }

        arena.getPlugin().getEventScheduler().scheduleEvent(arena, new EventOptions(
                EventType.SCHEDULED,
                interval,
                Duration.ZERO,
                Messages.MANUAL_EVENT_MESSAGE.toComponent(arena.getName(), arena.getName().toLowerCase(Locale.ROOT))
        ), false);
    }

    @ArenaCommand(commands = "debug", description = "Toggles debug mode.", permissionNode = "debug")
    public void debug(CommandSender sender) {
        BattleArena.getInstance().setDebugMode(!BattleArena.getInstance().isDebugMode());
        Messages.DEBUG_MODE_SET_TO.send(sender, Boolean.toString(BattleArena.getInstance().isDebugMode()));
    }

    @ArenaCommand(commands = "reload", description = "Reloads the plugin.", permissionNode = "reload")
    public void reload(CommandSender sender) {
        Messages.STARTING_RELOAD.send(sender);
        long start = System.currentTimeMillis();

        try {
            BattleArena.getInstance().reload();
        } catch (Exception e) {
            Messages.RELOAD_FAILED.send(sender);
            BattleArena.getInstance().error("Failed to reload plugin", e);
            return;
        }

        long end = System.currentTimeMillis();
        Messages.RELOAD_COMPLETE.send(sender, Util.toUnitString(end - start, TimeUnit.MILLISECONDS));
    }

    public void sendHeader(CommandSender sender) {
        Messages.HEADER.sendCentered(sender, "BattleArena");
    }
}
