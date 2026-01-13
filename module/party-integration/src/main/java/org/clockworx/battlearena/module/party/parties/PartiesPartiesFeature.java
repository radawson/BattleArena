package org.clockworx.battlearena.module.party.parties;

import com.alessiodp.parties.api.Parties;
import org.clockworx.battlearena.feature.PluginFeature;
import org.clockworx.battlearena.feature.party.PartiesFeature;
import org.clockworx.battlearena.feature.party.Party;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class PartiesPartiesFeature extends PluginFeature<PartiesPartiesFeature> implements PartiesFeature {

    public PartiesPartiesFeature() {
        super("Parties");
    }

    @Override
    public @Nullable Party getParty(UUID uuid) {
        com.alessiodp.parties.api.interfaces.Party party = Parties.getApi().getPartyOfPlayer(uuid);
        if (party == null) {
            return null;
        }

        return new PartiesParty(party);
    }
}
