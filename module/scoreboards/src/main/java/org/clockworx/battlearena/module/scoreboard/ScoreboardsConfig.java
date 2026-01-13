package org.clockworx.battlearena.module.scoreboard;

import org.clockworx.battlearena.config.ArenaOption;
import org.clockworx.battlearena.config.DocumentationSource;

import java.util.Map;

@DocumentationSource("https://docs.battleplugins.org/books/user-guide/page/scoreboards")
public class ScoreboardsConfig {

    @ArenaOption(name = "templates", description = "The scoreboard templates to use.", required = true)
    private Map<String, ScoreboardTemplate> templates;

    @ArenaOption(name = "replace-scoreboard", description = "Whether to replace the scoreboard.", required = true)
    private boolean replaceScoreboard;

    public Map<String, ScoreboardTemplate> getTemplates() {
        return Map.copyOf(this.templates);
    }

    public boolean shouldReplaceScoreboard() {
        return this.replaceScoreboard;
    }
}
