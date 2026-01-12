package mc.alk.arena.plugins.combattag;

import com.trc202.CombatTagApi.CombatTagApi;
import org.bukkit.entity.Player;

/**
 * CombatTag interface if a combat logging plugin is found
 *
 * @author Nikolai
 */
public class TagsOn implements CombatTagInterface {
    
    CombatTagApi api;
    
    public TagsOn(CombatTagApi reference) {
        this.api = reference;
    }

    @Override
    public boolean isInCombat(Player player) {
        return api.isInCombat(player);
    }

    @Override
    public void untag(Player player) {
        api.untagPlayer(player);
    }

}
