package mc.alk.arena.events.events;

import mc.alk.arena.competition.events.Event;

/**
 * Event that is called when a BattleArena game event is cancelled
 *
 */
public class EventCancelEvent extends EventEvent {

    public EventCancelEvent(Event event){
        super(event);
    }
}
