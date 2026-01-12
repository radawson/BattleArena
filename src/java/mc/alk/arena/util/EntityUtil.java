package mc.alk.arena.util;

import org.bukkit.DyeColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Wolf;

/**
 * @deprecated As of BattleArena v3.9.12.1
 * Instead, use {@link mc.alk.battlebukkitlib.EntityUtil}
 */
public class EntityUtil {

	static final String TAMED = "tamed_";

	public static EntityType parseEntityType(String str) {
		boolean tamed = str.startsWith(TAMED);
		if (tamed){
			str = str.substring(TAMED.length(), str.length());}
        return EntityType.fromName(str);
	}

    public static void setCollarColor(Wolf wolf, DyeColor color) {
        mc.alk.battlebukkitlib.EntityUtil.setCollarColor(wolf, color);
    }
}
