package org.clockworx.battlearena.module.teamheads;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.clockworx.battlearena.ArenaPlayer;
import org.clockworx.battlearena.event.action.EventAction;
import org.clockworx.battlearena.resolver.Resolvable;
import org.clockworx.battlearena.team.ArenaTeam;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class TeamHeadsAction extends EventAction {

    public TeamHeadsAction(Map<String, String> params) {
        super(params);
    }

    @Override
    public void call(ArenaPlayer arenaPlayer, Resolvable resolvable) {
        if (!arenaPlayer.getArena().isModuleEnabled(TeamHeads.ID)) {
            return;
        }

        ArenaTeam team = arenaPlayer.getTeam();
        if (team == null || !arenaPlayer.getArena().getTeams().isNamedTeams() || arenaPlayer.getArena().getTeams().isNonTeamGame()) {
            return; // Not a team game, no head
        }

        ItemStack item = team.getItem();
        if (item == null) {
            return; // No item no head
        }

        item = item.clone();
        item.editMeta(meta -> meta.displayName(
                Component.text(team.getName() + " Team", TextColor.color(team.getColor().getRGB()))
                        .decoration(TextDecoration.ITALIC, false))
        );

        arenaPlayer.getPlayer().getInventory().setHelmet(item);
    }
}
