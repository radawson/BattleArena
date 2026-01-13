package org.clockworx.battlearena.module.party.paf;

import de.simonsator.partyandfriends.spigot.api.party.PlayerParty;
import org.clockworx.battlearena.feature.party.Party;
import org.clockworx.battlearena.feature.party.PartyMember;

import java.util.Set;
import java.util.stream.Collectors;

public class PAFParty implements Party {
    private final PlayerParty impl;

    public PAFParty(PlayerParty impl) {
        this.impl = impl;
    }

    @Override
    public PartyMember getLeader() {
        return new PAFPartyMember(this.impl.getLeader());
    }

    @Override
    public Set<PartyMember> getMembers() {
        return this.impl.getPlayers().stream()
                .map(PAFPartyMember::new)
                .collect(Collectors.toSet());
    }
}
