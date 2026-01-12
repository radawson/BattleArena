package mc.alk.arena.events.players;

import java.util.List;

import mc.alk.arena.events.CompetitionEvent;
import mc.alk.arena.objects.ArenaClass;

import org.bukkit.inventory.ItemStack;

/**
 * Event that is called when a player selects a class
 *
 */
public class ArenaPlayerClassSelectedEvent extends CompetitionEvent {

    ArenaClass arenaClass;
    List<ItemStack> items = null;

    public ArenaPlayerClassSelectedEvent(ArenaClass arenaClass) {
        this.arenaClass = arenaClass;
    }

    /**
     * Returns the arena class the player chose
     *
     * @return The arena class the player chose
     */
    public ArenaClass getArenaClass() {
        return arenaClass;
    }

    /**
     * Sets the arena class the player will have
     *
     * @param arenaClass The arenaClass you want the player to have
     */
    public void setArenaClass(ArenaClass arenaClass) {
        this.arenaClass = arenaClass;
    }

    public List<ItemStack> getItems() {
        return items;
    }

    public void setItems(List<ItemStack> items) {
        this.items = items;
    }
}
