package mc.alk.arena.events.prizes;

import java.util.Collection;

import mc.alk.arena.competition.Competition;
import mc.alk.arena.objects.teams.ArenaTeam;

/**
 * Event that is called when the prizes for losers is called
 *
 */
public class ArenaLosersPrizeEvent extends ArenaPrizeEvent {

    public ArenaLosersPrizeEvent(Competition competition, Collection<ArenaTeam> teams) {
        super(competition, teams);
    }

}
