package org.clockworx.battlearena.options.types;

import org.clockworx.battlearena.options.ArenaOption;

import java.util.Map;

public class BooleanArenaOption extends ArenaOption {
    private static final String ENABLED_KEY = "enabled";

    public BooleanArenaOption(Map<String, String> params) {
        super(params, ENABLED_KEY);
    }

    public boolean isEnabled() {
        return Boolean.parseBoolean(this.get(ENABLED_KEY));
    }
}
