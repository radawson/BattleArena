package mc.alk.arena.objects.signs;

import mc.alk.arena.BattleArena;
import mc.alk.arena.objects.ArenaPlayer;
import mc.alk.arena.objects.MatchParams;
import mc.alk.arena.objects.exceptions.InvalidOptionException;
import mc.alk.arena.objects.options.JoinOptions;
import org.bukkit.Location;

/**
 * @author alkarin
 */
public class ArenaLeaveSign extends ArenaCommandSign{

    public ArenaLeaveSign(Location location, MatchParams mp, JoinOptions joinOptions) throws InvalidOptionException {
        super(location, mp, joinOptions);
    }

    @Override
    public void performAction(ArenaPlayer player) {
        BattleArena.getBAExecutor().leave(player, mp);
    }

    @Override
    public String getCommand() {
        return "Leave";
    }
}
