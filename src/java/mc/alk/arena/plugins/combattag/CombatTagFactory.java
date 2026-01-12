package mc.alk.arena.plugins.combattag;

import com.trc202.CombatTag.CombatTag;
import com.trc202.CombatTagApi.CombatTagApi;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

/**
 * 
 * @author Nikolai
 */
public class CombatTagFactory {

    /**
     * Creates a new CombatTag instance
     *
     * @return A new CombatTagInterface instance
     */
    public static CombatTagInterface newInstance() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("CombatTag");
        if (plugin != null) {
            CombatTagApi api = new CombatTagApi( (CombatTag)plugin );
            return new TagsOn(api);
        }
        return new TagsOff();
    }

}
