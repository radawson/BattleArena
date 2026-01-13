package mc.alk.arena.plugins.combattag;

import org.bukkit.entity.Player;

/**
 * CombatTag interface if a combat logging plugin isn't found
 *
 * @author Nikolai
 */
public class TagsOff implements CombatTagInterface {

    @Override
    public boolean isInCombat(Player player) {
        return false;
    }

    @Override
    public void untag(Player player) {
        // Do nothing
    }

}
