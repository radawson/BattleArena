package mc.alk.arena.util;

import java.util.List;
import java.util.UUID;

import mc.alk.arena.Defaults;
import mc.alk.arena.controllers.plugins.EssentialsController;
import mc.alk.arena.controllers.plugins.HeroesController;
import mc.alk.arena.objects.ArenaPlayer;
import mc.alk.arena.objects.CommandLineString;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayerUtil {

    public static int getHunger(final Player player) {
        return player.getFoodLevel();
    }

    public static void setHunger(final Player player, final Integer hunger) {
        player.setFoodLevel(hunger);
    }

    public static void setHealthP(final Player player, final Double health) {
        setHealthP(player,health, false);
    }

    public static void setHealthP(final Player player, final Double health, boolean skipHeroes) {
        if (!skipHeroes && HeroesController.enabled()){
            HeroesController.setHealthP(player,health);
            return;
        }
        double val = (player.getMaxHealth() * health/100.0);
        setHealth(player,val);
    }

    public static void setHealth(final Player player, final Double health) {
        setHealth(player,health,false);
    }

    public static void setHealth(final Player player, final Double health, boolean skipHeroes) {
        if (!skipHeroes && HeroesController.enabled()){
            HeroesController.setHealth(player,health);
            return;
        }

        mc.alk.battlebukkitlib.PlayerUtil.setHealth(player, health);
    }

    public static Double getHealth(Player player) {
        return getHealth(player,false);
    }

    public static Double getHealth(Player player, boolean skipHeroes) {
        return !skipHeroes && HeroesController.enabled() ?
                HeroesController.getHealth(player) : mc.alk.battlebukkitlib.PlayerUtil.getHealth(player);
    }

    public static void setInvulnerable(Player player, Integer invulnerableTime) {
        player.setNoDamageTicks(invulnerableTime);
        player.setLastDamage(Integer.MAX_VALUE);
    }

    public static void setGameMode(Player p, GameMode gameMode) {
        if (p.getGameMode() != gameMode){
            PermissionsUtil.givePlayerInventoryPerms(p);
            p.setGameMode(gameMode);
        }
    }

    public static void doCommands(Player p, List<CommandLineString> doCommands) {
        final String name = p.getName();
        for (CommandLineString cls: doCommands){
            try{
                CommandSender cs = cls.isConsoleSender() ? Bukkit.getConsoleSender() : p;
                if (Defaults.DEBUG_TRANSITIONS) {Log.info("BattleArena doing command '"+cls.getCommand(name)+"' as "+cs.getName());}
                doCommand(cs,cls.getCommand(name));
            } catch (Exception e){
                Log.err("[BattleArena] Error executing command as console or player");
                Log.printStackTrace(e);
            }

        }
    }

    public static void doCommand(CommandSender cs, String cmd){
        Bukkit.getServer().dispatchCommand(cs, cmd);
    }

    public static void setFlight(Player player, boolean enable) {
        if (player.getAllowFlight() != enable){
            player.setAllowFlight(enable);}
        if (player.isFlying() != enable){
            player.setFlying(enable);}
		/* Essentials (v2.10) fly just goes through bukkit, no need to call Essentials setFlight */
    }

    public static void setFlightSpeed(Player player, Float flightSpeed) {
        try {
            player.setFlySpeed(flightSpeed);
        } catch (Throwable e){
            /* ignore method not found problems */
        }
		/* Essentials (v2.10) fly just goes through bukkit, no need to call Essentials setFlySpeed */
    }

    public static void setGod(Player player, boolean enable) {
        if (EssentialsController.enabled()){
            EssentialsController.setGod(player, enable);}
    }


    public static Object getScoreboard(Player player) {
        return mc.alk.battlebukkitlib.PlayerUtil.getScoreboard(player);
    }

    public static void setScoreboard(Player player, Object scoreboard) {
        mc.alk.battlebukkitlib.PlayerUtil.setScoreboard(player, scoreboard);
    }

    public static UUID getID(ArenaPlayer player) {
        return mc.alk.battlebukkitlib.PlayerUtil.getID(player.getPlayer());
    }

    public static UUID getID(OfflinePlayer player) {
        return mc.alk.battlebukkitlib.PlayerUtil.getID(player);
    }

    public static UUID getID(Player player) {
        return mc.alk.battlebukkitlib.PlayerUtil.getID(player);
    }

    public static UUID getID(CommandSender sender)
    {
        if (sender instanceof ArenaPlayer){
            return mc.alk.battlebukkitlib.PlayerUtil.getID(((ArenaPlayer)sender).getPlayer());
        } else if (sender instanceof Player){
            return mc.alk.battlebukkitlib.PlayerUtil.getID((Player) sender);
        } else {
            return new UUID(0, sender.getName().hashCode());
        }
    }
}
