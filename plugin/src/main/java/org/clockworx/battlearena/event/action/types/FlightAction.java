package org.clockworx.battlearena.event.action.types;

import org.clockworx.battlearena.ArenaPlayer;
import org.clockworx.battlearena.event.action.EventAction;
import org.clockworx.battlearena.resolver.Resolvable;

import java.util.Map;

public class FlightAction extends EventAction {
    private static final String FLIGHT_KEY = "enabled";

    public FlightAction(Map<String, String> params) {
        super(params, FLIGHT_KEY);
    }

    @Override
    public void call(ArenaPlayer arenaPlayer, Resolvable resolvable) {
        boolean enabled = Boolean.parseBoolean(this.get(FLIGHT_KEY));

        arenaPlayer.getPlayer().setAllowFlight(enabled);
    }
}
