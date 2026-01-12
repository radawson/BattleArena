package mc.alk.arena.controllers;

import mc.alk.arena.BattleArena;
import mc.alk.battlebukkitlib.SchedulerUtil;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author alkarin
 *
 */
public class Scheduler {

    static int count = 0; /// count of current async timers

    public static int scheduleAsynchronousTask(Runnable task) {
        return SchedulerUtil.scheduleAsyncTask(BattleArena.getSelf(), task, 0);
    }

    public static int scheduleAsynchronousTask(Runnable task, long ticks) {
        return SchedulerUtil.scheduleAsyncTask(BattleArena.getSelf(), task, ticks);
    }

    public static int scheduleSynchronousTask(Runnable task) {
        return Bukkit.getScheduler().scheduleSyncDelayedTask(BattleArena.getSelf(), task, 0);
    }

    public static int scheduleSynchronousTask(Runnable task, long ticks) {
        return Bukkit.getScheduler().scheduleSyncDelayedTask(BattleArena.getSelf(), task, ticks);
    }

    public static int scheduleSynchronousTask(Plugin plugin, Runnable task) {
        return Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, task, 0);
    }

    public static int scheduleSynchronousTask(Plugin plugin, Runnable task, long ticks) {
        return Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, task, ticks);
    }

    public static void cancelTask(int taskId) {
        Bukkit.getScheduler().cancelTask(taskId);
    }

}
