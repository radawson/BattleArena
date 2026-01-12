package mc.alk.arena.controllers.plugins;

import mc.alk.arena.util.plugins.CombatTagUtil;
import org.bukkit.entity.Player;

@Deprecated
public class CombatTagInterface {

    public static void untag(Player player) {
        CombatTagUtil.untag(player);
    }

    public static boolean isTagged(Player player) {
        return CombatTagUtil.isTagged(player);
    }

}
