package org.clockworx.battlearena.editor.stage;

import org.clockworx.battlearena.editor.EditorContext;
import org.clockworx.battlearena.messages.Message;
import org.clockworx.battlearena.messages.Messages;

import java.util.Arrays;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Function;

public class EnumTextInputStage<E extends EditorContext<E>> extends TextInputStage<E> {

    public <T extends Enum<T>> EnumTextInputStage(Message chatMessage, Class<T> enumClass, Function<E, Consumer<T>> inputConsumer) {
        super(
                chatMessage,
                Messages.INVALID_INPUT.withContext(String.join(", ", Arrays.stream(enumClass.getEnumConstants()).map(e -> e.name().toLowerCase(Locale.ROOT)).toList())),
                (ctx, name) -> {
                    T[] constants = enumClass.getEnumConstants();
                    for (T constant : constants) {
                        if (constant.name().equalsIgnoreCase(name)) {
                            return true;
                        }
                    }

                    return false;
                    }, ctx -> str -> {
                        T[] constants = enumClass.getEnumConstants();
                        for (T constant : constants) {
                            if (constant.name().equalsIgnoreCase(str)) {
                                inputConsumer.apply(ctx).accept(constant);
                                return;
                            }
                        }
                }
        );
    }
}
