package mc.alk.arena.events;

import mc.alk.arena.Defaults;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

/**
 * The event manager for BattleArena
 *
 */
public class EventManager {

    /**
     * Register events for a listener
     *
     * @param listener The listener you want to register events for
     * @param plugin The plugin you want to register these events for
     */
    public static void registerEvents(Listener listener, Plugin plugin) {
        if (Defaults.TESTSERVER)
            return;

        Bukkit.getPluginManager().registerEvents(listener, plugin);
    }
}
