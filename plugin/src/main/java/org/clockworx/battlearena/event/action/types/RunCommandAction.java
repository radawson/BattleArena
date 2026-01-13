package org.clockworx.battlearena.event.action.types;

import org.clockworx.battlearena.ArenaPlayer;
import org.clockworx.battlearena.event.action.EventAction;
import org.clockworx.battlearena.resolver.Resolvable;
import org.bukkit.Bukkit;

import java.util.Map;

public class RunCommandAction extends EventAction {
    private static final String COMMAND_KEY = "command";
    private static final String SOURCE_KEY = "source";

    public RunCommandAction(Map<String, String> params) {
        super(params, COMMAND_KEY);
    }

    @Override
    public void call(ArenaPlayer arenaPlayer, Resolvable resolvable) {
        String command = resolvable.resolve().resolveToString(this.get(COMMAND_KEY));
        String source = this.getOrDefault(SOURCE_KEY, "player");
        if (source.equalsIgnoreCase("player")) {
            arenaPlayer.getPlayer().performCommand(command);
            return;
        }

        if (source.equalsIgnoreCase("console")) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
            return;
        }

        throw new IllegalArgumentException("Invalid source " + source + " for command " + command);
    }
}
