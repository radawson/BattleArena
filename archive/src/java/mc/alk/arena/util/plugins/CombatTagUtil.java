package mc.alk.arena.util.plugins;

import mc.alk.arena.plugins.combattag.CombatTagFactory;
import mc.alk.arena.plugins.combattag.CombatTagInterface;
import org.bukkit.entity.Player;

public class CombatTagUtil {
    
    private static final CombatTagInterface tag = CombatTagFactory.newInstance();
    
    public static boolean isTagged(Player player) {
        return tag.isInCombat(player);
    }
    
    public static void untag(Player player) {
        tag.untag(player);
    }
    
    public static CombatTagInterface getCombatTagInterface() {
        return tag;
    }
}
