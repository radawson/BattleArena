package org.clockworx.battlearena.options;

import org.clockworx.battlearena.config.ArenaOption;
import org.clockworx.battlearena.config.DocumentationSource;

@DocumentationSource("https://docs.battleplugins.org/books/user-guide/chapter/configuration")
public class Lives {

    @ArenaOption(name = "enabled", description = "Whether or not lives are enabled.")
    private boolean enabled = false;

    @ArenaOption(name = "amount", description = "The amount of lives each player has.")
    private int lives = 1;

    public boolean isEnabled() {
        return this.enabled;
    }

    public int getLives() {
        return this.lives;
    }
}
