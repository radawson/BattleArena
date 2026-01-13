package org.clockworx.battlearena.editor.stage;

import org.clockworx.battlearena.editor.EditorContext;
import org.clockworx.battlearena.editor.WizardStage;
import org.clockworx.battlearena.messages.Message;
import org.clockworx.battlearena.util.InteractionInputs;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

public class PositionInputStage<E extends EditorContext<E>> implements WizardStage<E> {
    private final Message chatMessage;
    private final Function<E, Consumer<Location>> inputConsumer;

    public PositionInputStage(Message chatMessage, Function<E, Consumer<Location>> inputConsumer) {
        this.chatMessage = chatMessage;
        this.inputConsumer = inputConsumer;
    }

    @Override
    public void enter(E context) {
        if (this.chatMessage != null) {
            context.inform(this.chatMessage);
        }

        new InteractionInputs.PositionInput(context.getPlayer()) {

            @Override
            public void onPositionInteract(Location position) {
                inputConsumer.apply(context).accept(position);
                context.advanceStage();
            }
        }.bind(context);
    }
}