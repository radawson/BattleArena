package mc.alk.arena.events.prizes;

import java.util.Collection;

import mc.alk.arena.competition.Competition;
import mc.alk.arena.objects.teams.ArenaTeam;

/**
 * Event that is called when the prizes for winners is called
 *
 */
public class ArenaWinnersPrizeEvent extends ArenaPrizeEvent {

    public ArenaWinnersPrizeEvent(Competition competition,Collection<ArenaTeam> teams) {
        super(competition, teams);
    }

}
