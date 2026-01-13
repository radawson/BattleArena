package org.clockworx.battlearena.event.action.types;

import org.clockworx.battlearena.ArenaPlayer;
import org.clockworx.battlearena.config.ParseException;
import org.clockworx.battlearena.config.PotionEffectParser;
import org.clockworx.battlearena.config.SingularValueParser;
import org.clockworx.battlearena.event.action.EventAction;
import org.clockworx.battlearena.resolver.Resolvable;
import org.bukkit.potion.PotionEffect;

import java.util.Map;

public class GiveEffectsAction extends EventAction {
    private static final String EFFECTS_KEY = "effects";

    public GiveEffectsAction(Map<String, String> params) {
        super(params, EFFECTS_KEY);
    }

    @Override
    public void call(ArenaPlayer arenaPlayer, Resolvable resolvable) {
        SingularValueParser.ArgumentBuffer buffer;
        try {
            buffer = SingularValueParser.parseUnnamed(this.get(EFFECTS_KEY), SingularValueParser.BraceStyle.SQUARE, ',');
        } catch (ParseException e) {
            ParseException.handle(e
                    .context("Action", "GiveEffectsAction")
                    .context("Arena", arenaPlayer.getArena().getName())
                    .context("Provided value", this.get(EFFECTS_KEY))
                    .cause(ParseException.Cause.INVALID_VALUE)
                    .userError()
            );
            return;
        }

        if (!buffer.hasNext()) {
            return;
        }

        while (buffer.hasNext()) {
            SingularValueParser.Argument argument = buffer.pop();
            String effectContents = argument.value();

            try {
                PotionEffect effect = PotionEffectParser.deserializeSingular(effectContents);
                arenaPlayer.getPlayer().addPotionEffect(effect);
            } catch (ParseException e) {
                ParseException.handle(e
                        .context("Action", "GiveEffectsAction")
                        .context("Arena", arenaPlayer.getArena().getName())
                        .context("Provided value", effectContents)
                        .cause(ParseException.Cause.INVALID_VALUE)
                        .userError()
                );
            }
        }
    }
}
