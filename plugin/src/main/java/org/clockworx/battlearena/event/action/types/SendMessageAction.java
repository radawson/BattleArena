package org.clockworx.battlearena.event.action.types;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.clockworx.battlearena.ArenaPlayer;
import org.clockworx.battlearena.event.action.EventAction;
import org.clockworx.battlearena.resolver.Resolvable;

import java.util.Locale;
import java.util.Map;

/**
 * Sends a message to a player.
 * <p>
 * This action displays a message to the player in various formats (chat, action bar, title, subtitle).
 * Messages support MiniMessage format and resolver placeholders.
 * <p>
 * <b>Parameters:</b>
 * <ul>
 *   <li>{@code message} (required): The message text. Supports MiniMessage format
 *   (e.g., {@code <green>Hello!</green>}) and resolver placeholders (e.g., {@code {player}})</li>
 *   <li>{@code type} (optional): How to display the message. Options:
 *   <ul>
 *     <li>{@code chat} - Regular chat message (default)</li>
 *     <li>{@code action_bar} - Action bar above hotbar</li>
 *     <li>{@code title} - Title text (large text in center)</li>
 *     <li>{@code subtitle} - Subtitle text (smaller text below title)</li>
 *   </ul>
 *   </li>
 * </ul>
 * <p>
 * <b>Example usage:</b>
 * <pre>{@code
 * on-join:
 *   - send-message{message=<green>Welcome to {arena}!</green>}
 * on-kill:
 *   - send-message{message=<green>You killed {killed}!</green>;type=action_bar}
 * on-victory:
 *   - send-message{message=<gold>VICTORY!</gold>;type=title}
 * }</pre>
 * <p>
 * For broadcasting messages to multiple players, use {@link BroadcastAction}.
 *
 * @see BroadcastAction
 */
public class SendMessageAction extends EventAction {
    private static final String MESSAGE_KEY = "message";
    private static final String TYPE_KEY = "type";

    public SendMessageAction(Map<String, String> params) {
        super(params, MESSAGE_KEY);
    }

    @Override
    public void call(ArenaPlayer arenaPlayer, Resolvable resolvable) {
        String message = this.get(MESSAGE_KEY);
        MessageType messageType = MessageType.valueOf(this.getOrDefault(TYPE_KEY, MessageType.CHAT.name())
                .toUpperCase(Locale.ROOT)
        );

        Component component = resolvable.resolve().resolveToComponent(MiniMessage.miniMessage().deserialize(message));
        switch (messageType) {
            case CHAT -> arenaPlayer.getPlayer().sendMessage(component);
            case ACTION_BAR -> arenaPlayer.getPlayer().sendActionBar(component);
            case TITLE -> arenaPlayer.getPlayer().showTitle(Title.title(component, Component.empty()));
            case SUBTITLE -> arenaPlayer.getPlayer().showTitle(Title.title(Component.empty(), component));
        }
    }

    enum MessageType {
        CHAT,
        ACTION_BAR,
        TITLE,
        SUBTITLE
    }
}
