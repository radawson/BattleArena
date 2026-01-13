package mc.alk.arena.events.teams;

import mc.alk.arena.events.BAEvent;
import mc.alk.arena.objects.ArenaParams;
import mc.alk.arena.objects.pairs.JoinResult;
import mc.alk.arena.objects.teams.ArenaTeam;

/**
 * Event that is called when a team joins a queue
 *
 */
public class TeamJoinedQueueEvent extends BAEvent {

    final ArenaTeam team;
    final int playersInQueue;
//	final int teamsInQueue;
    final int pos;
    final Long timeToStart;
    final ArenaParams params;

    public TeamJoinedQueueEvent(ArenaTeam team, JoinResult qpp) {
        this.team = team;
        this.playersInQueue = qpp.playersInQueue;
        this.pos = qpp.pos;
        this.timeToStart = qpp.time;
        this.params = qpp.params;
//		this.teamsInQueue = qpp.teamsInQueue;
    }

    /**
     * Returns the team that joined the queue
     *
     * @return The team that joined the queue
     */
    public ArenaTeam getTeam() {
        return team;
    }

    /**
     * Returns the players in the queue
     *
     * @return The players in the queue
     */
    public int getPlayersInQueue() {
        return playersInQueue;
    }

    /**
     * Returns the position
     *
     * @return The position
     */
    public int getPos() {
        return pos;
    }

    /**
     * Returns the amount of time before the arena start
     *
     * @return The amount of time before the arena starts
     */
    public Long getTimeToStart() {
        return timeToStart;
    }

    /**
     * Returns the arena parameters
     *
     * @return The arena parameters
     */
    public ArenaParams getParams() {
        return params;
    }

//	public int getTeamsInQueue() {
//		return teamsInQueue;
//	}
}
