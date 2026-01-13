package org.clockworx.battlearena.event.action.types;

import org.clockworx.battlearena.ArenaPlayer;
import org.clockworx.battlearena.event.action.EventAction;
import org.clockworx.battlearena.resolver.Resolvable;
import org.bukkit.potion.PotionEffect;

import java.util.List;
import java.util.Map;

public class ClearEffectsAction extends EventAction {

    public ClearEffectsAction(Map<String, String> params) {
        super(params);
    }

    @Override
    public void call(ArenaPlayer arenaPlayer, Resolvable resolvable) {
        for (PotionEffect effect : List.copyOf(arenaPlayer.getPlayer().getActivePotionEffects())) {
            arenaPlayer.getPlayer().removePotionEffect(effect.getType());
        }
    }
}
