package mc.alk.arena.events;

import mc.alk.arena.competition.Competition;

/**
 * Main event class for most all Competition events
 *
 */
public class CompetitionEvent extends BAEvent {

    protected Competition competition;
        
    public CompetitionEvent() { }
        
    public CompetitionEvent(Competition competition) {
        this.competition = competition;
    }

    /**
     * Sets the competition for this event
     *
     * @param competition The competition for to set for this event
     */
    public void setCompetition(Competition competition){
        this.competition = competition;
    }

    /**
     * Returns the competition for this event
     *
     * @return The competition for this event
     */
    public Competition getCompetition(){
        return competition;
    }
}
