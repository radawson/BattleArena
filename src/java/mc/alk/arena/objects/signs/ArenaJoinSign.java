package mc.alk.arena.objects.signs;

import mc.alk.arena.BattleArena;
import mc.alk.arena.Defaults;
import mc.alk.arena.objects.ArenaPlayer;
import mc.alk.arena.objects.MatchParams;
import mc.alk.arena.objects.exceptions.InvalidOptionException;
import mc.alk.arena.objects.options.JoinOptions;
import mc.alk.arena.util.Log;
import mc.alk.arena.util.MessageUtil;
import org.bukkit.Location;

/**
 * @author alkarin
 */
public class ArenaJoinSign extends ArenaCommandSign{

    final JoinOptions joinOptions;

    public ArenaJoinSign(Location location, MatchParams mp, JoinOptions joinOptions) throws InvalidOptionException {
        super(location, mp, joinOptions);

        this.joinOptions = joinOptions;
    }

    @Override
    public void performAction(ArenaPlayer player) {
        JoinOptions jops = joinOptions.clone();
        try {
            jops.setPlayer(player);
            BattleArena.getBAExecutor().join(player, mp, joinOptions, !Defaults.USE_SIGN_PERMS);
        } catch (InvalidOptionException e) {
            MessageUtil.sendMessage(player, e.getMessage());
        } catch (Exception e) {
            Log.printStackTrace(e);
            MessageUtil.sendMessage(player, e.getMessage());
        }
    }

    @Override
    public String getCommand() {
        return "Join";
    }
}
