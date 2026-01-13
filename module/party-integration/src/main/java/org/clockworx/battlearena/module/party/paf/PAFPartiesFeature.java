package org.clockworx.battlearena.module.party.paf;

import de.simonsator.partyandfriends.spigot.api.pafplayers.PAFPlayer;
import de.simonsator.partyandfriends.spigot.api.pafplayers.PAFPlayerManager;
import de.simonsator.partyandfriends.spigot.api.party.PartyManager;
import org.clockworx.battlearena.feature.PluginFeature;
import org.clockworx.battlearena.feature.party.PartiesFeature;
import org.clockworx.battlearena.feature.party.Party;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class PAFPartiesFeature extends PluginFeature<PartiesFeature> implements PartiesFeature {

    public PAFPartiesFeature() {
        super("Spigot-Party-API-PAF");
    }

    @Override
    public @Nullable Party getParty(UUID uuid) {
        PAFPlayer player = PAFPlayerManager.getInstance().getPlayer(uuid);
        if (player == null) {
            return null;
        }

        return new PAFParty(PartyManager.getInstance().getParty(player));
    }
}
