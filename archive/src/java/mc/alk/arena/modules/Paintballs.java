package mc.alk.arena.modules;

import mc.alk.arena.objects.events.ArenaEventHandler;
import mc.alk.arena.objects.modules.ArenaModule;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * 
 * 
 * @author Nikolai
 */
public class Paintballs extends ArenaModule {
    
    String name = "Paintballs";
    String version = "1.0";
    EntityType paintballs;
    double damage;
    
    public Paintballs() {
        this(EntityType.SNOWBALL);
    }
    
    public Paintballs(EntityType paintballType) {
        this(paintballType, 20);
    }
    
    public Paintballs(EntityType paintballType, double damageAmount) {
        this.paintballs = paintballType;
        this.damage = damageAmount;
    }

    @ArenaEventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        e.getPlayer().sendMessage("Module: Paintballs");
    }

    @ArenaEventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        EntityType type = event.getDamager().getType();
        if (type == EntityType.SNOWBALL) {
            event.setDamage(damage);
        }        
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getVersion() {
        return this.version;
    }
    
}
