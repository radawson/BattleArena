package mc.alk.arena.events.matches;

import mc.alk.arena.competition.match.Match;
import mc.alk.arena.objects.MatchState;
import mc.alk.arena.objects.messaging.Channel;
import mc.alk.arena.objects.messaging.Channels;

/**
 * Event that is called when a match message is sent
 *
 */
public class MatchMessageEvent extends MatchEvent {

    final MatchState state;
    String serverMessage;
    String matchMessage;
    Channel serverChannel;

    public MatchMessageEvent(Match match, MatchState state, Channel serverChannel, String serverMessage, String matchMessage) {
        super(match);
        this.serverChannel = serverChannel;
        this.serverMessage = serverMessage;
        this.matchMessage = matchMessage;
        this.state = state;
    }

    /**
     * Returns the server message
     *
     * @return The server message
     */
    public String getServerMessage() {
        return serverMessage;
    }

    /**
     * Sets the server message
     *
     * @return serverMessage The server message you want to set
     */
    public void setServerMessage(String serverMessage) {
        this.serverMessage = serverMessage;
    }

    /**
     * Returns the match message
     *
     * @return The match message
     */
    public String getMatchMessage() {
        return matchMessage;
    }

    /**
     * Sets the match message
     *
     * @return matchMessage The match message you want to set
     */
    public void setMatchMessage(String matchMessage) {
        this.matchMessage = matchMessage;
    }

    /**
     * Returns the server channel
     *
     * @return The server channel
     */
    public Channel getServerChannel() {
        return serverChannel == null ? Channels.NullChannel : serverChannel;
    }

    /**
     * Sets the server channel
     *
     * @param serverChannel The server channel you want to set
     */
    public void setServerChannel(Channel serverChannel) {
        this.serverChannel = serverChannel;
    }

    /**
     * Returns the current match state
     *
     * @return The current match state
     */
    public MatchState getState(){
        return state;
    }
}
