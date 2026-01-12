package mc.alk.arena.events.events;

import mc.alk.arena.competition.events.Event;

/**
 * Event that is called when a BattleArena game event is completed
 *
 */
public class EventCompletedEvent extends EventEvent {

    public EventCompletedEvent(Event event){
        super(event);
    }
}
