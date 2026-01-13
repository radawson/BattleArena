package org.clockworx.battlearena.module.party.parties;

import com.alessiodp.parties.api.Parties;
import com.alessiodp.parties.api.interfaces.PartyPlayer;
import org.clockworx.battlearena.feature.party.Party;
import org.clockworx.battlearena.feature.party.PartyMember;

import java.util.Set;
import java.util.stream.Collectors;

public class PartiesParty implements Party {
    private final com.alessiodp.parties.api.interfaces.Party impl;

    public PartiesParty(com.alessiodp.parties.api.interfaces.Party impl) {
        this.impl = impl;
    }

    @Override
    public PartyMember getLeader() {
        if (this.impl.getLeader() == null) {
            // Return the first member of the party if the leader is null
            Set<PartyMember> members = this.getMembers();
            if (!members.isEmpty()) {
                return members.iterator().next();
            }

            return null;
        }

        PartyPlayer player = Parties.getApi().getPartyPlayer(this.impl.getLeader());
        if (player == null) {
            return null;
        }

        return new PartiesPartyMember(player);
    }

    @Override
    public Set<PartyMember> getMembers() {
        return this.impl.getOnlineMembers()
                .stream()
                .filter(m -> !m.getPlayerUUID().equals(this.impl.getLeader()))
                .map(PartiesPartyMember::new)
                .collect(Collectors.toSet());
    }
}
