package org.clockworx.battlearena.module.classes;

import org.clockworx.battlearena.Arena;
import org.clockworx.battlearena.ArenaPlayer;
import org.clockworx.battlearena.command.ArenaCommand;
import org.clockworx.battlearena.command.Argument;
import org.clockworx.battlearena.command.SubCommandExecutor;
import org.clockworx.battlearena.messages.Messages;
import org.clockworx.battlearena.options.types.BooleanArenaOption;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ClassesExecutor implements SubCommandExecutor {
    private final Classes module;
    private final Arena arena;

    public ClassesExecutor(Classes module, Arena arena) {
        this.module = module;
        this.arena = arena;
    }

    @ArenaCommand(commands = "equip", description = "Equip a class.", permissionNode = "equip")
    public void equip(Player player, @Argument(name = "class") ArenaClass arenaClass) {
        // Should not get here, but just as an additional safeguard
        // *just in case*
        if (!this.arena.isModuleEnabled(Classes.ID)) {
            ClassMessages.CLASSES_NOT_ENABLED.send(player);
            return;
        }

        ArenaPlayer arenaPlayer = ArenaPlayer.getArenaPlayer(player);
        if (arenaPlayer == null) {
            Messages.NOT_IN_ARENA.send(player);
            return;
        }

        if (this.module.isRequirePermission()) {
            if (!player.hasPermission("battlearena.classes.equip." + arenaClass.getName().toLowerCase())) {
                Messages.NO_PERMISSION.send(player);
                return;
            }
        }

        // Ensure that the current competition phase allows class equipping
        boolean canEquip = arenaPlayer.getCompetition().option(Classes.CLASS_EQUIPPING_OPTION)
                .map(BooleanArenaOption::isEnabled)
                .orElse(false);
        if (canEquip) {
            boolean equipOnlySelects = arenaPlayer.getCompetition().option(Classes.CLASS_EQUIP_ONLY_SELECTS_OPTION)
                    .map(BooleanArenaOption::isEnabled)
                    .orElse(false);

            // Check if equip only selects, and if so, set the player's class
            if (equipOnlySelects) {
                arenaPlayer.setMetadata(ArenaClass.class, arenaClass);
            } else {
                arenaClass.equip(arenaPlayer);
            }

            ClassMessages.CLASS_EQUIPPED.send(player, arenaClass.getName());
        } else {
            ClassMessages.CANNOT_EQUIP_CLASS.send(player);
        }
    }

    @Override
    public Object onVerifyArgument(CommandSender sender, String arg, Class<?> parameter) {
        if (parameter.getSimpleName().equalsIgnoreCase("arenaclass")) {
            return this.module.getClass(arg);
        }

        return null;
    }

    @Override
    public List<String> onVerifyTabComplete(String arg, Class<?> parameter) {
        if (parameter.getSimpleName().equalsIgnoreCase("arenaclass")) {
            return List.copyOf(this.module.getClasses().keySet());
        }

        return List.of();
    }

    @Override
    public String getUsageString(Class<?> parameter) {
        if (parameter.getSimpleName().equalsIgnoreCase("arenaclass")) {
            return "<class> ";
        }

        return null;
    }
}
