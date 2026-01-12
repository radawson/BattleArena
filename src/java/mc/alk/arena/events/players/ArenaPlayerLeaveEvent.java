package mc.alk.arena.events.players;

import java.util.ArrayList;
import java.util.List;

import mc.alk.arena.objects.ArenaPlayer;
import mc.alk.arena.objects.teams.ArenaTeam;

/**
 * Event that is called when a player leaves an arena
 *
 * Signifies that the player has typed the command to Join the competition
 */
public class ArenaPlayerLeaveEvent extends ArenaPlayerEvent {

    /**
     * The quit reasons
     */
    public enum QuitReason{
        QUITCOMMAND,
        QUITMC,
        KICKED,
        OTHER;
    }

    final ArenaTeam team;
    final QuitReason reason;
    boolean handledQuit = false;
    List<String> messages = null;

    public ArenaPlayerLeaveEvent(ArenaPlayer arenaPlayer, ArenaTeam team, QuitReason reason) {
        super(arenaPlayer);
        this.team = team;
        this.reason = reason;
    }

    /**
     * Returns the team the player who died is on
     *
     * @return The team the player who died is on
     */
    public ArenaTeam getTeam() {
        return team;
    }

    /**
     * Returns if the quit is handled
     *
     * @return If the quit is handled
     */
    public boolean isHandledQuit() {
        return handledQuit;
    }

    /**
     * Sets if the quit is handled
     *
     * @param handledQuit Sets if you want to handle the quit
     */
    public void setHandledQuit(boolean handledQuit) {
        this.handledQuit = handledQuit;
    }

    /**
     * Returns the quit messages
     *
     * @return The quit messages
     */
    public List<String> getMessages() {
        return messages;
    }

    /**
     * Adds quit messages
     *
     * @param str The quit message you want to add
     */
    public void addMessage(String str) {
        if (messages == null){
            messages = new ArrayList<String>();}
        if (!messages.contains(str))
            messages.add(str);
    }
}
