package mc.alk.arena.events.events.tournaments;

import mc.alk.arena.competition.events.Event;
import mc.alk.arena.events.events.EventEvent;

/**
 * Event that is called when a tournament round changes
 *
 * @author alkarin
 */
public class TournamentRoundEvent extends EventEvent {

    final int round;

    public TournamentRoundEvent(Event event, int round){
        super(event);
        this.round = round;
    }

    /**
     * Returns the current round
     *
     * @return The current round
     */
    public int getRound() {
        return round;
    }

}
