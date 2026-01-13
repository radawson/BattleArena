package org.clockworx.battlearena.config;

import org.clockworx.battlearena.util.CustomEffect;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

@DocumentationSource("https://docs.battleplugins.org/books/user-guide/page/custom-effect-format")
public class CustomEffectParser<T extends CustomEffect<?>> implements ArenaConfigParser.Parser<T> {

    @SuppressWarnings("unchecked")
    @Override
    public T parse(Object object) throws ParseException {
        if (object instanceof String string) {
            return (T) deserializeSingular(string);
        }

        if (object instanceof ConfigurationSection section) {
            return (T) deserializeNode(section);
        }

        throw new ParseException("Invalid CustomEffect for object: " + object)
                .cause(ParseException.Cause.INVALID_TYPE)
                .type(this.getClass())
                .userError();
    }

    public static CustomEffect<?> deserializeSingular(String contents) throws ParseException {
        CustomEffect.EffectType<?> type;

        SingularValueParser.ArgumentBuffer buffer = SingularValueParser.parseNamed(contents, SingularValueParser.BraceStyle.CURLY, ';');
        if (!buffer.hasNext()) {
            throw new ParseException("No data found for CustomEffect")
                    .cause(ParseException.Cause.INVALID_TYPE)
                    .type(CustomEffectParser.class)
                    .userError();
        }

        SingularValueParser.Argument root = buffer.pop();
        if (root.key().equals("root")) {
            type = CustomEffect.EffectType.get(root.value());
            if (type == null) {
                throw new ParseException("Invalid CustomEffect type: " + root.value())
                        .cause(ParseException.Cause.INVALID_TYPE)
                        .type(CustomEffectParser.class)
                        .userError();
            }
        } else {
            throw new ParseException("Invalid CustomEffect root tag " + root.key())
                    .cause(ParseException.Cause.INTERNAL_ERROR)
                    .type(CustomEffectParser.class);
        }

        Map<String, String> data = new HashMap<>();
        while (buffer.hasNext()) {
            SingularValueParser.Argument argument = buffer.pop();
            data.put(argument.key(), argument.value());
        }

        CustomEffect<?> customEffect = type.create(builder -> { });
        customEffect.deserialize(data);
        return customEffect;
    }

    private static CustomEffect<?> deserializeNode(ConfigurationSection section) throws ParseException {
        String type = section.getString("type");
        if (type == null) {
            throw new ParseException("No type found for CustomEffect")
                    .cause(ParseException.Cause.INVALID_TYPE)
                    .type(CustomEffectParser.class)
                    .userError();
        }

        CustomEffect.EffectType<?> effectType = CustomEffect.EffectType.get(type);
        if (effectType == null) {
            throw new ParseException("Invalid CustomEffect type: " + type)
                    .cause(ParseException.Cause.INVALID_TYPE)
                    .type(CustomEffectParser.class)
                    .userError();
        }

        Map<String, String> data = new HashMap<>();
        for (String key : section.getKeys(false)) {
            if (key.equals("type")) {
                continue;
            }

            data.put(key, section.getString(key));
        }

        CustomEffect<?> customEffect = effectType.create(builder -> { });
        customEffect.deserialize(data);
        return customEffect;
    }
}
