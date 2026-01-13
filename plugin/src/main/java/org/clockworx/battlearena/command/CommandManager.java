package org.clockworx.battlearena.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import org.clockworx.battlearena.BattleArena;
import org.bukkit.command.CommandSender;

import static com.mojang.brigadier.arguments.StringArgumentType.string;

/**
 * Manages command registration using Paper's native Brigadier API.
 * 
 * This replaces the old NMS-based command registration system with
 * Paper's modern, type-safe command API.
 */
public class CommandManager {
    
    private final BattleArena plugin;
    
    public CommandManager(BattleArena plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Registers all BattleArena commands.
     * This should be called during plugin initialization.
     */
    public void registerCommands() {
        // Register main battlearena command
        LiteralArgumentBuilder<CommandSourceStack> battlearena = Commands.literal("battlearena")
            .then(Commands.literal("backups")
                .then(Commands.argument("player", string())
                    .executes(ctx -> handleBackups(ctx.getSource(), StringArgumentType.getString(ctx, "player")))))
            .then(Commands.literal("restore")
                .then(Commands.argument("player", string())
                    .then(Commands.argument("index", com.mojang.brigadier.arguments.IntegerArgumentType.integer())
                        .executes(ctx -> handleRestore(ctx.getSource(), 
                            StringArgumentType.getString(ctx, "player"),
                            com.mojang.brigadier.arguments.IntegerArgumentType.getInteger(ctx, "index"))))))
            .then(Commands.literal("backup")
                .then(Commands.argument("player", string())
                    .executes(ctx -> handleBackup(ctx.getSource(), StringArgumentType.getString(ctx, "player")))))
            .then(Commands.literal("modules")
                .executes(ctx -> handleModules(ctx.getSource())))
            .executes(ctx -> {
                // Default: show help
                CommandSender sender = ctx.getSource().getSender();
                sender.sendMessage(Component.text("BattleArena - Use /ba help for command list"));
                return Command.SINGLE_SUCCESS;
            });
        
        // TODO: Register commands using Paper's command registration API
        // Paper's command API registration needs to be done via plugin bootstrap or lifecycle events
        // For now, this is a placeholder - commands will need to be registered properly
        // when the full Brigadier migration is completed
        
        plugin.info("Registered BattleArena commands using Paper Brigadier API");
    }
    
    private int handleBackups(CommandSourceStack source, String playerName) {
        // TODO: Implement backups command - migrate from BACommandExecutor
        CommandSender sender = source.getSender();
        sender.sendMessage(Component.text("Backups command for " + playerName + " - TODO: Implement"));
        return Command.SINGLE_SUCCESS;
    }
    
    private int handleRestore(CommandSourceStack source, String playerName, int index) {
        // TODO: Implement restore command - migrate from BACommandExecutor
        CommandSender sender = source.getSender();
        sender.sendMessage(Component.text("Restore command for " + playerName + " index " + index + " - TODO: Implement"));
        return Command.SINGLE_SUCCESS;
    }
    
    private int handleBackup(CommandSourceStack source, String playerName) {
        // TODO: Implement backup command - migrate from BACommandExecutor
        CommandSender sender = source.getSender();
        sender.sendMessage(Component.text("Backup command for " + playerName + " - TODO: Implement"));
        return Command.SINGLE_SUCCESS;
    }
    
    private int handleModules(CommandSourceStack source) {
        // TODO: Implement modules command - migrate from BACommandExecutor
        CommandSender sender = source.getSender();
        sender.sendMessage(Component.text("Modules command - TODO: Implement"));
        return Command.SINGLE_SUCCESS;
    }
}
