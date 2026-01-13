package mc.alk.arena.events.events;

import mc.alk.arena.competition.events.Event;

/**
 * Event that is called when a BattleArena game event is finished
 *
 */
public class EventFinishedEvent extends EventEvent {

    public EventFinishedEvent(Event event){
        super(event);
    }
}
